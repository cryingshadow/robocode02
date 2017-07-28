package de.metro.robocode;

import robocode.*;
import robocode.util.Utils;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class TheBotOfOT extends AdvancedRobot {
    private enum Mode {
        SCANNING,
        TARGET
    }

    public static final int MAX_DISTANCE = 500;
    public static final int MAX_FIRING_STRENGTH = 10;
    public static final int TARGET_MODE_ITERATIONS = 10;

    private Mode mode;
    private List<ScannedRobot> knownRobots;
    private ScannedRobot targetedRobot;

    @Override
    public void run() {
        while ( true ) {
            scanForRobots();
            target( );
        }
    }

    private void target( ) {
        mode = Mode.TARGET;
        targetedRobot = null;

        for ( ScannedRobot robot : knownRobots ) {
            if ( targetedRobot == null || targetedRobot.getEnergy() < robot.getEnergy() ) {
                targetedRobot = robot;
            }
        }

        for ( int i = 0; i < TARGET_MODE_ITERATIONS; i++ ) {
            turnRight( 360 );
            ahead( Math.random( ) * MAX_DISTANCE );
        }
    }

    private void scanForRobots() {
        mode = Mode.SCANNING;
        knownRobots = new ArrayList<ScannedRobot>(  );
        turnRadarRight( 360 );
    }

    private double getDegrees( ) {
        return Math.random() * 360 - 180;
    }

    private void target(ScannedRobotEvent e) {
        double bulletPower = Math.min(3.0,getEnergy());
        double myX = getX();
        double myY = getY();
        double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
        double enemyX = getX() + e.getDistance() * Math.sin(absoluteBearing);
        double enemyY = getY() + e.getDistance() * Math.cos(absoluteBearing);
        double enemyHeading = e.getHeadingRadians();
        double enemyVelocity = e.getVelocity();


        double deltaTime = 0;
        double battleFieldHeight = getBattleFieldHeight(),
                battleFieldWidth = getBattleFieldWidth();
        double predictedX = enemyX, predictedY = enemyY;
        while((++deltaTime) * (20.0 - 3.0 * bulletPower) <
                Point2D.Double.distance(myX, myY, predictedX, predictedY)){
            predictedX += Math.sin(enemyHeading) * enemyVelocity;
            predictedY += Math.cos(enemyHeading) * enemyVelocity;
            if(	predictedX < 18.0
                    || predictedY < 18.0
                    || predictedX > battleFieldWidth - 18.0
                    || predictedY > battleFieldHeight - 18.0){
                predictedX = Math.min(Math.max(18.0, predictedX),
                        battleFieldWidth - 18.0);
                predictedY = Math.min(Math.max(18.0, predictedY),
                        battleFieldHeight - 18.0);
                break;
            }
        }
        double theta = Utils.normalAbsoluteAngle(Math.atan2(
                predictedX - getX(), predictedY - getY()));

        setTurnRadarRightRadians(
                Utils.normalRelativeAngle(absoluteBearing - getRadarHeadingRadians()));
        setTurnGunRightRadians(Utils.normalRelativeAngle(theta - getGunHeadingRadians()));
        fire(bulletPower);
    }

    @Override
    public void onScannedRobot( ScannedRobotEvent e ) {
        switch ( mode ) {
            case SCANNING:
                knownRobots.add( ScannedRobot.of( e ) );
            case TARGET:
                targetRobot( e );
        }
    }

    private void targetRobot( final ScannedRobotEvent e ) {
        if ( targetedRobot == null || targetedRobot.getName() == e.getName() ) {
            if ( e.getDistance( ) < 500 ) {
                target( e );
            } else {
                turnRight( e.getBearing( ) );
                ahead( e.getDistance( ) / 2 );
            }
        }
    }


    @Override
    public void onHitWall( HitWallEvent e ) {
        double turnAngle = e.getBearing( ) > 180 ? e.getBearing( ) - 180 : e.getBearing( ) + 180;
        turnLeft( turnAngle );
    }

    @Override
    public void onHitByBullet( HitByBulletEvent e ) {
        targetedRobot = ScannedRobot.ofHitByBulletEvent( e );
    }
}
