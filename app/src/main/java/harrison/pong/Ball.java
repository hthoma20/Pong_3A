package harrison.pong;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

/**
 * Class Ball
 *
 * blue print of Ball attributes
 * Able to deal with movement only related to ball
 * Draws ball onto animation surface
 *
 * @author Harry Thoma
 * @author Daylin Kuboyama
 */

public class Ball {

    //center location of ball
    private double x;
    private double y;
    private int radius;
    private int speed; //in pixels/sec
    private double direction; //in radians
    private Paint paint = new Paint();

    //the 4 quadrants of direction that ball could be traveling
    public static final int NE = 1;
    public static final int NW = 2;
    public static final int SW = 3;
    public static final int SE = 4;

    /**
     * Ball constructor
     *
     * @param x center coord of ball
     * @param y center coord of ball
     * @param rad of ball
     * @param spd speed ball is traveling
     * @param dir direction ball is moving in
     * @param col color of ball
     */
    public Ball (double x, double y, int rad, int spd, double dir, int col){
        this.x = x;
        this.y = y;
        this.radius = rad;
        this.speed = spd;
        this.direction = dir;
        angleDirection();
        this.paint.setColor(col);
    }

    /**
     * Ball constructor that randomizes speed and direction
     *
     * @param x
     * @param y
     * @param rad
     * @param col
     */
    public Ball (double x, double y, int rad, int col) {
        this.x = x;
        this.y = y;
        this.radius = rad;
        this.paint.setColor(col);

        //set random speed
        int minSpeed = 1000;
        int maxSpeed = 3000;
        this.speed = (int) (Math.random()*(maxSpeed-minSpeed)+minSpeed);

        //set random direction
        this.direction = Math.random()*2*Math.PI;
        angleDirection();
    }

    /**
     * draws ball on canvas
     * @param c canvas on which to draw
     */
    public void onDraw (Canvas c){

        c.drawCircle((float)x,(float)y,radius,paint);
        Paint dotPaint= new Paint();
        dotPaint.setColor(0xff000000);
        c.drawCircle((float)x,(float)y,3,dotPaint);
    }

    /**
     * moves ball according to how much time has passed and its speed
     *
     * @param deltaT the amount of time since last move in milliseconds
     */
    public void move (int deltaT){
        //TODO ball speed seems to be dependent on given deltaT

        double xSpeed = Math.cos(direction)*speed;
        double ySpeed = Math.sin(direction)*speed;

        double deltaX = xSpeed*deltaT/1000;
        double deltaY = ySpeed*deltaT/1000;

        x += deltaX;
        y -= deltaY; //minus is since Java's y-axis increases downward
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getRadius(){
        return radius;
    }

    public double getDirection () {
        return direction;
    }

    public void setX (double x) {
        this.x = x;
    }

    public void setSpeed (int speed) {
        this.speed = speed;
    }

    public void setDirection (double direction) {
        this.direction = direction;
        angleDirection();
    }

    /**
     * increments ball's direction
     *
     * @param deltaTheta the angle by which to increment
     */
    public void incrementDirection(double deltaTheta) {
        this.direction += deltaTheta;
        angleDirection();
    }

    /**
     * makes sure direction is between angles 0 and 2pi
     * If not between range, changes to corresponding angle within range
     */
    private void angleDirection () {
        while (direction<0) {
            direction += Math.PI*2;
        }
        while (direction>=2*Math.PI) {
            direction -= Math.PI*2;
        }
    }

    /**
     * determines which direction ball is traveling in
     * @return the direction ball is traveling
     */
    public int quadrantDirection () {
        if (direction >= 3*Math.PI/2) return SE;
        if (direction >= Math.PI) return SW;
        if (direction >= Math.PI/2) return NW;
        return NE;
    }
}