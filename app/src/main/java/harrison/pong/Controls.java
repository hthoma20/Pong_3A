package harrison.pong;


import android.view.View;
import android.widget.Button;
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

    PongAnimator pong;
    Button startButton;
    SeekBar paddleSeekBar;
    RadioGroup speedRadioGroup;

    int minPaddle= 100;
    int maxPaddle= 700;

    /**
     * controls constructor
     *
     * @param pong
     * @param start
     * @param paddleSize
     * @param speedRadioGroup
     */
    public Controls (PongAnimator pong, Button start,
                     SeekBar paddleSize, RadioGroup speedRadioGroup) {
        this.pong = pong;
        this.startButton = start;
        this.paddleSeekBar = paddleSize;
        this.speedRadioGroup = speedRadioGroup;

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
        paddleSeekBar.setOnSeekBarChangeListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == startButton) {
            //the radio button selected
            int checkedID = speedRadioGroup.getCheckedRadioButtonId();
            int minSpeed = -1;
            int maxSpeed = -1;

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

            startButton.setText("STOP!"); //changes text on button
            pong.startGame(minSpeed, maxSpeed);
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
    public void onStartTrackingTouch(SeekBar seekBar){}
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}
