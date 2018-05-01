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
import java.util.Vector;

public class GhostSprite extends Sprite {
    public static final int FACE_D = 0;
    public static final int FACE_L = 4;
    public static final int FACE_R = 8;
    public static final int FACE_U = 12;

    public int facing; // which of 4 directions is he facing?

    boolean visible;

    SoundEffectManager soundEffectManager;
    boolean soundPlayed;
    boolean chasingPlayer;
    boolean sawPlayer;
    int chaseTimer;
    Vector2f spawnPos;

    private static final Vector2f[] boundVectors = {
            new Vector2f(0.075f / 2, -0.119f / 2),
            new Vector2f(-0.075f / 2, -0.119f / 2),
            new Vector2f(-0.075f / 2, 0.119f / 2),
            new Vector2f(0.075f / 2, 0.119f / 2)
    };

    private static final Vector2f[] visionBounds = {
            new Vector2f(0.45f / 2, -0.45f / 2),
            new Vector2f(-0.45f / 2, -0.45f / 2),
            new Vector2f(-0.45f / 2, 0.45f / 2),
            new Vector2f(0.45f / 2, 0.45f / 2)
    };

    private static final Vector2f[] audioBounds = {
            new Vector2f(0.6f / 2, -0.6f / 2),
            new Vector2f(-0.6f / 2, -0.6f / 2),
            new Vector2f(-0.6f / 2, 0.6f / 2),
            new Vector2f(0.6f / 2, 0.6f / 2)
    };

    VectorObject primaryBound, visionBound, audioBound;


    public GhostSprite(URL file) {
        super(file);

        primaryBound = new VectorObject(boundVectors);
        addBound(primaryBound);

        visionBound = new VectorObject(visionBounds);
        addBound(visionBound);

        audioBound = new VectorObject(audioBounds);
        addBound(audioBound);

        setFacing(FACE_D);
        visible = false;
        sawPlayer = false;
        soundEffectManager = new SoundEffectManager();
        soundPlayed = false;
        chasingPlayer = false;
        chaseTimer = 0;
    }

    public void setPos(Vector2f newPos) {
        pos = newPos;
        for(VectorObject b : bounds) {
            b.position = newPos;
        }
    }

    private void setFacing(int direction) {
        facing = direction;
        currentSpriteNum = facing;
    }

    public void update(DekuSprite ds, BGSprite bg) {
        if(visionBound.isCollidingWith(ds.bounds.get(0))) {
            visible = true;
            sawPlayer = true;
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
                        soundEffectManager.playSound("ghost1");
                        soundPlayed = true;
                        break;
                    case 1:
                        soundEffectManager.playSound("ghost2");
                        soundPlayed = true;
                        break;
                }
            }
        }
        else
            soundPlayed = false;

        move(sawPlayer, bg, ds);
    }

    private void move(boolean afterPlayer, BGSprite bg, DekuSprite ds) {
        Vector2f deltaPos = new Vector2f();
        float vel;

        if(!afterPlayer) {
            Random r = new Random();
            vel = 0.03f;
            int direction = r.nextInt(150);
            switch (direction) {
                case 0: // down
                    deltaPos.y -= vel;
                    setFacing(FACE_D);
                    break;
                case 1: // right
                    deltaPos.x += vel;
                    setFacing(FACE_R);
                    break;
                case 2: // up
                    deltaPos.y += vel;
                    setFacing(FACE_U);
                    break;
                case 3: // left
                    deltaPos.x -= vel;
                    setFacing(FACE_L);
                    break;
            }
        }
        else { // if after the player
            if(!chasingPlayer) {
                Random r = new Random();
                chaseTimer = r.nextInt(500) + 300;
                chasingPlayer = true;
            }
            System.out.println("chaseTimer: " + chaseTimer);
            chaseTimer--;

            if(chaseTimer <= 0 && !visible) {
                chasingPlayer = false;
                sawPlayer = false;
                setPos(spawnPos);
            }
            else {
                vel = 0.005f;

                float xDist = ds.pos.x - pos.x;
                float yDist = ds.pos.y - pos.y;

                float h = (float) Math.sqrt(xDist * xDist + yDist * yDist);
                xDist /= h;
                yDist /= h;

                pos.x += vel * xDist;
                pos.y += vel * yDist;

                if (xDist > 0)
                    setFacing(FACE_R);
                else
                    setFacing(FACE_L);
            }
        }

        // hold on to the old pos + the new pos
        Vector2f oldPos = new Vector2f(pos);
        Vector2f newPos = pos.add(deltaPos);

        // apply the new position change to the bounding shapes
        translateBounds(deltaPos);

        for(VectorObject b : bounds) {
            b.updateWorld();
            b.repopulateTransformedVectors();
        }

        setPos(newPos);

        //check for colliding with background
        if (isCollidingWith(bg)) {
            // use the old position pre-collision for the sprite
            // and bounding shapes instead of the new position
            for (VectorObject b : bounds) {
                b.position = oldPos;
                setPos(oldPos);
            }
        }
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

    public boolean isChasingPlayer() {
        return chasingPlayer;
    }
}
