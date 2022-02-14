package jmri.server.json.throttle;

import static jmri.server.json.JSON.ADDRESS;
import static jmri.server.json.JSON.F;
import static jmri.server.json.JSON.FORWARD;
import static jmri.server.json.JSON.IS_LONG_ADDRESS;
import static jmri.server.json.JSON.NAME;
import static jmri.server.json.JSON.STATUS;
import static jmri.server.json.roster.JsonRoster.ROSTER_ENTRY;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletResponse;

import jmri.BasicRosterEntry;
import jmri.DccLocoAddress;
import jmri.DccThrottle;
import jmri.InstanceManager;
import jmri.LocoAddress;
import jmri.Throttle;
import jmri.ThrottleListener;
import jmri.jmrit.roster.Roster;
import jmri.server.json.JSON;
import jmri.server.json.JsonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonThrottle implements ThrottleListener, PropertyChangeListener {

    /**
     * Token for type for throttle status messages.
     * <p>
     * {@value #THROTTLE}
     */
    public static final String THROTTLE = "throttle"; // NOI18N
    /**
     * {@value #RELEASE}
     */
    public static final String RELEASE = "release"; // NOI18N
    /**
     * {@value #ESTOP}
     */
    public static final String ESTOP = "eStop"; // NOI18N
    /**
     * {@value #IDLE}
     */
    public static final String IDLE = "idle"; // NOI18N
    /**
     * {@value #SPEED_STEPS}
     */
    public static final String SPEED_STEPS = "speedSteps"; // NOI18N
    /**
     * Used to notify clients of the number of clients controlling the same
     * throttle.
     * <p>
     * {@value #CLIENTS}
     */
    public static final String CLIENTS = "clients"; // NOI18N
    private Throttle throttle;
    private int speedSteps = 1; // Number of speed steps.
    private DccLocoAddress address = null;
    private static final Logger log = LoggerFactory.getLogger(JsonThrottle.class);

    protected JsonThrottle(DccLocoAddress address, JsonThrottleSocketService server) {
        this.address = address;
    }

    /**
     * Creates a new JsonThrottle or returns an existing one if the request is
     * for an existing throttle.
     * <p>
     * data can contain either a string {@link jmri.server.json.JSON#ID} node
     * containing the ID of a {@link jmri.jmrit.roster.RosterEntry} or an
     * integer {@link jmri.server.json.JSON#ADDRESS} node. If data contains an
     * ADDRESS, the ID node is ignored. The ADDRESS may be accompanied by a
     * boolean {@link jmri.server.json.JSON#IS_LONG_ADDRESS} node specifying the
     * type of address, if IS_LONG_ADDRESS is not specified, the inverse of
     * {@link jmri.ThrottleManager#canBeShortAddress(int)} is used as the "best
     * guess" of the address length.
     *
     * @param throttleId The client's identity token for this throttle
     * @param data       JSON object containing either an ADDRESS or an ID
     * @param server     The server requesting this throttle on behalf of a
     *                   client
     * @param id         message id set by client
     * @return The throttle
     * @throws jmri.server.json.JsonException if unable to get the requested
     *                                        {@link jmri.Throttle}
     */
    public static JsonThrottle getThrottle(String throttleId, JsonNode data, JsonThrottleSocketService server, int id)
            throws JsonException {
        JsonThrottle throttle = null;
        DccLocoAddress address = null;
        BasicRosterEntry entry = null;
        Locale locale = server.getConnection().getLocale();
        JsonThrottleManager manager = InstanceManager.getDefault(JsonThrottleManager.class);
        if (!data.path(ADDRESS).isMissingNode()) {
            if (manager.canBeLongAddress(data.path(ADDRESS).asInt()) ||
                    manager.canBeShortAddress(data.path(ADDRESS).asInt())) {
                address = new DccLocoAddress(data.path(ADDRESS).asInt(),
                        data.path(IS_LONG_ADDRESS).asBoolean(!manager.canBeShortAddress(data.path(ADDRESS).asInt())));
            } else {
                log.warn("Address \"{}\" is not a valid address.", data.path(ADDRESS).asInt());
                throw new JsonException(HttpServletResponse.SC_BAD_REQUEST,
                        Bundle.getMessage(locale, "ErrorThrottleInvalidAddress", data.path(ADDRESS).asInt()), id); // NOI18N
            }
        } else if (!data.path(ROSTER_ENTRY).isMissingNode()) {
            entry = Roster.getDefault().getEntryForId(data.path(ROSTER_ENTRY).asText());
            if (entry != null) {
                address = entry.getDccLocoAddress();
            } else {
                log.warn("Roster entry \"{}\" does not exist.", data.path(ROSTER_ENTRY).asText());
                throw new JsonException(HttpServletResponse.SC_NOT_FOUND,
                        Bundle.getMessage(locale, "ErrorThrottleRosterEntry", data.path(ROSTER_ENTRY).asText()), id); // NOI18N
            }
        } else {
            log.warn("No address specified");
            throw new JsonException(HttpServletResponse.SC_BAD_REQUEST,
                    Bundle.getMessage(locale, "ErrorThrottleNoAddress"), id); // NOI18N
        }
        if (manager.containsKey(address)) {
            throttle = manager.get(address);
            manager.put(throttle, server);
            throttle.sendMessage(server.getConnection().getObjectMapper().createObjectNode().put(CLIENTS,
                    manager.getServers(throttle).size()));
        } else {
            throttle = new JsonThrottle(address, server);
            if (entry!=null) {
                if (!manager.requestThrottle(entry, throttle)) {
                    log.error("Unable to get rostered throttle for \"{}\".", entry.getId());
                    throw new JsonException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Bundle
                            .getMessage(server.getConnection().getLocale(), "ErrorThrottleUnableToGetThrottle", entry.getId()),
                            id);
                }
            } else {
                if (!manager.requestThrottle(address, throttle)) {
                    log.error("Unable to get throttle for \"{}\".", address);
                    throw new JsonException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Bundle
                            .getMessage(server.getConnection().getLocale(), "ErrorThrottleUnableToGetThrottle", address),
                            id);
                }

            }
            manager.put(address, throttle);
            manager.put(throttle, server);
        }
        return throttle;
    }

    public void close(JsonThrottleSocketService server, boolean notifyClient) {
        if (this.throttle != null) {
            List<JsonThrottleSocketService> servers =
                    InstanceManager.getDefault(JsonThrottleManager.class).getServers(this);
            if (servers.size() == 1 && servers.get(0).equals(server)) {
                this.throttle.setSpeedSetting(0);
            }
            this.release(server, notifyClient);
        }
    }

    public void release(JsonThrottleSocketService server, boolean notifyClient) {
        JsonThrottleManager manager = InstanceManager.getDefault(JsonThrottleManager.class);
        ObjectMapper mapper = server.getConnection().getObjectMapper();
        if (this.throttle != null) {
            if (manager.getServers(this).size() == 1) {
                this.throttle.release(this);
                this.throttle.removePropertyChangeListener(this);
                this.throttle = null;
            }
            if (notifyClient) {
                this.sendMessage(mapper.createObjectNode().putNull(RELEASE), server);
            }
        }
        manager.remove(this, server);
        if (manager.getServers(this).isEmpty()) {
            // Release address-based reference to this throttle if there are no
            // servers using it
            // so that when the server releases its reference, this throttle can
            // be garbage collected
            manager.remove(this.address);
        } else {
            this.sendMessage(mapper.createObjectNode().put(CLIENTS, manager.getServers(this).size()));
        }
    }

    public void onMessage(Locale locale, JsonNode data, JsonThrottleSocketService server) {
        data.fields().forEachRemaining((entry) -> {
            String k = entry.getKey();
            JsonNode v = entry.getValue();
            switch (k) {
                case ESTOP:
                    this.throttle.setSpeedSetting(-1);
                    return; // stop processing any commands that may conflict
                            // with ESTOP
                case IDLE:
                    this.throttle.setSpeedSetting(0);
                    break;
                case JSON.SPEED:
                    this.throttle.setSpeedSetting((float) v.asDouble());
                    break;
                case FORWARD:
                    this.throttle.setIsForward(v.asBoolean());
                    break;
                case RELEASE:
                    server.release(this);
                    break;
                case STATUS:
                    this.sendStatus(server);
                    break;
                case ADDRESS:
                case NAME:
                case THROTTLE:
                case ROSTER_ENTRY:
                    // no action for address, name, or throttle property
                    break;
                default:
                    for ( int i = 0; i< this.throttle.getFunctions().length; i++ ) {
                        if (k.equals(jmri.Throttle.getFunctionString(i))) {
                            this.throttle.setFunction(i,v.asBoolean());
                            break;
                        }
                    }
                    log.debug("Unknown field \"{}\": \"{}\"", k, v);
                    // do not error on unknown or unexpected items, since a
                    // following item may be an ESTOP and we always want to
                    // catch those
                    break;
            }
        });
    }

    public void sendMessage(ObjectNode data) {
        new ArrayList<>(InstanceManager.getDefault(JsonThrottleManager.class).getServers(this)).stream()
                .forEach(server -> this.sendMessage(data, server));
    }

    public void sendMessage(ObjectNode data, JsonThrottleSocketService server) {
        try {
            // .deepCopy() ensures each server gets a unique (albeit identical)
            // message
            // to allow each server to modify the message as needed by its
            // client
            server.sendMessage(this, data.deepCopy());
        } catch (IOException ex) {
            this.close(server, false);
            log.warn("Unable to send message, closing connection: {}", ex.getMessage());
            try {
                server.getConnection().close();
            } catch (IOException e1) {
                log.warn("Unable to close connection.", e1);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        ObjectNode data = InstanceManager.getDefault(JsonThrottleManager.class).getObjectMapper().createObjectNode();
        String property = evt.getPropertyName();
        if (property.equals(Throttle.SPEEDSETTING)) { // NOI18N
            data.put(JSON.SPEED, ((Number) evt.getNewValue()).floatValue());
        } else if (property.equals(Throttle.ISFORWARD)) { // NOI18N
            data.put(FORWARD, ((Boolean) evt.getNewValue()));
        } else if (property.startsWith(F) && !property.contains("Momentary")) { // NOI18N
            data.put(property, ((Boolean) evt.getNewValue()));
        }
        if (data.size() > 0) {
            this.sendMessage(data);
        }
    }

    @Override
    public void notifyThrottleFound(DccThrottle throttle) {
        log.debug("Found throttle {}", throttle.getLocoAddress());
        this.throttle = throttle;
        throttle.addPropertyChangeListener(this);
        this.speedSteps = throttle.getSpeedStepMode().numSteps;
        this.sendStatus();
    }

    @Override
    public void notifyFailedThrottleRequest(LocoAddress address, String reason) {
        JsonThrottleManager manager = InstanceManager.getDefault(JsonThrottleManager.class);
        for (JsonThrottleSocketService server : manager.getServers(this)
                .toArray(new JsonThrottleSocketService[manager.getServers(this).size()])) {
            // TODO: use message id correctly
            this.sendErrorMessage(new JsonException(512, Bundle.getMessage(server.getConnection().getLocale(),
                    "ErrorThrottleRequestFailed", address, reason), 0), server);
            server.release(this);
        }
    }

    /**
     * No steal or share decisions made locally
     * <p>
     * {@inheritDoc}
     */
    @Override
    public void notifyDecisionRequired(jmri.LocoAddress address, DecisionType question) {
        // no steal or share decisions made locally
    }

    private void sendErrorMessage(JsonException message, JsonThrottleSocketService server) {
        try {
            server.getConnection().sendMessage(message.getJsonMessage(), message.getId());
        } catch (IOException e) {
            log.warn("Unable to send message, closing connection. ", e);
            try {
                server.getConnection().close();
            } catch (IOException e1) {
                log.warn("Unable to close connection.", e1);
            }
        }
    }

    private void sendStatus() {
        if (this.throttle != null) {
            this.sendMessage(this.getStatus());
        }
    }

    protected void sendStatus(JsonThrottleSocketService server) {
        if (this.throttle != null) {
            this.sendMessage(this.getStatus(), server);
        }
    }

    private ObjectNode getStatus() {
        ObjectNode data = InstanceManager.getDefault(JsonThrottleManager.class).getObjectMapper().createObjectNode();
        data.put(ADDRESS, this.throttle.getLocoAddress().getNumber());
        data.put(JSON.SPEED, this.throttle.getSpeedSetting());
        data.put(FORWARD, this.throttle.getIsForward());
        for ( int i = 0; i< this.throttle.getFunctions().length; i++ ) {
            data.put(Throttle.getFunctionString(i), this.throttle.getFunction(i));
        }
        data.put(SPEED_STEPS, this.speedSteps);
        data.put(CLIENTS, InstanceManager.getDefault(JsonThrottleManager.class).getServers(this).size());
        if (this.throttle.getRosterEntry() != null) {
            data.put(ROSTER_ENTRY, this.throttle.getRosterEntry().getId());
        }
        return data;
    }

    /**
     * Get the Throttle this JsonThrottle is a proxy for.
     *
     * @return the throttle or null if no throttle is set
     */
    // package private
    Throttle getThrottle() {
        return this.throttle;
    }
}
