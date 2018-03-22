package harrison.pong;

/**
 * Class Paddle
 *
 * blueprint of Paddle attributes; extends Wall, so also has Wall attributes
 * Draws paddle onto animation surface
 *
 * @author Harry Thoma
 * @author Daylin Kuboyama
 */

public class Paddle extends Wall {

    //size of paddle
    private int width;
    private int height;

    /**
     * paddle constructor
     *
     * @param left   coord of wall
     * @param top    coord of wall
     * @param right  coord of wall
     * @param bottom coord of wall
     * @param color  of wall
     */
    public Paddle(int left, int top, int right, int bottom, int color) {
        super(left, top, right, bottom, color);
        width = right-left;
        height = bottom-top;
    }

    /**
     * sets width of paddle according to seekBar
     * changes left and right to match
     * @param width
     */
    public void setWidth (int width) {
        int center = getCenterX();
        left = center-width/2;

        //the %2 corrects for an odd width
        right = center+width/2 +width%2;

        this.width= width;
    }

    /**
     * sets x-coord of center of paddle
     */
    public void setCenterX (int x) {
        left = x-(width/2);
        right = x+(width/2);
    }

    /**
     * sets y-coord of center of paddle
     */
    public void setCenterY (int y) {
        top = y+(height/2);
        bottom = y-(height/2);
    }

    /**
     * changes location of paddle based on a given left side
     * while maintaining size of paddle
     * @param newLeft where the left side of paddle should start
     */
    public void setLeft (int newLeft){
        this.left = newLeft;
        this.right = left+width;
    }

    /**
     * changes location of paddle based on given right side
     * while maintaining size of paddle
     * @param newRight where the right side of paddle should start
     */
    public void setRight (int newRight) {
        this.right = newRight;
        this.left = right-width;
    }

    public int getCenterX () {
        return (left+right)/2;
    }

    public int getCenterY () {
        return (top+bottom)/2;
    }

    public int getHeight(){
        return height;
    }

}
