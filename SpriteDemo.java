import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;

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
    private MenuSprite startMenu;
    private MenuSprite winMenu;
    private MenuSprite loseMenu;

    // gamestate
    private boolean renderBounds;

    private int musicSelect; // for selecting what music to play, 1 when the player is chased, 2 for title, 3 for game over, 4 for victory
    private int lastPlayed;
    
    private int screenType;
    private static final int startScreen = 0;
    private static final int gameScreen = 1;
    private static final int winScreen = 2;
    private static final int loseScreen = 3;

    private boolean playerChased;

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
        renderBounds = false;
        view = getViewportTransform();

        // load spritesheets
        heart = new HeartSprite(getClass().getResource("/res/img/torch_9x1.png"));
        bg = new BGSprite(getClass().getResource("/res/img/background_1x1.png"));
        deku = new DekuSprite(getClass().getResource("/res/img/girlsprite_4x4.png"));
        startMenu = new MenuSprite(getClass().getResource("/res/img/startscreen_1x1.png"));
        loseMenu = new MenuSprite(getClass().getResource("/res/img/deathscreen_1x1.png"));
        winMenu = new MenuSprite(getClass().getResource("/res/img/victoryscreen_1x1.png"));

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

        // music and sound
        soundEffectManager = new SoundEffectManager();
        musicManager = new MusicManager();
        musicSelect = 2;
        lastPlayed = -1;
        musicManager.playMusic("start");
        setResizable(false);

        playerChased = false;
        
        // Set up game state
        screenType = startScreen;

        createTimer();
    }

    private void createTimer(){
        GameStates.timer.schedule(new TimerTask() {
            public void run() {
                if(--GameStates.gameTime == 0 && screenType != startScreen){
                    screenType = loseScreen;
                    GameStates.gameTime = 60;
                }
            }
        }, 0, 1000);
    }

    private void renderTimer(Graphics g){
        Font z = new Font("ZapfDingbats", Font.PLAIN, 30);
        g.setColor(Color.red);
        g.setFont(z);
        g.drawString("Time Left: " + GameStates.gameTime, 80, 80);
    }

    @Override
    protected void processInput(float delta) {
        super.processInput(delta);
    }

    @Override
    protected void updateObjects(float delta) {
        super.updateObjects(delta);
        playerChased = false;

        if (screenType == startScreen) { 
        	if (keyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
        		screenType = gameScreen;
                GameStates.gameTime = 60;
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
                    playerChased = true;
                }
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
            for (Sprite sprite : GameStates.enemies) {
                sprite.updateWorldsForBounds();
            }
            if(playerChased)
                musicSelect = 1;
            else
                musicSelect = 0;
            playMusic();
        }
        if (screenType == winScreen) {
        	musicSelect = 4;
            playMusic();
        	if (keyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
        		GameStates.enemies.clear();
        		musicManager.stopMusic();
        		initialize();
        	}
        }
        if (screenType == loseScreen) {
            musicSelect = 3;
            playMusic();
        	if (keyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
        		GameStates.enemies.clear();
        		musicManager.stopMusic();
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
        if(musicSelect == 2 && lastPlayed != musicSelect) {
            musicManager.stopMusic();
            musicManager.playMusic("start");
            lastPlayed = musicSelect;
        }
        if(musicSelect == 3 && lastPlayed != musicSelect) {
            musicManager.stopMusic();
            musicManager.playMusic("gameover");
            lastPlayed = musicSelect;
        }
        if(musicSelect == 4 && lastPlayed != musicSelect) {
            musicManager.stopMusic();
            musicManager.playMusic("victory");
            lastPlayed = musicSelect;
        }
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        
        // create a graphics2d object for
        // drawing BufferedImage instances
        Graphics2D g2d = (Graphics2D) g;
        
        if (screenType == startScreen) {
        	startMenu.render(g2d, getViewportTransform());
        }
        if (screenType == gameScreen) {
            // call render function for each sprite
            bg.render(g2d, getViewportTransform());
            heart.render(g2d, getViewportTransform());
            deku.render(g2d, getViewportTransform());
            for (Sprite sprite : GameStates.enemies){
                sprite.render(g2d, getViewportTransform());
            }
            renderTimer(g);

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
            if(bg.isNewLevel()){
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
        	winMenu.render(g2d, getViewportTransform());
        }
        if (screenType == loseScreen) {
        	loseMenu.render(g2d, getViewportTransform());
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
