package com.logicalgrape.quadcopter;

public class Quadcopter {

    private static Quadcopter sQuadcopter;

    public static void main(String[] args) {
        Quadcopter.getInstance().start();
    }

    public static Quadcopter getInstance() {
        if (sQuadcopter == null) {
            sQuadcopter = new Quadcopter();
        }
        return sQuadcopter;
    }

    private Quadcopter() {
    }

    public void start() {


    }

    public void stop() {


    }

}