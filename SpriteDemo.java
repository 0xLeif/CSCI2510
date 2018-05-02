import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

import framework.*;
import sound.MusicManager;
import sound.SoundEffectManager;
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

    private int musicSelect; // for selecting what music to play, 1 when the player is chased
    private int lastPlayed;
    
    private int screenType;
    private static final int startScreen = 0;
    private static final int gameScreen = 1;
    private static final int winScreen = 2;
    private static final int loseScreen = 3;

    // sound
    private SoundEffectManager soundEffectManager;
    private MusicManager musicManager;

    public SpriteDemo() {
        appTitle = "Maze Game";
        appBorderScale = 0.99f;

        // app is a square
        appWidth = appHeight = 1250;
        appWorldWidth = appWorldHeight = 2.0f;
    }

    @Override
    protected void initialize() {
        super.initialize();

        // essential initializations
        renderBounds = true;
        view = getViewportTransform();

        // load spritesheets
        heart = new HeartSprite(getClass().getResource("/res/img/torch_9x1.png"));
        bg = new BGSprite(getClass().getResource("/res/img/background_1x1.png"));
        deku = new DekuSprite(getClass().getResource("/res/img/girlsprite_4x4.png"));

        // move deku up and to the right a bit
        deku.setPos(new Vector2f(0.75f, -.72f));
        heart.setPos(bg.getTorchPos());
        // set the viewport for the sprites
        bg.setViewsForBounds(view);
        heart.setViewsForBounds(view);
        deku.setViewsForBounds(view);
        for (Sprite sprite : GameStates.enemies){
            sprite.setViewsForBounds(view);
        }

        // play dubstep (for testing purposes)
        soundEffectManager = new SoundEffectManager();
        musicManager = new MusicManager();
        musicSelect = 0;
        lastPlayed = -1;
        musicManager.playMusic("level");
        setResizable(false);
        
        // Set up game state
        screenType = startScreen;
    }

    @Override
    protected void processInput(float delta) {
        super.processInput(delta);
    }

    @Override
    protected void updateObjects(float delta) {
        super.updateObjects(delta);

        if (screenType == startScreen) { 
        	if (keyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
        		screenType = gameScreen;
        	}
        }
        if (screenType == gameScreen) {
        	if (heart.wonGame) {
        		screenType = winScreen;
        	}
        	
        	// toggle rendering of bounding shapes
            if (keyboard.keyDownOnce(KeyEvent.VK_B)) {
                renderBounds = !renderBounds;
            }
            heart.setPos(bg.getTorchPos());
            bg.setViewsForBounds(getViewportTransform());
            heart.setViewsForBounds(getViewportTransform());
            deku.setViewsForBounds(getViewportTransform());
            for (Sprite sprite : GameStates.enemies){
                sprite.setViewsForBounds(getViewportTransform());
            }

            // update game sprites
            heart.update(deku, bg);
            deku.update(keyboard, bg);

            for (Sprite sprite : GameStates.enemies){
                if (sprite instanceof GhostSprite &&
                    ((GhostSprite) sprite).isChasingPlayer()) {
                    musicSelect = 1;
                }
                else
                    musicSelect = 0;
                if (sprite instanceof GhostSprite || sprite instanceof JelloSprite) {
                	if (sprite.isCollidingWith(deku)) {
                    	screenType = loseScreen;
                    }
                }
                sprite.update(deku, bg);
            }
            
            // apply words to vectorobject bounds
            heart.updateWorldsForBounds();
            bg.updateWorldsForBounds();
            deku.updateWorldsForBounds();
            for (Sprite sprite : GameStates.enemies){
                sprite.updateWorldsForBounds();
            }

            playMusic();
        }
        if (screenType == winScreen) {
        	System.out.println("You win!");
        	if (keyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
        		GameStates.enemies.clear();
        		initialize();
        	}
        }
        if (screenType == loseScreen) {
        	System.out.println("You lose!");
        	if (keyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
        		GameStates.enemies.clear();
        		initialize();
        	}
        }
        
    }

    private void playMusic() {
        if(musicSelect == 0 && lastPlayed != musicSelect) {
            musicManager.stopMusic();
            musicManager.playMusic("level");
            lastPlayed = musicSelect;
        }
        if(musicSelect == 1 && lastPlayed != musicSelect) {
            musicManager.stopMusic();
            musicManager.playMusic("chase");
            lastPlayed = musicSelect;
        }
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        if (screenType == startScreen) {
        	
        }
        if (screenType == gameScreen) {
        	// create a graphics2d object for
            // drawing BufferedImage instances
            Graphics2D g2d = (Graphics2D) g;

            // call render function for each sprite
            bg.render(g2d, getViewportTransform());
            heart.render(g2d, getViewportTransform());
            deku.render(g2d, getViewportTransform());
            for (Sprite sprite : GameStates.enemies){
                sprite.render(g2d, getViewportTransform());
            }

            // render the bounding shapes only
            // if bounds rendering is enabled

            if (renderBounds) {
                bg.renderBoundingShapes(g);
                heart.renderBoundingShapes(g);
                deku.renderBoundingShapes(g);
                for (Sprite sprite : GameStates.enemies){
                    if (!sprite.bounds.isEmpty()) {
                        sprite.renderBoundingShapes(g);
                    }
                }
            }
        }
        if (screenType == winScreen) {
        	
        }
        if (screenType == loseScreen) {
        	
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
