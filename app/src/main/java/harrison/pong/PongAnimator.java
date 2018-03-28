package harrison.pong;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

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

    private Brick[] bricks;

    private final Paddle paddle; //just a reference to the paddle in walls[PADDLE]
    private ArrayList<Ball> balls= new ArrayList<>();

    //whether ball is in play
    private boolean ballInPlay;

    private int screenWidth;
    private int screenHeight;
    private int wallWidth;
    private int ballRad= 45;

    private int score; //number of times ball hits paddle
    private int livesRemaining = 3; //initial amount of lives given

    private int tickInterval = 7;

    //reference to Controls object that owns this PongAnimator
    private Controls control = null;

    /**
     * PongAnimator constructor
     */
    public PongAnimator (){
        screenWidth = 2550;
        screenHeight = 1300;
        wallWidth = screenHeight/20;

        int wallColor = 0xff555555;

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

        initBricks(); //creates bricks

        balls.add( new Ball(paddle.getCenterX(),paddle.getTop()-ballRad,
                        ballRad,0,0,0xff0000ff) );

        ballInPlay = false;
        score= 0;
    }

    public void onDraw (Canvas c){
        //draw walls
        for (Wall wall : walls) {
            wall.onDraw(c);
        }

        //draws bricks
        for (Brick brick : bricks) {
            if (brick == null) continue;
            brick.onDraw(c);
        }
        drawBoundary(c);
        drawScore(c);

        for(Ball ball : balls){
            ball.onDraw(c);
        }
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

    /**
     * draws both score and lives remaining
     *
     * @param c canvas on which to draw
     */
    private void drawScore(Canvas c){
        Paint scorePaint= new Paint();
        scorePaint.setColor(0xffffffff);
        scorePaint.setTextSize(wallWidth);

        c.drawText("Score: "+score,3*wallWidth/2,2*wallWidth,scorePaint);

        scorePaint.setColor(Color.rgb(0,0,255));//sets ball color

        int livesRad = 25; //radius of balls representing lives remaining
        //for every ball to represent lives drawn
        for (int i=0; i< livesRemaining; i++) {
            c.drawCircle(i*7*livesRad/3+2*wallWidth,2*(wallWidth+livesRad),
                    livesRad,scorePaint);
        }

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

    /**
     * Initializes bricks
     */
    private void initBricks () {
        bricks = new Brick[18];

        //size of each brick
        int w= screenWidth/8;
        int h= screenHeight/14;
        //top-left coord of each brick
        int x= screenWidth/2 - 3*w;
        int y= 2*wallWidth;

        int index= 0;

        //determines how many hits required to break brick in the row
        for (int i=0; i<4; i++) {
            int hits= 0;
            switch(i){
                case 0:
                case 3:
                    hits= 3;
                    break;
                case 1:
                    hits= 2;
                    break;
                case 2:
                    hits= 1;
                    break;
            }

            int bricksInRow= 6-i;

            initBrickRow(x+i*w/2, y+i*h, w, h, bricksInRow, hits, index);

            //increment where row should start
            index+= bricksInRow;
        }
    }

    /**
     * initializes a row of bricks
     *
     * @param x left-coord of this row
     * @param y top-coord of this row
     * @param width of each brick
     * @param height of each brick
     * @param numBricks number of bricks in row
     * @param hits to initialize bricks with
     * @param index starting index in brick array that this row is initializing
     */
    private void initBrickRow (int x, int y, int width, int height,
                               int numBricks, int hits, int index) {

        //bottom of bricks all the same
        int bottom = y+height;

        for (int i=0; i<numBricks; i++) {
            int left = x+i*width;
            int right = left+width;

            bricks[index+i] = new Brick(left,y,right,bottom,hits);
        }
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

            //check ball stuff
            for(Ball ball : balls) {
                ball.move(tickInterval);

                //check for collision with a wall
                Wall hitWall = checkCollision(ball);
                //if we hit a wall, bounce the ball
                if (hitWall != null) bounceBall(ball,hitWall);
            }

            for(int i=0; i < balls.size(); i++){
                Ball firstBall= balls.get(i);
                for(int j=i+1; j<balls.size(); j++){
                    Ball secondBall= balls.get(j);

                    if(firstBall.touches(secondBall)){
                        firstBall.bounceOff(secondBall);
                    }
                }
            }

            //check brick stuff
            for (int i=0; i<bricks.length; i++) {
                if (bricks[i] == null) continue;
                if (bricks[i].ifBreak()) {
                    bricks[i] = null; //deletes brick from array
                    score++;
                }
            }

            //if there are no balls in bounds
            if (!ballInBounds()) {

                //restart the ball at starting position
                restartBall();
                //they lose a life
                livesRemaining--;

                //if they have no lives remaining
                if (livesRemaining < 1) { //if game should be over
                    //control.gameOver();
                    //todo why can't we call this from a different thread
                }
            }

        }

        onDraw(canvas);
    }

    /**
     * checks if ball has hit a wall
     *
     * @param ball the ball to check
     * @return the wall that was hit,
     *          null if no collision
     */
    private Wall checkCollision(Ball ball){
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

        //checks if ball hits any brick
        for (Brick brick : bricks) {
            if (brick == null) continue;
            if (brick.isPointWithin(ballX,ballY,ballRad)) {
                brick.hit();
                return brick;
            }
        }

        //no wall was hit
        return null;
    }

    /**
     * checks if balls are in bounds
     * removes balls not in bounds
     *
     * @return whether thre is at least one ball in bounds
     */
    private boolean ballInBounds () {
        //whether we have seen an inbounds ball
        boolean hasInBounds= false;

        //a list of out of bounds balls
        //to be removed from balls
        ArrayList<Ball> outOfBoundsBalls=
                new ArrayList<>();

        for(Ball ball : balls){
            if(ball.getY() < screenHeight+ball.getRadius()){
                hasInBounds= true;
            }
            else{ //the ball is out of bounds
                outOfBoundsBalls.add(ball);
            }
        }

        //now we are done iterating balls
        //we may remove the balls that are out of bounds
        //from the balls array list
        for(Ball ball : outOfBoundsBalls){
            balls.remove(ball);
        }


        return hasInBounds;
    }

    /**
     * bounces the ball off wall
     * @param ball the ball to bounce
     * @param wall the wall to bounce ball off of
     */
    private void bounceBall(Ball ball, Wall wall){
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
        balls.clear();

        balls.add( new Ball (paddle.getCenterX(),paddle.getTop()-ballRad,
                ballRad,0,0,0xff0000ff) );

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
     *
     * @param minSpeed
     * @param maxSpeed
     */
    public void startBall (int minSpeed, int maxSpeed) {
        assert balls.size() == 1;

        ballInPlay = true;

        //set random speed
        int spd = (int) (Math.random()*(maxSpeed-minSpeed)+minSpeed);
        balls.get(0).setSpeed(spd);

        //set random direction
        double minDir = Math.PI/6;
        double maxDir = 5*Math.PI/6;
        double dir = Math.random()*(maxDir-minDir)+minDir;
        balls.get(0).setDirection(dir);
    }

    /**
     *adds a ball to play
     *
     * @param minSpeed
     * @param maxSpeed
     */
    public void addBall (int minSpeed, int maxSpeed) {
        if (!ballInPlay) return;

        //find random speed and direction
        int spd = (int) (Math.random()*(maxSpeed-minSpeed)+minSpeed);
        double minDir = Math.PI/6;
        double maxDir = 5*Math.PI/6;
        double dir = Math.random()*(maxDir-minDir)+minDir;

        int r= (int)(Math.random()*256);
        int g= (int)(Math.random()*256);
        int b= (int)(Math.random()*256);
        int ballColor= Color.rgb(r,g,b);

        balls.add(new Ball(paddle.getCenterX(),paddle.getTop()-ballRad,ballRad,
                spd,dir,ballColor));
    }

    @Override
    public void onTouch(MotionEvent event) {
        if (!ballInPlay) {
            assert balls.size() == 1;

            balls.get(0).setX(paddle.getCenterX());
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

        assert balls.size() == 1;

        paddle.setWidth(paddleSize);
        //makes sure paddle does not pass wall boundary
        if (checkPaddleToWall()) {
            //if paddle moved, ball should follow
            balls.get(0).setX(paddle.getCenterX());
        }
        return true;
    }
}
