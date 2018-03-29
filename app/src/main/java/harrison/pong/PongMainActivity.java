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
 * An arbitrary number of balls can be in play at once.
 * When the ball is in play, if the user hits "BALL++", a new ball
 * will shoot out of their paddle.
 * Player only has three lives. If the player loses all balls, a life is lost.
 * Added brick breaker. Many bricks appear on screen and either take one, two,
 * or three hits of the ball to break. When a brick is broken, the score is incimented.
 * When a life is lost, so is the score.
 * When all lives are lost, or all bricks are broken, a game over message pops up.
 * Final score is calculated by current score (bricks consecutively broken) times
 * lives remaining.
 * Game can then be restarted with the "New Game" button.
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

        Controls control =
                new Controls(pong,startButton,addBallButton,paddleSizeBar,
                        ballSpeedRadio);
    }
}
