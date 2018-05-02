package sprite;

import sound.SoundEffectManager;
import world.Matrix3x3f;
import world.Vector2f;
import world.VectorObject;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Random;

public class JelloSprite extends Sprite {

    boolean visible;

    SoundEffectManager soundEffectManager;
    boolean soundPlayed;

    Vector2f spawnPos;

    // growth ability
    public boolean changingSize; 	// if 'grow' ability in progress
    public boolean increasing; 		// next ability: grow or shrink?
    public float growMax; 			// maximum size
    public float growMin; 			// minimum size
    public float growScale; 		// deku's current size
    public float growRate; 			// rate of growth

    int scaleTimer;

    private static final Vector2f[] boundVectors = {
            new Vector2f(0.075f / 2, -0.119f / 2),
            new Vector2f(-0.075f / 2, -0.119f / 2),
            new Vector2f(-0.075f / 2, 0.119f / 2),
            new Vector2f(0.075f / 2, 0.119f / 2)
    };

    private static final Vector2f[] visionBounds = {
            new Vector2f(0.4f / 3, -0.4f / 3),
            new Vector2f(-0.4f / 3, -0.4f / 3),
            new Vector2f(-0.4f / 3, 0.4f / 3),
            new Vector2f(0.4f / 3, 0.4f / 3)
    };

    private static final Vector2f[] audioBounds = {
            new Vector2f(0.6f / 3, -0.6f / 3),
            new Vector2f(-0.6f / 3, -0.6f / 3),
            new Vector2f(-0.6f / 3, 0.6f / 3),
            new Vector2f(0.6f / 3, 0.6f / 3)
    };

    VectorObject primaryBound, audioBound, visionBound;

    public JelloSprite(URL file) {
        super(file);

        primaryBound = new VectorObject(boundVectors);
        addBound(primaryBound);

        visionBound = new VectorObject(visionBounds);
        addBound(visionBound);

        audioBound = new VectorObject(audioBounds);
        addBound(audioBound);

        visible = false;
        soundEffectManager = new SoundEffectManager();
        soundPlayed = false;
        currentSpriteNum = 3;

        changingSize = false; 	// if 'grow' ability in progress
        growMax = 0.70f; 		// maximum size
        growMin = 0.30f; 		// minimum size
        growRate = 0.05f; 		// rate of growth

        scaleTimer = 0;
    }

    public void setPos(Vector2f newPos) {
        pos = newPos;
        for(VectorObject b : bounds) {
            b.position = newPos;
        }
    }

    public void setSpawnPos(Vector2f p) {
        spawnPos = p;
        pos = p;
        for(VectorObject b : bounds) {
            b.position = p;
        }
    }

    public void update(PlayerSprite ds, BGSprite bg) {
        if(visionBound.isCollidingWith(ds.bounds.get(0))) {
            visible = true;
        }
        else {
            visible = false;
        }
        if(audioBound.isCollidingWith(ds.bounds.get(0))) {
            if(!soundPlayed) {
                Random r = new Random();
                int randomSound = r.nextInt(100);
                switch (randomSound) {
                    case 0:
                        soundEffectManager.playSound("jello1");
                        soundPlayed = true;
                        break;
                    case 1:
                        soundEffectManager.playSound("jello2");
                        soundPlayed = true;
                        break;
                }
            }
        }
        else
            soundPlayed = false;

        if(scaleTimer <= 150)
            scaleTimer++;
        else
            scaleTimer = 0;
        if(scaleTimer >= 75) {
            scaleDecrease();
        }
        else {
            scaleIncrease();
        }
    }

    // increase jello's scale as well as his bounding shape
    private void scaleIncrease() {
        // apply adjust but keep it within the min/max
        scale = Math.max(growMin, scale - growRate);
        bounds.get(0).scale = scale;

        // update world now that scale has changed
        bounds.get(0).updateWorld();

        // transformations needs to be reapplied for
        // proper collision detection (NOT EFFICIENT)
        bounds.get(0).repopulateTransformedVectors();
    }

    // decrease jello's scale as well as his bounding shape
    private void scaleDecrease() {
        // apply adjust but keep it within the min/max
        scale = Math.min(growMax, scale + growRate);
        bounds.get(0).scale = scale;

        // update world now that scale has changed
        bounds.get(0).updateWorld();

        // transformations needs to be reapplied for
        // proper collision detection (NOT EFFICIENT)
        bounds.get(0).repopulateTransformedVectors();
    }

    // stop changing size if the min/max is met
    private boolean shouldStopChangingSize() {
        return scale == growMin || scale == growMax;
    }


    @Override
    public boolean isCollidingWith(Sprite other) {
        // for every bounding shape of this object...
        for (VectorObject localBound : bounds) {
            if(localBound == bounds.get(0)) {
                // ...and for every point on this bounding shape...
                for (Vector2f localPoint : localBound.transformedVectors) {
                    // check if that point is within the current sprite's
                    // series of bounding shapes
                    for (VectorObject otherBound : other.bounds) {
                        if (otherBound.vectorIsWithin(localPoint)) {
                            return true;
                        }
                    }
                }
            }
        }
        // return false if no collision is detected
        return false;
    }

    @Override
    public void render(Graphics2D g2d, Matrix3x3f view) {
        // get one sprite from the sheet,
        // apply the viewport transformation,
        // and render it to the g2d instance
        BufferedImage img = getSpriteFromSheet(currentSpriteNum);
        AffineTransform transformImg = getTransform(img, view);
        if(visible)
            g2d.drawImage(img, transformImg, null);
    }
}
