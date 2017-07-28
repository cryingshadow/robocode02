package de.metro.robocode;

import robocode.*;

public class TheBotOfOT extends Robot {
    public static final int MAX_DISTANCE = 750;

    @Override
    public void run() {
        while ( true ) {
            turnGunRight( getDegrees( ) );
            turnRight( getDegrees( ) );
            ahead( Math.random() * MAX_DISTANCE );
        }
    }

    private double getDegrees( ) {
        return Math.random() * 360 - 180;
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        if ( e.getDistance() < 5) {
            fire(5);
        }
    }

    public void onHitByBullet(HitByBulletEvent e) {
        turnLeft(90 - e.getBearing());
    }

}
