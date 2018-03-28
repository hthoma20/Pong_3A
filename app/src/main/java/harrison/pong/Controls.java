package harrison.pong;


import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;

/**
 * Class Controls
 *
 * Able to determine actions of widgets according to PongAnimator
 * has references to and listens to the views on the control panel
 * Updates Pong to match controls, and vice versa
 *
 * @author Harry Thoma
 * @author Daylin Kuboyama
 */

public class Controls implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private PongAnimator pong;
    private Button startButton;
    private Button addBallButton;
    private SeekBar paddleSeekBar;
    private RadioGroup speedRadioGroup;

    private LinearLayout gameOver;
    private Button closeMessageButton;

    //range of size of paddle
    private int minPaddle= 100;
    private int maxPaddle= 700;

    //range of speed of ball
    private int minSpeed= -1;
    private int maxSpeed= -1;

    /**
     * controls constructor
     *
     * @param pong
     * @param start
     * @param addBall
     * @param paddleSize
     * @param speedRadioGroup
     * @param gameOver linear layout of game over display
     * @param closeMessageButton button to close message
     */
    public Controls (PongAnimator pong, Button start,Button addBall,
                     SeekBar paddleSize, RadioGroup speedRadioGroup,
                     LinearLayout gameOver, Button closeMessageButton) {
        this.pong = pong;
        this.startButton = start;
        this.addBallButton = addBall;
        this.paddleSeekBar = paddleSize;
        this.speedRadioGroup = speedRadioGroup;
        this.gameOver = gameOver;
        this.closeMessageButton= closeMessageButton;

        pong.setControls(this);
        initViews();
        initListener();
    }

    /**
     * initializes the views
     */
    private void initViews () {
        paddleSeekBar.setMax(maxPaddle-minPaddle);
        setSeekBarToSize((maxPaddle-minPaddle)/2);

        speedRadioGroup.check(R.id.radioButtonNormal); //default to normal
    }

    /**
     * initializes the listeners
     */
    private void initListener () {
        startButton.setOnClickListener (this);
        addBallButton.setOnClickListener(this);
        paddleSeekBar.setOnSeekBarChangeListener(this);

        closeMessageButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == startButton) {
            findSpeeds();
            startButton.setText("STOP!"); //changes text on button
            pong.startGame(minSpeed, maxSpeed);
        }
        else if (view == addBallButton) {
            findSpeeds();
            pong.addBall(minSpeed, maxSpeed);
        }
        else if(view == closeMessageButton){
            gameOver.setVisibility(View.INVISIBLE);
        }
    }

    public void findSpeeds () {
        int checkedID = speedRadioGroup.getCheckedRadioButtonId();

        if (checkedID == R.id.radioButtonSlow) {
            minSpeed = 2000;
            maxSpeed = 3000;
        }
        else if (checkedID == R.id.radioButtonNormal) {
            minSpeed = 4000;
            maxSpeed = 5000;
        }
        else if (checkedID == R.id.radioButtonFast) {
            minSpeed = 6000;
            maxSpeed = 7000;
        }
    }

    /**
     * updates controls to match restarted game
     */
    public void ballRestarted () {
        startButton.setText("START!");
        pong.changePaddleSize(paddleSize());

    }

    /**
     * enables popup message if no more lives for player to play
     */
    public void gameOver (){
        gameOver.setVisibility(View.VISIBLE);
    }

    /**
     * @return the size the paddle should be based on the seekBar
     */
    public int paddleSize(){
        return paddleSeekBar.getProgress()+minPaddle;
    }

    /**
     * changes seekBar to reflect given paddle size
     * @param size the size of paddle
     */
    private void setSeekBarToSize(int size){
        int progress= size-minPaddle;
        paddleSeekBar.setProgress(progress);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        pong.changePaddleSize(paddleSize());
    }

    //unused methods
    @Override
    public void onStartTrackingTouch(SeekBar seekBar){
        gameOver();
    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}
