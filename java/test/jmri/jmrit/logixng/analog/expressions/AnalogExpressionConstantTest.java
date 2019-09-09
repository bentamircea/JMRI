package jmri.jmrit.logixng.analog.expressions;

import java.util.Locale;
import jmri.InstanceManager;
import jmri.Memory;
import jmri.MemoryManager;
import jmri.jmrit.logixng.AnalogActionManager;
import jmri.jmrit.logixng.AnalogExpressionManager;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.ConditionalNG;
import jmri.jmrit.logixng.DigitalActionBean;
import jmri.jmrit.logixng.DigitalActionManager;
import jmri.jmrit.logixng.LogixNG;
import jmri.jmrit.logixng.LogixNG_Manager;
import jmri.jmrit.logixng.MaleSocket;
import jmri.jmrit.logixng.SocketAlreadyConnectedException;
import jmri.jmrit.logixng.analog.actions.AnalogActionMemory;
import jmri.jmrit.logixng.digital.actions.DoAnalogAction;
import jmri.jmrit.logixng.implementation.DefaultConditionalNG;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test AnalogExpressionConstant
 * 
 * @author Daniel Bergqvist 2018
 */
public class AnalogExpressionConstantTest extends AbstractAnalogExpressionTestBase {

    @Test
    public void testCtor() {
        Assert.assertTrue("object exists", _base != null);
        
        AnalogExpressionConstant expression2;
        
        expression2 = new AnalogExpressionConstant("IQAE11");
        Assert.assertNotNull("object exists", expression2);
        Assert.assertEquals("Username matches", null, expression2.getUserName());
        Assert.assertEquals("String matches", "Get constant value 0", expression2.getLongDescription(Locale.ENGLISH));
        
        expression2 = new AnalogExpressionConstant("IQAE11", "My constant value");
        Assert.assertNotNull("object exists", expression2);
        Assert.assertEquals("Username matches", "My constant value", expression2.getUserName());
        Assert.assertEquals("String matches", "Get constant value 0", expression2.getLongDescription(Locale.ENGLISH));
        
        expression2 = new AnalogExpressionConstant("IQAE11");
        expression2.setValue(12.34);
        Assert.assertNotNull("object exists", expression2);
        Assert.assertEquals("Username matches", null, expression2.getUserName());
        Assert.assertEquals("String matches", "Get constant value 12.34", expression2.getLongDescription(Locale.ENGLISH));
        
        expression2 = new AnalogExpressionConstant("IQAE11", "My memory");
        expression2.setValue(98.76);
        Assert.assertNotNull("object exists", expression2);
        Assert.assertEquals("Username matches", "My memory", expression2.getUserName());
        Assert.assertEquals("String matches", "Get constant value 98.76", expression2.getLongDescription(Locale.ENGLISH));
        
        // Test template
        expression2 = (AnalogExpressionConstant)_base.getNewObjectBasedOnTemplate("IQAE12");
        Assert.assertNotNull("object exists", expression2);
        Assert.assertNull("Username is null", expression2.getUserName());
//        Assert.assertTrue("Username matches", "My memory".equals(expression2.getUserName()));
        Assert.assertEquals("String matches", "Get constant value 10.2", expression2.getLongDescription(Locale.ENGLISH));
        
        boolean thrown = false;
        try {
            // Illegal system name
            new AnalogExpressionConstant("IQA55:12:XY11");
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        Assert.assertTrue("Expected exception thrown", thrown);
        
        thrown = false;
        try {
            // Illegal system name
            new AnalogExpressionConstant("IQA55:12:XY11", "A name");
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        Assert.assertTrue("Expected exception thrown", thrown);
    }
    
    @Test
    public void testEvaluate() throws SocketAlreadyConnectedException, SocketAlreadyConnectedException {
        AnalogExpressionConstant expression = (AnalogExpressionConstant)_base;
        expression.setValue(0.0d);
        Assert.assertTrue("Evaluate matches", 0.0d == expression.evaluate());
        expression.setValue(10.0d);
        Assert.assertTrue("Evaluate matches", 10.0d == expression.evaluate());
        expression.reset();
    }
    
    @Test
    public void testEvaluateAndAction() throws SocketAlreadyConnectedException, SocketAlreadyConnectedException {
        
        AnalogExpressionConstant expression = (AnalogExpressionConstant)_base;
        
        LogixNG logixNG = InstanceManager.getDefault(LogixNG_Manager.class).createLogixNG("A logixNG");
        ConditionalNG conditionalNG = new DefaultConditionalNG(logixNG.getSystemName()+":1");
        
        logixNG.addConditionalNG(conditionalNG);
        logixNG.activateLogixNG();
        
        DigitalActionBean actionDoAnalog = new DoAnalogAction();
        MaleSocket socketDoAnalog = InstanceManager.getDefault(DigitalActionManager.class).registerAction(actionDoAnalog);
        conditionalNG.getChild(0).connect(socketDoAnalog);
        
        MaleSocket socketExpression = InstanceManager.getDefault(AnalogExpressionManager.class).registerExpression(expression);
        socketDoAnalog.getChild(0).connect(socketExpression);
        
        Memory _memoryOut = InstanceManager.getDefault(MemoryManager.class).provide("IM2");
        _memoryOut.setValue(0.0);
        AnalogActionMemory actionMemory = new AnalogActionMemory("IQAA1");
        actionMemory.setMemory(_memoryOut);
        MaleSocket socketAction = InstanceManager.getDefault(AnalogActionManager.class).registerAction(actionMemory);
        socketDoAnalog.getChild(1).connect(socketAction);
/*        
        // The action is not yet executed so the double should be 0.0
        Assert.assertTrue("memory is 0.0", 0.0 == (Double)_memoryOut.getValue());
        // Set the value of the memory. This should not execute the conditional.
        _memory.setValue(1.0);
        // The conditionalNG is not yet enabled so it shouldn't be executed.
        // So the memory should be 0.0
        Assert.assertTrue("memory is 0.0", 0.0 == (Double)_memoryOut.getValue());
        // Set the value of the memory. This should not execute the conditional.
        _memory.setValue(2.0);
        // Enable the conditionalNG and all its children.
        conditionalNG.setEnabled(true);
        // The action is not yet executed so the memory should be 0.0
        Assert.assertTrue("memory is 0.0", 0.0 == (Double)_memoryOut.getValue());
        // Set the value of the memory. This should execute the conditional.
        _memory.setValue(3.0);
        // The action should now be executed so the memory should be 3.0
        Assert.assertTrue("memory is 3.0", 3.0 == (Double)_memoryOut.getValue());
        // Disable the conditionalNG and all its children.
        conditionalNG.setEnabled(false);
        // The action is not yet executed so the memory should be 0.0
        Assert.assertTrue("memory is 0.0", 3.0 == (Double)_memoryOut.getValue());
        // Set the value of the memory. This should not execute the conditional.
        _memory.setValue(4.0);
        // The action should not be executed so the memory should still be 3.0
        Assert.assertTrue("memory is 3.0", 3.0 == (Double)_memoryOut.getValue());
        // Unregister listeners. This should do nothing since the listeners are
        // already unregistered.
        expression.unregisterListeners();
        // The memory should be 3.0
        Assert.assertTrue("memory is 0.0", 3.0 == (Double)_memoryOut.getValue());
        // Set the value of the memory. This should not execute the conditional.
        _memory.setValue(5.0);
        // The action should not be executed so the memory should still be 3.0
        Assert.assertTrue("memory is 3.0", 3.0 == (Double)_memoryOut.getValue());
        
        // Test register listeners when there is no memory.
        expression.setMemory((Memory)null);
        expression.registerListeners();
*/
    }
    
    @Test
    public void testCategory() {
        Assert.assertTrue("Category matches", Category.ITEM == _base.getCategory());
    }
    
    @Test
    public void testIsExternal() {
        Assert.assertTrue("is external", _base.isExternal());
    }
    
    @Test
    public void testShortDescription() {
        Assert.assertEquals("String matches", "Get constant value 10.2", _base.getShortDescription(Locale.ENGLISH));
    }
    
    @Test
    public void testLongDescription() {
        Assert.assertEquals("String matches", "Get constant value 10.2", _base.getLongDescription(Locale.ENGLISH));
    }
    
    @Test
    public void testChild() {
        Assert.assertTrue("Num children is zero", 0 == _base.getChildCount());
        boolean hasThrown = false;
        try {
            _base.getChild(0);
        } catch (UnsupportedOperationException ex) {
            hasThrown = true;
            Assert.assertTrue("Error message is correct", "Not supported.".equals(ex.getMessage()));
        }
        Assert.assertTrue("Exception is thrown", hasThrown);
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initInternalTurnoutManager();
        JUnitUtil.initMemoryManager();
        _base = new AnalogExpressionConstant("IQAE321", "AnalogIO_Constant");
        ((AnalogExpressionConstant)_base).setValue(10.2);
    }

    @After
    public void tearDown() {
        _base.dispose();
        JUnitUtil.tearDown();
    }
    
}
