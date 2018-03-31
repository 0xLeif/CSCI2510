package sprite;

import java.net.URL;

import world.*;

public class BGSprite extends Sprite {

    // the vectors for one long rectangle, four are used
    // to create all boundaries of the background
    private static final Vector2f[] boundVectors = {
        new Vector2f(1.0f, -0.3f),
        new Vector2f(-1.0f, -0.3f), 
        new Vector2f(-1.0f, 0.3f), 
        new Vector2f(1.0f, 0.3f)
    };

    // constructor: create a bounding shape and apply transformations,
    // add to the list of bounding shapes once each is set
    public BGSprite(URL file) {
        super(file);
        
        // left boundary
        VectorObject leftBound = new VectorObject(boundVectors);
        leftBound.rotation = (float)Math.toRadians(90.0);
        leftBound.position = new Vector2f(-1.0f, 0.0f);
        addBound(leftBound);

        // bottom boundary
        VectorObject bottomBound = new VectorObject(boundVectors);
        bottomBound.position = new Vector2f(0.3f, -0.97f);
        addBound(bottomBound);

        // top boundary        
        VectorObject topBound = new VectorObject(boundVectors);
        topBound.scale = 0.8f;
        topBound.position = new Vector2f(0.1f, 1.12f);
        addBound(topBound);

        // right boundary
        VectorObject rightBound = new VectorObject(boundVectors);
        rightBound.scale = 0.775f;
        rightBound.rotation = (float)Math.toRadians(90.0);
        rightBound.position = new Vector2f(1.1f, 0.1f);
        addBound(rightBound);
    }

}