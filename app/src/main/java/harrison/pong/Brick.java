package harrison.pong;

import android.graphics.Color;

/**
 * Class Brick
 *
 *
 * @author Harry Thoma
 * @author Daylin Kuboyama
 */

public class Brick extends Wall {

    //number of hits remaining until brick breaks
    private int remainingHits;

    private static final int WHITE = Color.rgb(255,255,255); //error color
    private static final int RED = Color.rgb(255,0,0); //error color
    private static final int YELLOW = Color.rgb(247,255,9); //1 hit remain
    private static final int GREEN = Color.rgb(55,255,50); //2 hit remain
    private static final int PURPLE = Color.rgb(206,49,215); //3 hit remain

    //array of possible brick colors
    private static final int[] colors =
            {WHITE,YELLOW,GREEN,PURPLE};

    /**
     * Brick constructor
     *
     * @param left   coord of wall
     * @param top    coord of wall
     * @param right  coord of wall
     * @param bottom coord of wall
     * @param hits number of hits remaining until brick breaks
     */
    public Brick(int left, int top, int right, int bottom, int hits) {
        super(left, top, right, bottom, RED);
        this.remainingHits = hits;
        setColor();
    }

    /**
     * sets color of brick based on how many hits remaining
     */
    private void setColor () {
        if (remainingHits < 0) {//bricks should be destroyed
            super.setColor(WHITE);
            return;
        }
        if (remainingHits >= colors.length) {//if remaining hits is some unexpected value
            super.setColor(RED);
            return;
        }

        super.setColor(colors[remainingHits]); //sets color of brick
    }

    /**
     * @return whether brick can be deleted
     */
    public boolean ifBreak () {
        return (remainingHits < 1);
    }

    /**
     * called when brick is hit; reduces life of brickf
     */
    public void hit () {
        remainingHits--;
        setColor();
    }
}
