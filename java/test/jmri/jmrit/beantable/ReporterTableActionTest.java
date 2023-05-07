package jmri.jmrit.beantable;

import javax.annotation.Nonnull;
import javax.swing.JFrame;
import javax.swing.JTextField;

import jmri.*;
import jmri.jmrix.internal.InternalReporterManager;
import jmri.jmrix.internal.InternalSystemConnectionMemo;
import jmri.util.JUnitUtil;
import jmri.util.swing.JemmyUtil;

import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

import org.netbeans.jemmy.operators.*;
import org.netbeans.jemmy.util.NameComponentChooser;

/**
 *
 * @author Paul Bender Copyright (C) 2017
 */
public class ReporterTableActionTest extends AbstractTableActionBase<Reporter> {

    @Test
    public void testCTor() {
        Assert.assertNotNull("exists", a);
    }

    @Override
    public String getTableFrameName() {
        return Bundle.getMessage("TitleReporterTable");
    }

    @Override
    @Test
    public void testGetClassDescription() {
        Assert.assertEquals("Reporter Table Action class description", "Reporter Table", a.getClassDescription());
    }

    /**
     * Check the return value of includeAddButton. The table generated by this
     * action includes an Add Button.
     */
    @Override
    @Test
    public void testIncludeAddButton() {
        Assert.assertTrue("Default include add button", a.includeAddButton());
    }

    
    @Test
    @Override
    @DisabledIfSystemProperty(named = "java.awt.headless", matches = "true")
    public void testAddButton() {
        Assert.assertTrue(a.includeAddButton());
        a.actionPerformed(null);
        JFrame f = JFrameOperator.waitJFrame(getTableFrameName(), true, true);

        // find the "Add... " button and press it.
        jmri.util.swing.JemmyUtil.pressButton(new JFrameOperator(f), Bundle.getMessage("ButtonAdd"));
        new org.netbeans.jemmy.QueueTool().waitEmpty();
        JFrame f1 = JFrameOperator.waitJFrame(getAddFrameName(), true, true);
        JemmyUtil.pressButton(new JFrameOperator(f1), Bundle.getMessage("ButtonClose")); // not sure why this is close in this frame.
        JUnitUtil.dispose(f1);
        JUnitUtil.dispose(f);
    }

    @Override
    public String getAddFrameName() {
        return Bundle.getMessage("TitleAddReporter");
    }

    @Test
    @Override
    @Disabled("No Edit button on Reporter table")
    public void testEditButton() {
    }

    @Test
    @DisabledIfSystemProperty(named = "java.awt.headless", matches = "true")
    public void testAddFailureCreate() {
        
        InstanceManager.setDefault(ReporterManager.class, new AlwaysExceptionCreateNewReporter());
        
        a = new ReporterTableAction();
        Assert.assertTrue(a.includeAddButton());
        
        a.actionPerformed(null);
        JFrame f = JFrameOperator.waitJFrame(getTableFrameName(), true, true);
        // find the "Add... " button and press it.
        JemmyUtil.pressButton(new JFrameOperator(f), Bundle.getMessage("ButtonAdd"));
        
        JFrame f1 = JFrameOperator.waitJFrame(getAddFrameName(), true, true);
        JTextField hwAddressField = JTextFieldOperator.findJTextField(f1, new NameComponentChooser("hwAddressTextField"));
        Assert.assertNotNull("hwAddressTextField", hwAddressField);
        // set to "1"
        new JTextFieldOperator(hwAddressField).setText("1");
        Thread add1 = JemmyUtil.createModalDialogOperatorThread(
            Bundle.getMessage("ErrorBeanCreateFailed","Reporter", "IR1"), Bundle.getMessage("ButtonOK"));  // NOI18N
        
        //and press create
        JemmyUtil.pressButton(new JFrameOperator(f1), Bundle.getMessage("ButtonCreate"));
        JUnitUtil.waitFor(()->{return !(add1.isAlive());}, "dialog finished");  // NOI18N
        
        JemmyUtil.pressButton(new JFrameOperator(f1), Bundle.getMessage("ButtonClose")); // not sure why this is close in this frame.
        JUnitUtil.dispose(f1);
        JUnitUtil.dispose(f);
    }

    private static class AlwaysExceptionCreateNewReporter extends InternalReporterManager {

        AlwaysExceptionCreateNewReporter() {
            super(InstanceManager.getDefault(InternalSystemConnectionMemo.class));
        }

        /** {@inheritDoc} */
        @Override
        @Nonnull
        protected Reporter createNewReporter(@Nonnull String systemName, String userName) throws IllegalArgumentException {
            throw new IllegalArgumentException("createNewReporter Exception Text");
        }
        
    }

    @Override
    @BeforeEach
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetProfileManager();
        helpTarget = "package.jmri.jmrit.beantable.ReporterTable";
        a = new ReporterTableAction();
    }

    @Override
    @AfterEach
    public void tearDown() {
        JUnitUtil.tearDown();
    }

    // private final static Logger log = LoggerFactory.getLogger(ReporterTableActionTest.class);
}
