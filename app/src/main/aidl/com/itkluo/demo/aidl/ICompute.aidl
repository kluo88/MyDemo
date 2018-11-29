// ICompute.aidl
package com.itkluo.demo.aidl;

// Declare any non-default types here with import statements

interface ICompute {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);
	String strcat (String x,String y);
}
