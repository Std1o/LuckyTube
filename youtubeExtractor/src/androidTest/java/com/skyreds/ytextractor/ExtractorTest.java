package com.skyreds.ytextractor;

import junit.framework.Test;
import junit.framework.TestSuite;

import android.test.suitebuilder.TestSuiteBuilder;


public class ExtractorTest extends TestSuite {

    public static Test suite() {
        return new TestSuiteBuilder(ExtractorTest.class).includeAllPackagesUnderHere().build();
    }
}



