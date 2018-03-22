package harrison.pong;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Class PongAnimator
 *
 * Includes all functionalities of Pong game dealing with
 * ball, walls, including paddle, and controls.
 *
 * @author Harry Thoma
 * @author Daylin Kuboyama
 */

public class PongAnimator implements Animator{

    //array of walls: left, top, right
    private Wall[] walls = new Wall[4];
    private static final int LEFT = 0;
    private static final int TOP = 1;
    private static final int RIGHT = 2;
    private static final int PADDLE = 3;

    private final Paddle paddle; //just a reference to the paddle in walls[PADDLE]
    private Ball ball;

    //whether ball is in play
    private boolean ballInPlay;

    private int screenWidth;
    private int screenHeight;

    private int score; //number of times ball hits paddle

    private int tickInterval = 7;

    //reference to Controls object that owns this PongAnimator
    private Controls control = null;

    /**
     * PongAnimator constructor
     */
    public PongAnimator (){
        screenWidth = 2550;
        screenHeight = 1300;
        int wallWidth = 65;

        int wallColor = 0xff555555;
        int ballRad = 45;

        paddle= new Paddle (screenWidth/2, screenHeight-wallWidth,
                screenWidth/2+300,screenHeight, 0xffff0000);

        walls[LEFT] =
                new Wall(0,wallWidth,
                        wallWidth,screenHeight,wallColor);
        walls[TOP] =
                new Wall(wallWidth,0,
                        screenWidth-wallWidth,wallWidth,wallColor);
        walls[RIGHT] =
                new Wall(screenWidth-wallWidth,wallWidth,
                        screenWidth,screenHeight,wallColor);

        walls[PADDLE] = paddle;

        ball = new Ball(paddle.getCenterX(),paddle.getTop()-ballRad,
                        ballRad,0,0,0xff0000ff);

        ballInPlay = false;
        score= 0;
    }

    public void onDraw (Canvas c){
        //draw walls
        for (Wall wall : walls) {
            wall.onDraw(c);
        }

        drawBoundary(c);
        drawScore(c);
        ball.onDraw(c);
    }

    private void drawBoundary (Canvas c) {
        Paint boundaryPaint = new Paint ();
        boundaryPaint.setColor(0xffffffff);

        //the length of each segment of broken line
        int interval = 25;

        for(int i=0; i <= screenWidth; i+= 2*interval){
            c.drawLine(i,screenHeight,i+interval,screenHeight,boundaryPaint);
        }
    }

    private void drawScore(Canvas c){
        Paint scorePaint= new Paint();
        scorePaint.setColor(0xff000000);
        scorePaint.setTextSize(paddle.getHeight());
        scorePaint.setTextAlign(Paint.Align.CENTER);

        c.drawText
                (""+score,paddle.getCenterX(),paddle.getBottom()-5,scorePaint);

        /**
         * External citation
         * Date: 3/16/18
         * Problem: Wanted to draw the score on the surface
         * Resource: Android Canvas API, Android Paint API
         * Solution: drawText(), setTextAlign()
         */
    }

    /**
     * if we don't already have a control, sets control to given control
     * @param control the control to set
     * @return whether we set the control
     */
    public boolean setControls (Controls control) {
        if (this.control != null) return false;

        this.control = control;
        return true;
    }

    @Override
    public int interval() {
        return tickInterval; //how many millis between ticks
    }

    @Override
    public int backgroundColor() {
        return 0xff000000;
    }

    @Override
    public boolean doPause() {
        return false;
    }

    @Override
    public boolean doQuit() {
        return false;
    }

    @Override
    public void tick(Canvas canvas) {
        //if ball not in play, we don't need to do anything
        if(ballInPlay) {

            ball.move(tickInterval);

            //check for collision with a wall
            Wall hitWall = checkCollision();
            //if we hit a wall, bounce the ball
            if (hitWall != null) bounceBall(hitWall);

            if (hitWall == paddle) score++;

            //if ball was out of bounds, restart ball in playing position
            if (!ballInBounds()) restartBall();
        }

        onDraw(canvas);
    }

    /**
     * checks if ball has hit a wall
     *
     * @return the wall that was hit,
     *          null if no collision
     */
    private Wall checkCollision(){
        //ball's location
        int ballX = (int) ball.getX();
        int ballY = (int) ball.getY();
        int ballRad = ball.getRadius();


        //the wall we are checking for a collision with
        //checks if ball hits any wall
        for (Wall currWall : walls) {
            //if the ball is within the radius of the current wall
            if (currWall.isPointWithin(ballX, ballY, ballRad)) {
                //we have found the wall the ball is touching
                //so return which side of the wall the ball hit
                return currWall;
            }
        }

        //no wall was hit
        return null;
    }

    /**
     * @return whether ball is in valid area
     */
    private boolean ballInBounds () {
        return (ball.getY() < screenHeight+ball.getRadius());
    }

    /**
     * bounces the ball off wall
     * @param wall the wall that was hit
     */
    private void bounceBall(Wall wall){
        //the side of the wall that was hit
        int wallSide= wall.sideClosestTo(
                (int)ball.getX(),(int)ball.getY());

        //the direction of the ball in radians
        double ballDir= ball.getDirection();

        //the direction of the ball as a compass quadrant:
        // NE, NW, SW, SE
        double quadDir= ball.quadrantDirection();

        //check which side of wall ball hits
        //and determine which angle ball will bounce off at
        switch (wallSide) {
            case Wall.LEFTSIDE:
                //if ball is traveling east
                if (quadDir == Ball.NE || quadDir == Ball.SE) {
                    ball.setDirection(Math.PI - ballDir);
                }
                break;
            case Wall.TOPSIDE:
                //if ball is traveling south
                if (quadDir == Ball.SE || quadDir == Ball.SW) {
                    ball.setDirection(-ballDir);
                }
                break;
            case Wall.RIGHTSIDE:
                //if ball is traveling west
                if (quadDir == Ball.NW || quadDir == Ball.SW) {
                    ball.setDirection(Math.PI-ballDir);
                }
                break;
            case Wall.BOTTOMSIDE:
                //if ball is traveling north
                if (quadDir == Ball.NE || quadDir == Ball.NW) {
                    ball.setDirection(-ballDir);
                }
                break;
        }
    }

    /**
     * places ball back in starting position
     */
    private void restartBall () {
        int ballRad = ball.getRadius();

        ball = new Ball (paddle.getCenterX(),paddle.getTop()-ballRad,
                ballRad,0,0,0xff0000ff);

        ballInPlay = false;
        score= 0;

        control.ballRestarted();
    }

    /**
     * if ball is dead, starts game
     * if ball is live, restarts game
     *
     * @param minSpeed that player wants
     * @param maxSpeed that player wants
     *
     * @return whether the game was started (T) or restarted (F)
     */
    public boolean startGame (int minSpeed, int maxSpeed) {
        if (ballInPlay) {
            restartBall();
            return false;
        }

        startBall(minSpeed, maxSpeed);
        return true;
    }

    /**
     * sets random speed and direction of starting ball
     */
    public void startBall (int minSpeed, int maxSpeed) {
        ballInPlay = true;

        //set random speed
        int spd = (int) (Math.random()*(maxSpeed-minSpeed)+minSpeed);
        ball.setSpeed(spd);

        //set random direction
        double minDir = Math.PI/6;
        double maxDir = 5*Math.PI/6;
        double dir = Math.random()*(maxDir-minDir)+minDir;
        ball.setDirection(dir);
    }

    @Override
    public void onTouch(MotionEvent event) {
        if (!ballInPlay) {
            ball.setX(paddle.getCenterX());
        }
        movePaddle((int) event.getX());
    }

    /**
     * moves paddle according to right and left boundaries
     * @param x coord of where we want to move paddle
     */
    public void movePaddle (int x) {
        paddle.setCenterX(x);
        checkPaddleToWall();

    }

    /**
     * checks if paddle is touching or past any wall
     * if so, moves paddle within wall boundaries
     * @return whether paddle had to move
     */
    private boolean checkPaddleToWall () {
        //these are boundaries that paddle is allowed to be within
        int leftBoundary = walls[LEFT].getRight();
        int rightBoundary = walls[RIGHT].getLeft();
        //make sure paddle is in bounds

        if (paddle.getLeft() < leftBoundary) {
            paddle.setLeft(leftBoundary);
            return true;
        }
        if (paddle.getRight() > rightBoundary) {
            paddle.setRight(rightBoundary);
            return true;
        }

        return false;
    }

    /**
     * if ball is out of play, the paddle size will change
     *
     * @param paddleSize the new size of paddle
     * @return whether the size was changed
     */
    public boolean changePaddleSize(int paddleSize){
        if(ballInPlay) return false;

        paddle.setWidth(paddleSize);
        //makes sure paddle does not pass wall boundary
        if (checkPaddleToWall()) {
            //if paddle moved, ball should follow
            ball.setX(paddle.getCenterX());
        }
        return true;
    }
}
