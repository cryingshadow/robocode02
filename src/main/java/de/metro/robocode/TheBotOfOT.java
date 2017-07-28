package de.metro.robocode;

import robocode.*;
import robocode.util.Utils;

import java.awt.*;
import java.awt.geom.Point2D;

public class TheBotOfOT extends AdvancedRobot {
    private enum Mode {
        DEFAULT( Color.BLUE ),
        RAM( Color.RED ),
        HIT( Color.YELLOW );

        private final Color color;

        Mode( Color color ) {
            this.color = color;
        }

        public Color getColor( ) {
            return color;
        }
    }

    public static final int MAX_DISTANCE = 500;
    public static final int MAX_FIRING_STRENGTH = 10;

    private Mode mode;


    @Override
    public void run( ) {
        setMode( Mode.DEFAULT );
        while ( true ) {
            if ( mode == Mode.DEFAULT ) {
                turnRight( 360 );
                ahead( Math.random( ) * MAX_DISTANCE );
            }
        }
    }

    private double getDegrees( ) {
        return Math.random( ) * 360 - 180;
    }

    private void target( ScannedRobotEvent e ) {
        double bulletPower = Math.min( 3.0, getEnergy( ) );
        double myX = getX( );
        double myY = getY( );
        double absoluteBearing = getHeadingRadians( ) + e.getBearingRadians( );
        double enemyX = getX( ) + e.getDistance( ) * Math.sin( absoluteBearing );
        double enemyY = getY( ) + e.getDistance( ) * Math.cos( absoluteBearing );
        double enemyHeading = e.getHeadingRadians( );
        double enemyVelocity = e.getVelocity( );


        double deltaTime = 0;
        double battleFieldHeight = getBattleFieldHeight( ),
                battleFieldWidth = getBattleFieldWidth( );
        double predictedX = enemyX, predictedY = enemyY;
        while ( ( ++deltaTime ) * ( 20.0 - 3.0 * bulletPower ) <
                Point2D.Double.distance( myX, myY, predictedX, predictedY ) ) {
            predictedX += Math.sin( enemyHeading ) * enemyVelocity;
            predictedY += Math.cos( enemyHeading ) * enemyVelocity;
            if ( predictedX < 18.0
                    || predictedY < 18.0
                    || predictedX > battleFieldWidth - 18.0
                    || predictedY > battleFieldHeight - 18.0 ) {
                predictedX = Math.min( Math.max( 18.0, predictedX ),
                        battleFieldWidth - 18.0 );
                predictedY = Math.min( Math.max( 18.0, predictedY ),
                        battleFieldHeight - 18.0 );
                break;
            }
        }
        double theta = Utils.normalAbsoluteAngle( Math.atan2(
                predictedX - getX( ), predictedY - getY( ) ) );

        setTurnRadarRightRadians(
                Utils.normalRelativeAngle( absoluteBearing - getRadarHeadingRadians( ) ) );
        setTurnGunRightRadians( Utils.normalRelativeAngle( theta - getGunHeadingRadians( ) ) );
        fire( bulletPower );
    }

    @Override
    public void onScannedRobot( ScannedRobotEvent e ) {
        targetRobot( e );
    }

    private void targetRobot( final ScannedRobotEvent e ) {
        if ( e.getDistance( ) < 1000 ) {
            target( e );
        }
        turnRight( e.getBearing( ) );
        ahead( e.getDistance( ) / 2 );
    }

    @Override
    public void onHitByBullet( HitByBulletEvent e ) {
        if ( mode != Mode.HIT ) {
            setMode( Mode.HIT );
            stop( );
            ahead( 100 );
            turnLeft( 90 );
            ahead( 100);
            resume( );
            setMode( Mode.DEFAULT );
        }
    }

    @Override
    public void onHitWall( HitWallEvent e ) {
        turnLeft( 180 );
    }

    private boolean reactingOnRam = false;

    @Override
    public void onHitRobot( HitRobotEvent e ) {
        if ( mode != Mode.RAM ) {
            setMode( Mode.RAM );
            stop( );
            turnGunRight( getHeading( ) - getGunHeading( ) );
            turnRight( e.getBearing( ) );
            ahead( 20 );
            fire( e.getEnergy( ) );
            back( 40 );
            resume( );
            setMode( Mode.DEFAULT );
        }
    }

    private void setMode(Mode mode) {
        this.mode = mode;
        setBodyColor( mode.getColor() );
    }
}
