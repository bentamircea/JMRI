package jmri.jmrix.loconet;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        LocoNetThrottledTransmitterTest.class,
        jmri.jmrix.loconet.locostats.PackageTest.class,
        jmri.jmrix.loconet.sdf.PackageTest.class,
        jmri.jmrix.loconet.sdfeditor.PackageTest.class,
        jmri.jmrix.loconet.locomon.PackageTest.class,
        jmri.jmrix.loconet.soundloader.PackageTest.class,
        jmri.jmrix.loconet.spjfile.PackageTest.class,
        jmri.jmrix.loconet.messageinterp.PackageTest.class,
        SlotManagerTest.class,
        LocoNetSlotTest.class,
        LnOpsModeProgrammerTest.class,
        LocoNetMessageTest.class,
        LnTrafficControllerTest.class,
        LnTrafficRouterTest.class,
        LnPacketizerTest.class,
        LocoNetThrottleTest.class,
        LocoNetConsistTest.class,
        LnPowerManagerTest.class,
        LnTurnoutTest.class,
        LnTurnoutManagerTest.class,
        LnReporterTest.class,
        LnSensorTest.class,
        LnSensorAddressTest.class,
        LnSensorManagerTest.class,
        LnCommandStationTypeTest.class,
        LnDeferProgrammerTest.class,
        BundleTest.class,
        jmri.jmrix.loconet.pr3.PackageTest.class,
        jmri.jmrix.loconet.hexfile.PackageTest.class,
        jmri.jmrix.loconet.lnsvf2.PackageTest.class,
        jmri.jmrix.loconet.downloader.PackageTest.class,
        jmri.jmrix.loconet.configurexml.PackageTest.class,
        jmri.jmrix.loconet.clockmon.PackageTest.class,
        jmri.jmrix.loconet.cmdstnconfig.PackageTest.class,
        jmri.jmrix.loconet.duplexgroup.PackageTest.class,
        jmri.jmrix.loconet.locoid.PackageTest.class,
        jmri.jmrix.loconet.slotmon.PackageTest.class,
        jmri.jmrix.loconet.swing.PackageTest.class,
        jmri.jmrix.loconet.bdl16.PackageTest.class,
        jmri.jmrix.loconet.ds64.PackageTest.class,
        jmri.jmrix.loconet.se8.PackageTest.class,
        jmri.jmrix.loconet.pm4.PackageTest.class,
        jmri.jmrix.loconet.Intellibox.PackageTest.class,
        jmri.jmrix.loconet.bluetooth.PackageTest.class,
        jmri.jmrix.loconet.locobuffer.PackageTest.class,
        jmri.jmrix.loconet.locobufferii.PackageTest.class,
        jmri.jmrix.loconet.locobufferusb.PackageTest.class,
        jmri.jmrix.loconet.loconetovertcp.PackageTest.class,
        jmri.jmrix.loconet.ms100.PackageTest.class,
        jmri.jmrix.loconet.pr2.PackageTest.class,
        jmri.jmrix.loconet.uhlenbrock.PackageTest.class,
        jmri.jmrix.loconet.locormi.PackageTest.class,
        LnReporterManagerTest.class,
        jmri.jmrix.loconet.locoio.PackageTest.class,
        jmri.jmrix.loconet.locogen.PackageTest.class,
        LnNetworkPortControllerTest.class,
        LocoNetSystemConnectionMemoTest.class,
        LnPortControllerTest.class,
        LocoNetExceptionTest.class,
        LocoNetMessageExceptionTest.class,
        LnConnectionTypeListTest.class,
        LnConstantsTest.class,
        Ib1ThrottleManagerTest.class,
        Ib1ThrottleTest.class,
        Ib2ThrottleManagerTest.class,
        Ib2ThrottleTest.class,
        LNCPSignalMastTest.class,
        LnLightManagerTest.class,
        LnLightTest.class,
        LnMessageManagerTest.class,
        LnPr2ThrottleManagerTest.class,
        Pr2ThrottleTest.class,
        LnClockControlTest.class,
        LnProgrammerManagerTest.class,
        LnThrottleManagerTest.class,
        LocoNetConsistManagerTest.class,
        SE8cSignalHeadTest.class,
        UhlenbrockSlotManagerTest.class,
        UhlenbrockSlotTest.class,
        jmri.jmrix.loconet.streamport.PackageTest.class,
        CsOpSwAccessTest.class,
        LnPacketizerStrictTest.class,
})

/**
 * Tests for the jmri.jmrix.loconet package.
 *
 * @author	Bob Jacobsen Copyright 2001, 2003
 */
public class PackageTest  {
}
