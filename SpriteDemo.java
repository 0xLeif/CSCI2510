import java.awt.*;
import java.awt.event.*;

import framework.*;
import sound.MusicManager;
import sound.SoundEffectManager;
import sound.SoundManager;
import sprite.*;
import world.*;

public class SpriteDemo extends SimpleFramework {

	// viewport matrix
    private Matrix3x3f view;
    
    // game sprites
    private HeartSprite heart;
    private BGSprite bg;
    private DekuSprite deku;

    // gamestate
    private boolean renderBounds;

    // sound
    private SoundEffectManager soundEffectManager;
    private MusicManager musicManager;

    public SpriteDemo() {
        appTitle = "Sprite Demo";
        appBorderScale = 0.99f;

        // app is a square
        appWidth = appHeight = 640;
        appWorldWidth = appWorldHeight = 2.0f;

        // for consistency, don't allow window resizing
        setResizable(false);
    }

    @Override
    protected void initialize() {
        super.initialize();

        // essential initializations
        renderBounds = true;
        view = getViewportTransform();

        // load spritesheets
        heart = new HeartSprite(getClass().getResource("/res/hearts_9x1.png"));
        bg = new BGSprite(getClass().getResource("/res/background_1x1.png"));
        deku = new DekuSprite(getClass().getResource("/res/deku_4x4.png"));
        
        // move deku up and to the right a bit
        deku.setPos(new Vector2f(0.25f, 0.25f));

        // set the viewport for the sprites
        bg.setViewsForBounds(view);
        heart.setViewsForBounds(view);
        deku.setViewsForBounds(view);

        // play dubstep (for testing purposes)
        soundEffectManager = new SoundEffectManager();
        musicManager = new MusicManager();
        musicManager.playMusic("dubstep");

    }

    @Override
    protected void processInput(float delta) {
        super.processInput(delta);
        if(keyboard.keyDownOnce(KeyEvent.VK_M))
            soundEffectManager.playSound("gunshot");
    }

    @Override
    protected void updateObjects(float delta) {
        super.updateObjects(delta);
        
        // toggle rendering of bounding shapes
        if (keyboard.keyDownOnce(KeyEvent.VK_B)) {
            renderBounds = !renderBounds;
        }
        
        // update game sprites
        heart.update(deku);
        deku.update(keyboard, bg);
        
        // apply words to vectorobject bounds
        heart.updateWorldsForBounds();
        bg.updateWorldsForBounds();
        deku.updateWorldsForBounds();

    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        // create a graphics2d object for
        // drawing BufferedImage instances
        Graphics2D g2d = (Graphics2D) g;
        
        // call render function for each sprite
        bg.render(g2d, view);
        heart.render(g2d, view);
        deku.render(g2d, view);     
        
        // render the bounding shapes only
        // if bounds rendering is enabled
        if (renderBounds) {
            bg.renderBoundingShapes(g);
            heart.renderBoundingShapes(g);
            deku.renderBoundingShapes(g);
        }
    }

    @Override
    protected void terminate() {
        super.terminate();
    }

    public static void main(String[] args) {
        launchApp(new SpriteDemo());
    }
}