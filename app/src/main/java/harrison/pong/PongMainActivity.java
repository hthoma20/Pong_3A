package harrison.pong;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;

/**
 * PongMainActivity
 *
 * This is the activity for the Pong game. It attaches a PongAnimator to
 * an AnimationSurface.
 *
 * 3A Update: Created a working program of Pong.
 * Ball starts in stationary position on paddle. User is able to drag the
 * paddle to where they want to start, and hit the start button when they
 * are ready, which will launch ball at a random speed and direction.
 * Custom player features include changing the size of the paddle and
 * selecting the speed of the ball. When ball hits a wall or paddle, ball
 * will bounce off the wall. Game will end and ball will restart when
 * ball either goes out of bounds or player presses "STOP!" button.
 *
 * 3B Update:
 *
 * Instructions:
 * Landscape orientation
 *
 * @author Andrew Nuxoll
 * @author Steven R. Vegdahl
 * @version July 2013
 *
 * @author Harry Thoma
 * @author Daylin Kuboyama
 */
public class PongMainActivity extends AppCompatActivity {

    /**
     * creates an AnimationSurface containing a TestAnimator.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pong_main);

        // Connect the animation surface with the animator
        AnimationSurface mySurface = (AnimationSurface) this
                .findViewById(R.id.animationSurface);
        PongAnimator pong = new PongAnimator();
        mySurface.setAnimator(pong);

        Button startButton=
                (Button)findViewById(R.id.buttonStart);
        Button addBallButton=
                (Button)findViewById(R.id.buttonAddBall);
        SeekBar paddleSizeBar=
                (SeekBar)findViewById(R.id.seekBarPaddleSize);
        RadioGroup ballSpeedRadio=
                (RadioGroup)findViewById(R.id.radioGroupSpeed);
        LinearLayout gameOver =
                (LinearLayout)findViewById(R.id.layoutGameOverMessage);
        Button closeMessageButton= (Button)findViewById(R.id.buttonCloseMessage);

        Controls control =
                new Controls(pong,startButton,addBallButton,paddleSizeBar,
                        ballSpeedRadio, gameOver,closeMessageButton);
    }
}
