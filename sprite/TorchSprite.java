package sprite;

import java.net.URL;
import world.*;

public class TorchSprite extends Sprite {

	public int colorFramesPassed = 0;  // number of frames spent changing colors
	public int colorInterval = 5;     // number of frames passing before changing colors
	public boolean wonGame = false; // Whether the end of the game has been reached
	public int level = 0; // The current level the player is in
	private static int totalLevels = 2;
	
    public TorchSprite(URL file) {
        super(file);
        // the heart has a circular bounds as an approximation
        addBound(new VectorCircle(new Vector2f(), 30.0f));

        // initially, the heart is not changing colors
        colorFramesPassed = 0;
    }
    
    // update heart characteristics
    public void update(PlayerSprite deku, BGSprite bg) {
        colorFramesPassed++;

        // animate
        if (colorFramesPassed % colorInterval == 0) {
            nextSpriteNum();
        }
        // if player is standing on the heart...
    	if (isCollidingWith(deku)) {
            transitionLevel(deku, bg);
    	}
    }

    private void transitionLevel(PlayerSprite deku, BGSprite bg) {
    	level++;
    	if (level >= totalLevels || wonGame) {
    		wonGame = true;
    		return;
    	}
        deku.setPos(new Vector2f(0.75f, -.72f));
        deku.setCurrentSpriteNum(0);
        GameStates.enemies.clear();
        bg.generateMaze();
        GameStates.gameTime = 60;
    }

    // set the position of the torch
    public void setPos(Vector2f newPos) {
        pos = newPos;
        bounds.get(0).position = newPos;
    }
}