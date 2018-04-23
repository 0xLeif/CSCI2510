package sprite;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.awt.geom.AffineTransform;

import java.io.IOException;

import util.Utility;
import world.*;

public class Sprite {

    public BufferedImage sheet;     // the whole sprite sheet
    public int spriteWidth;         // width  of one sprite
    public int spriteHeight;        // height of one sprite
    public int numSpritesHorizontal;// number of sprites in one row
    public int numSpritesVertical;  // number of rows of sprites
    public int currentSpriteNum;    // the current selected sprite from the sheet

    // sprite transformation characteristics
    public Vector2f pos;
    public float rot;
    public float scale;

    // a series of bounding shapes made of VectorObjects
    public ArrayList<VectorObject> bounds;

    // constructor: load a sprite from a file
    public Sprite(URL file) {
        // load the sprite sheet as a buffered image
        try {
            sheet = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
            sheet = null;
        }

        currentSpriteNum = 0;       // initialize to the first sprite on the sheet
        pos = new Vector2f();       // position the sprite in the center
        rot = 0.0f;                 // don't apply a rotation
        scale = 1.0f;               // don't apply a scale
        bounds = new ArrayList<>(); // instantiate the list of bounds


        // find the size of a sprite as
        // well as the sheet dimensions
        parseAndSetSheetDimensions(file);
    }

    // get a single sprite from the spritesheet
    public BufferedImage getSpriteFromSheet(int spriteNum) {
        // determine coordinates of a n*m spritesheet:
        // it is indexed like a 2d array where the top-left
        // corner is [0][0], next to the right is [0][1]...
        int spriteRowIndex = spriteNum / numSpritesHorizontal;
        int spriteColIndex = spriteNum % numSpritesHorizontal;

        // find the top-left x,y pixel coordinate of this sprite
        int spriteX = spriteColIndex * spriteWidth;
        int spriteY = spriteRowIndex * spriteHeight;

        // return a subimage of the sheet
        return sheet.getSubimage(spriteX, spriteY, spriteWidth, spriteHeight);
    }
    
    // find the size of a sprite as well as the sheet dimensions
    private void parseAndSetSheetDimensions(URL file) {
        // remove the path from the filename and remove the extension
        String fileName = file.getFile();
        fileName = fileName.substring( fileName.lastIndexOf('/')+1, fileName.length() );
        fileName = fileName.substring(0, fileName.lastIndexOf('.'));

        // if a filename is 'sprite_2x3' then the spritesheet is 2x3,
        // so 2 sprites in a row, 3 rows total
        // we need to separate those values from the filename
        String dimensions = fileName.substring(fileName.indexOf('_') + 1);
        String[] numberStrings = dimensions.split("x");
        numSpritesHorizontal = Integer.parseInt(numberStrings[0]);
        numSpritesVertical   = Integer.parseInt(numberStrings[1]);

        // calculate and set the size of a single sprite from the sheet
        spriteWidth  = sheet.getWidth()  / numSpritesHorizontal;
        spriteHeight = sheet.getHeight() / numSpritesVertical;
    }

    // transform the pos, rot, scale of this sprite
    public AffineTransform getTransform(BufferedImage sprite, Matrix3x3f view) {
        Vector2f s = view.mul(pos);
        AffineTransform transform = AffineTransform.getTranslateInstance(s.x, s.y);
        transform.scale(scale, scale);
        transform.rotate(rot);
        transform.translate(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
        return transform;
    }

    // get current number of sprite from the sheet
    public int getCurrentSpriteNum() {
        return currentSpriteNum;
    }

    // set the current sprite number from the sheet
    public void setCurrentSpriteNum(int cs) {
        currentSpriteNum = cs;
    }

    // change to the next sprite on the sheet
    public void nextSpriteNum() {
        currentSpriteNum++;
        if (currentSpriteNum > getTotalNumSprites()-1) {
            currentSpriteNum = 0;
        }
    }

    // change to the previous sprite on the sheet
    public void prevSpriteNum() {
        currentSpriteNum--;
        if (currentSpriteNum < 0) {
            currentSpriteNum = getTotalNumSprites()-1;
        }
    }

    // apply a translation to all bounding shapes
    public void translateBounds(Vector2f deltaPos) {
        for (VectorObject b : bounds) {
            b.position = b.position.add(deltaPos);
        }
    }
    
    // check if this sprite is colliding with some
    // other Sprite by comparing bounding shapes
    public boolean isCollidingWith(Sprite other) {
    	// for every bounding shape of this object...
    	for (VectorObject localBound : bounds) {
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
        // return false if no collision is detected
    	return false;
    }

    // render the sprite
    public void render(Graphics2D g2d, Matrix3x3f view) {
        // get one sprite from the sheet,
        // apply the viewport transformation,
        // and render it to the g2d instance
        BufferedImage img = getSpriteFromSheet(currentSpriteNum);
        AffineTransform transformImg = getTransform(img, view);
        g2d.drawImage(img, transformImg, null);
    }

    // render the bounding shapes of this sprite
    public void renderBoundingShapes(Graphics g) {
        for (VectorObject b : bounds) {
            b.render(g);
        }
    }

    // return the total number of sprites on the sheet
    public int getTotalNumSprites() {
        return numSpritesHorizontal * numSpritesVertical;
    }

    // add an additional bounding shape to the sprite
    public void addBound(VectorObject bound) {
        bounds.add(bound);
    }

    // get the list of bounding shapes
    public ArrayList<VectorObject> getBounds() {
        return bounds;
    }

    // apply viewport transformations to the bounds to
    // match transformations made to the sprite
    public void setViewsForBounds(Matrix3x3f viewport) {
        for (VectorObject b : bounds) {
            b.setView(viewport);
        }
    }

    // update the world matrix for the bounds
    public void updateWorldsForBounds() {
        for (VectorObject b : bounds) {
            b.updateWorld();
        }
    }
}