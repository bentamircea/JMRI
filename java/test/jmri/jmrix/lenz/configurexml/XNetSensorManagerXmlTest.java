package jmri.jmrix.lenz.configurexml;

import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * XNetSensorManagerXmlTest.java
 *
 * Test for the XNetSensorManagerXml class
 *
 * @author   Paul Bender  Copyright (C) 2016
 */
public class XNetSensorManagerXmlTest {

    @Test
    public void testCtor(){
      Assert.assertNotNull("XNetSensorManagerXml constructor",new XNetSensorManagerXml());
    }

    @Before
    public void setUp() {
        JUnitUtil.setUp();
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }

}

