package de.metro.robocode;

import robocode.*;
import robocode.util.Utils;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class TheBotOfOT extends AdvancedRobot {
    public static final int MAX_DISTANCE = 500;
    public static final int MAX_FIRING_STRENGTH = 10;


    @Override
    public void run() {
        while ( true ) {
            if(!reactingOnRam) {
                turnRight( 360 );
                ahead( Math.random( ) * MAX_DISTANCE );
            }
        }
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
        targetRobot( e );
    }

    private void targetRobot( final ScannedRobotEvent e ) {
        if ( e.getDistance( ) < 1000 ) {
            target( e );
        } else {
            turnRight( e.getBearing( ) );
            ahead( e.getDistance( ) / 2 );
        }
    }

    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        stop();
        ahead( 50 );
        turnLeft( 90 );
        ahead( 50 );
        resume();
    }

    @Override
    public void onHitWall( HitWallEvent e ) {
        turnLeft( 180 );
    }

    private boolean reactingOnRam = false;

    @Override
    public void onHitRobot( HitRobotEvent e) {
        if (!reactingOnRam) {
            reactingOnRam = true;
            stop( );
            turnGunRight( getHeading() - getGunHeading() );
            turnRight( e.getBearing( ) );
            ahead( 20 );
            fire( e.getEnergy() );
            back( 40 );
            resume( );
            reactingOnRam = false;
        }
    }

}
