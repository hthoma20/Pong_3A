package harrison.pong;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Class Wall
 *
 * blueprint of Wall attributes
 * Able to check points within/close to any wall
 * Draws walls on animation surface
 *
 * @author Harry Thoma
 * @author Daylin Kuboyama
 */

public class Wall {

    //the coordinates of rectangle that is wall
    protected int left;
    protected int top;
    protected int right;
    protected int bottom;
    private Paint paint = new Paint () ;

    //the sides of any wall used for touching
    public static final int LEFTSIDE = 1;
    public static final int TOPSIDE = 2;
    public static final int RIGHTSIDE = 3;
    public static final int BOTTOMSIDE = 4;

    /**
     * wall constructor
     *
     * @param left coord of wall
     * @param top coord of wall
     * @param right coord of wall
     * @param bottom coord of wall
     * @param color of wall
     */
    public Wall ( int left, int top, int right, int bottom, int color) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.paint.setColor(color);
    }

    /**
     * draws rectangle at coords
     * @param c canvas on which to draw
     */
    public void onDraw (Canvas c) {
        c.drawRect(left, top, right, bottom, paint);
    }

    /**
     * @param x coord of point to test
     * @param y coord of point to test
     * @param rad the distance from the wall to test
     * @return the side of wall ball hit
     */
    public boolean isPointWithin (int x, int y, int rad) {
        if (x < left-rad) return false;
        if (y < top-rad) return false;
        if (x > right+rad) return false;
        if (y > bottom+rad) return false;

        return true;
    }

    /**
     * checks which wall side ball is closest to
     *
     * @param x coord of point
     * @param y coord of point
     * @return the side the point is closest to
     */
    public int sideClosestTo (int x, int y) {
        int distL = Math.abs(left-x);
        int distT = Math.abs(top-y);
        int distR = Math.abs(right-x);
        int distB = Math.abs(bottom-y);

        //determines min value
        int min = minVal(distL,distT,distR,distB);

        if (min==distL) return LEFTSIDE;
        if (min==distT) return TOPSIDE;
        if (min==distR) return RIGHTSIDE;
        return BOTTOMSIDE;
    }

    /**
     * finds min value
     * @param vals values to find min out of
     * @return min value
     */
    public int minVal (int... vals) {
        if (vals == null || vals.length<1) return 0;

        int min = vals[0];

        for (int val : vals) {
            if (val<min) min = val;
        }

        return min;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getRight() {
        return right;
    }

    public int getBottom() {
        return bottom;
    }

    protected void setColor (int color) {
        this.paint.setColor(color);
    }
}
