import java.awt.*;
import java.awt.event.*;
import java.util.*;

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
    private GhostSprite ghost;
    private JelloSprite jello;

    // gamestate
    private boolean renderBounds;
    
    private int gameState; // Determines which gamestate the player is in currently
    private static int startScreen = 0;
    private static int isPlaying = 1;
    private static int winScreen = 2;
    private static int gameOverScreen = 3;

    private int musicSelect; // for selecting what music to play, 1 when the player is chased
    private int lastPlayed;

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
        ghost = new GhostSprite(getClass().getResource("/res/img/ghost_4x4.png"));
        jello = new JelloSprite(getClass().getResource("/res/img/jello_3x4.png"));
        
        // move deku up and to the right a bit
        deku.setPos(new Vector2f(0.75f, -.72f));
        ghost.setSpawnPos(new Vector2f(-0.50f, -.72f));
        jello.setSpawnPos(new Vector2f(0.75f, -.72f));

        // set the viewport for the sprites
        bg.setViewsForBounds(view);
        heart.setViewsForBounds(view);
        deku.setViewsForBounds(view);
        ghost.setViewsForBounds(view);
        jello.setViewsForBounds(view);

        // play dubstep (for testing purposes)
        soundEffectManager = new SoundEffectManager();
        musicManager = new MusicManager();
        musicSelect = 0;
        lastPlayed = -1;
        musicManager.playMusic("level");
        setResizable(false);
        
        createTimer();
        
        gameState = startScreen;
    }
    
    private void createTimer() {
        GameStates.timer.schedule(new TimerTask() {
            public void run() {
                if(--GameStates.gameTime == 0) {
                    System.out.println("Game Over!");
                    System.exit(1);
                }
            }
        }, 0, 1000);
    }

    @Override
    protected void processInput(float delta) {
        super.processInput(delta);
    }

    @Override
    protected void updateObjects(float delta) {
        super.updateObjects(delta);

        if (gameState == startScreen) { // Start screen
        	if (keyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
        		gameState = isPlaying;
        	}
        }
        if (gameState == isPlaying) {
        	if (heart.wonGame) {
        		gameState = winScreen;
        	}
        	else if (ghost.isCollidingWith(deku)) { 
        		gameState = gameOverScreen;
        	}
        	else { 
        		// toggle rendering of bounding shapes
                if (keyboard.keyDownOnce(KeyEvent.VK_B)) {
                    renderBounds = !renderBounds;
                }
                bg.setViewsForBounds(getViewportTransform());
                heart.setViewsForBounds(getViewportTransform());
                deku.setViewsForBounds(getViewportTransform());
                ghost.setViewsForBounds(getViewportTransform());
                jello.setViewsForBounds(getViewportTransform());

                // update game sprites
                heart.update(deku, bg);
                deku.update(keyboard, bg);
                if(ghost.update(deku, bg))
                    musicSelect = 1;
                else
                    musicSelect = 0;
                jello.update(deku, bg);
                
                // apply words to vectorobject bounds
                heart.updateWorldsForBounds();
                bg.updateWorldsForBounds();
                deku.updateWorldsForBounds();
                ghost.updateWorldsForBounds();
                jello.updateWorldsForBounds();

                playMusic();
        	}
        }
        if (gameState == winScreen) {
        	System.out.println("You win");
        }
        if (gameState == gameOverScreen) {
        	System.out.println("You lose");
        }
        else { // Unknown state?
        	
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
    
    private void renderTimer(Graphics g) {
        Font z = new Font("ZapfDingbats", Font.PLAIN, 30);
        g.setColor(Color.red);  
        g.setFont(z);
        g.drawString("Time Left: " + GameStates.gameTime, 30, 30);
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        
        if (gameState == isPlaying) { 
        	// create a graphics2d object for
            // drawing BufferedImage instances
            Graphics2D g2d = (Graphics2D) g;

            // call render function for each sprite
            bg.render(g2d, getViewportTransform());
            heart.render(g2d, getViewportTransform());
            deku.render(g2d, getViewportTransform());
            ghost.render(g2d, getViewportTransform());
            jello.render(g2d, getViewportTransform());

            // render the bounding shapes only
            // if bounds rendering is enabled

            if (renderBounds) {
                bg.renderBoundingShapes(g);
                heart.renderBoundingShapes(g);
                deku.renderBoundingShapes(g);
                ghost.renderBoundingShapes(g);
                jello.renderBoundingShapes(g);
            }
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
