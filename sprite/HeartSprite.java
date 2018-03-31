package sprite;

import java.net.URL;

import world.*;

public class HeartSprite extends Sprite {

	public int colorFramesPassed = 0;  // number of frames spent changing colors
	public int colorInterval = 30;     // number of frames passing before changing colors
	
    public HeartSprite(URL file) {
        super(file);
        // the heart has a circular bounds as an approximation
        addBound(new VectorCircle(new Vector2f(), 60.0f));

        // initially, the heart is not changing colors
        colorFramesPassed = 0;
    }
    
    // update heart characteristics
    public void update(DekuSprite deku) {

        // if deku is standing on the heart...           	
    	if (isCollidingWith(deku)) {
            // count each frame that deku is on the heart
			colorFramesPassed++;

            // change colors if 'colorInterval' frames have passed
			if (colorFramesPassed % colorInterval == 0) {
				nextSpriteNum();
			}
    	}
    }

}