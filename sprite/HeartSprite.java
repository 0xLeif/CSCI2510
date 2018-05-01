package sprite;

import java.net.URL;

import world.*;

public class HeartSprite extends Sprite {

	public int colorFramesPassed = 0;  // number of frames spent changing colors
	public int colorInterval = 5;     // number of frames passing before changing colors
	
    public HeartSprite(URL file) {
        super(file);
        // the heart has a circular bounds as an approximation
        addBound(new VectorCircle(new Vector2f(), 30.0f));

        // initially, the heart is not changing colors
        colorFramesPassed = 0;
    }
    
    // update heart characteristics
    public void update(DekuSprite deku, BGSprite bg) {
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

    private void transitionLevel(DekuSprite deku, BGSprite bg) {
        deku.setPos(new Vector2f(0.75f, -.72f));
        deku.setCurrentSpriteNum(0);
        bg.generateMaze();
        setPos(new Vector2f(.55f, .2f));
        GameStates.gameTime = 60;
    }

    // set the position of the torch
    public void setPos(Vector2f newPos) {
        pos = newPos;
        bounds.get(0).position = newPos;
    }
}