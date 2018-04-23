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
    private GhostSprite ghost;

    // gamestate
    private boolean renderBounds;

    // sound
    private SoundEffectManager soundEffectManager;
    private MusicManager musicManager;

    public SpriteDemo() {
        appTitle = "Maze Game";
        appBorderScale = 0.99f;

        // app is a square
        appWidth = appHeight = 1450;
        appWorldWidth = appWorldHeight = 2.0f;
    }

    @Override
    protected void initialize() {
        super.initialize();

        // essential initializations
        renderBounds = true;
        view = getViewportTransform();

        // load spritesheets
        heart = new HeartSprite(getClass().getResource("/res/hearts_9x1.png"));
        bg = new BGSprite(getClass().getResource("/res/floor_1x1.png"));
        deku = new DekuSprite(getClass().getResource("/res/deku_4x4.png"));
        ghost = new GhostSprite(getClass().getResource("/res/ghost_4x4.png"));
        
        // move deku up and to the right a bit
        deku.setPos(new Vector2f(0.25f, 0.25f));
        //ghost.setSpawnPos(new Vector2f(-0.25f, 0.25f));
        ghost.setSpawnPos(new Vector2f(0f, 0f));

        // set the viewport for the sprites
        bg.setViewsForBounds(view);
        heart.setViewsForBounds(view);
        deku.setViewsForBounds(view);
        ghost.setViewsForBounds(view);

        // play dubstep (for testing purposes)
        soundEffectManager = new SoundEffectManager();
        musicManager = new MusicManager();
        //musicManager.playMusic("dubstep");
        setResizable(false);
    }

    @Override
    protected void processInput(float delta) {
        super.processInput(delta);
    }

    @Override
    protected void updateObjects(float delta) {
        super.updateObjects(delta);

        // toggle rendering of bounding shapes
        if (keyboard.keyDownOnce(KeyEvent.VK_B)) {
            renderBounds = !renderBounds;
        }
        bg.setViewsForBounds(getViewportTransform());
        heart.setViewsForBounds(getViewportTransform());
        deku.setViewsForBounds(getViewportTransform());
        ghost.setViewsForBounds(getViewportTransform());

        // update game sprites
        heart.update(deku);
        deku.update(keyboard, bg);
        ghost.update(deku, bg);
        
        // apply words to vectorobject bounds
        heart.updateWorldsForBounds();
        bg.updateWorldsForBounds();
        deku.updateWorldsForBounds();
        ghost.updateWorldsForBounds();

    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        // create a graphics2d object for
        // drawing BufferedImage instances
        Graphics2D g2d = (Graphics2D) g;

        // call render function for each sprite
        bg.render(g2d, getViewportTransform());
        heart.render(g2d, getViewportTransform());
        deku.render(g2d, getViewportTransform());
        ghost.render(g2d, getViewportTransform());
        
        // render the bounding shapes only
        // if bounds rendering is enabled

        if (renderBounds) {
            bg.renderBoundingShapes(g);
            heart.renderBoundingShapes(g);
            deku.renderBoundingShapes(g);
            ghost.renderBoundingShapes(g);
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