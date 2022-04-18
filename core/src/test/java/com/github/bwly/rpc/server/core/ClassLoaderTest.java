package com.github.bwly.rpc.server.core;

public class ClassLoaderTest {
    // private static int a = 1; // version = 1;
    private static final int version = getVersion();
    private static final int a = 1; // version = 1;
    // private static int a = 1; // version = 0;


    static {
        System.out.println("ClassLoaderTest static block");
//        a = 2;
    }
    private static int getVersion() {
        System.out.println("getVersion");
        return a;
    }

    public static int getV() {
        return version;
    }

    public static void main(String[] args) {
        System.out.println(ClassLoaderTest.getV());
    }
}
