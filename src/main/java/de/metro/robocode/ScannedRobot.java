package de.metro.robocode;

import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;

/**
 * Created by th on 28.07.17.
 */
public class ScannedRobot {
    private final String name;
    private final double heading;
    private final double bearing;
    private final double energy;

    public ScannedRobot( final String name, final double heading, final double bearing, final double energy ) {
        this.name = name;
        this.heading = heading;
        this.bearing = bearing;
        this.energy = energy;
    }

    static ScannedRobot of( ScannedRobotEvent e ) {
        return new ScannedRobot( e.getName(), e.getHeading(), e.getBearing(), e.getEnergy() );
    }

    static ScannedRobot ofHitByBulletEvent( HitByBulletEvent e ) {
        return new ScannedRobot( e.getName(), e.getHeading(), e.getBearing(), 0 );
    }

    public String getName( ) {
        return name;
    }

    public double getHeading( ) {
        return heading;
    }

    public double getBearing( ) {
        return bearing;
    }

    public double getEnergy( ) {
        return energy;
    }
}
