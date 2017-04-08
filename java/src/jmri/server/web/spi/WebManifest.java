package jmri.server.web.spi;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

/**
 * Provide integration for the JMRI web services so servlets can visually
 * integrate into the JMRI web site.
 *
 * @author Randall Wood (C) 2016
 */
public interface WebManifest {

    /**
     * Get the navigation menu items that provide access to the servlet
     * associated with the manifest.
     *
     * @param locale the client's locale
     * @return a set of menu items; provide an empty set if the item should not
     *         be in the navigation menu
     */
    @Nonnull
    public Set<WebMenuItem> getNavigationMenuItems(Locale locale);

    /**
     * Get any scripts the servlet associated with the manifest requires in the
     * order required.
     *
     * @return a set of script URLs; provide an empty set if the item needs no
     *         scripts
     */
    @Nonnull
    public List<String> getScripts();

    /**
     * Get any CSS style sheets the servlet associated with the manifest
     * requires in the order required.
     *
     * @return a set of style sheet URLs; provide an empty set if the item needs
     *         no style sheets
     */
    @Nonnull
    public List<String> getStyles();

    /**
     * Get the Angular dependencies required by the servlet associated with the
     * manifest.
     *
     * @return an ordered list of angular dependencies
     */
    @Nonnull
    public List<String> getAngularDependencies();

    /**
     * Get the Angular routes supported by the servlet associated with the
     * manifest.
     *
     * @return a map of angular path to angular routing instructions
     */
    @Nonnull
    public Map<String, String> getAngularRoutes();
}
