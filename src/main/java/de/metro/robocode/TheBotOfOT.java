package de.metro.robocode;

import robocode.*;
import robocode.util.Utils;
import java.awt.geom.Point2D;

public class TheBotOfOT extends AdvancedRobot {

    public static final int MAX_DISTANCE = 500;
    public static final int MAX_FIRING_STRENGTH = 10;

    @Override
    public void run() {
        while ( true ) {
            turnRight( 360 );
            ahead( Math.random() * MAX_DISTANCE );
        }
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
        if (e.getDistance() < 500 ) {
            target(e);
        } else {
            turnRight( e.getBearing() );
            ahead( e.getDistance() / 2 );
        }
    }


    @Override
    public void onHitWall( HitWallEvent e ) {
        double turnAngle = e.getBearing( ) > 180 ? e.getBearing( ) - 180 : e.getBearing( ) + 180;
        turnLeft( turnAngle );
    }
}
