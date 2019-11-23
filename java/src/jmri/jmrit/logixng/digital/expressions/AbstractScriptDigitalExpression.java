package jmri.jmrit.logixng.digital.expressions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.util.Locale;
import jmri.InstanceManager;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.DigitalExpression;
import jmri.jmrit.logixng.DigitalExpressionManager;
import jmri.jmrit.logixng.FemaleSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for scripts that implements a digital expression.
 * <P>
 * A script that extends this class must implement the method evaluate().
 * It may also implement the methods setup(), registerScriptListeners(),
 * unregisterScriptListeners(), reset() and disposeMe().
 * <P>
 * If the script needs to run the ConditionalNG that this expression belongs to,
 * it does that by calling the method propertyChange().
 * 
 * @author Daniel Bergqvist Copyright 2019
 */
public abstract class AbstractScriptDigitalExpression extends AbstractDigitalExpression
        implements PropertyChangeListener, VetoableChangeListener {

    private AbstractScriptDigitalExpression _template;
    private boolean _listenersAreRegistered = false;
    private final DigitalExpression _parentDigitalExpression;

//    public AbstractScriptDigitalExpression(String sys, String user)
    public AbstractScriptDigitalExpression(DigitalExpression de)
            throws BadUserNameException, BadSystemNameException {
        // This bean is never stored in a manager and
        // its system name nor user name is never used.
        super("IQDE0", null);
//        super(sys, user);
        _parentDigitalExpression = de;
    }
    
    private AbstractScriptDigitalExpression(AbstractScriptDigitalExpression template) {
        super(InstanceManager.getDefault(DigitalExpressionManager.class).getAutoSystemName(), null);
        _template = template;
        if (_template == null) throw new NullPointerException();    // Temporary solution to make variable used.
//        _parentDigitalExpression = null;
        throw new UnsupportedOperationException("Not supported");
    }
    
    /** {@inheritDoc} */
    @Override
    public Base getNewObjectBasedOnTemplate() {
        throw new UnsupportedOperationException("Method not implemented by script");
//        return new AbstractScriptDigitalExpression(this);
    }
    
    @Override
    public void vetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException {
    }
    
    /** {@inheritDoc} */
    @Override
    public Category getCategory() {
        return Category.ITEM;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExternal() {
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    public void reset() {
        // Do nothing.
    }

    @Override
    public FemaleSocket getChild(int index) throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public String getShortDescription(Locale locale) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public String getLongDescription(Locale locale) {
        throw new UnsupportedOperationException("Not supported");
    }
    
    /** {@inheritDoc} */
    @Override
    public void setup() {
    }
    
    /**
     * Register listeners if this object needs that.
     * The script can override this method to register any listener it needs.
     * <P>
     * This method is only called if listeners are not registered.
     */
    protected void registerScriptListeners() {
    }
    
    /**
     * Unregister listeners if this object needs that.
     * The script can override this method to unregister any listener it needs.
     * <P>
     * This method is only called if listeners are registered.
     */
    protected void unregisterScriptListeners() {
    }
    
    /** {@inheritDoc} */
    @Override
    public void registerListenersForThisClass() {
        if (!_listenersAreRegistered) {
            registerScriptListeners();
            _listenersAreRegistered = true;
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void unregisterListenersForThisClass() {
        if (_listenersAreRegistered) {
            unregisterScriptListeners();
            _listenersAreRegistered = false;
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        _parentDigitalExpression.getConditionalNG().execute();
    }
    
    /** {@inheritDoc} */
    @Override
    public void disposeMe() {
    }
    
    
    private final static Logger log = LoggerFactory.getLogger(AbstractScriptDigitalExpression.class);
    
}
