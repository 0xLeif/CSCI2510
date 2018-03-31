package world;

import java.util.ArrayList;
import java.awt.*;

public class VectorObject implements Drawable {

    public ArrayList<Vector2f> vectors;                // Vectors centered around origin
    public ArrayList<Vector2f> transformedVectors;     // New vectors after applied transformations
    public Matrix3x3f world;                           // Used to apply translations, scales, and rotations
    public Matrix3x3f view;                            // Viewport

    // Current vector properties
    public Vector2f position;
    public float    scale;
    public float    rotation;
    public Color    color;


    // Constructor: creates a vector object from an array of Vector2f objects
    public VectorObject(Vector2f[] vectorsIn) {
        // instantiate arraylists for future use
        vectors = new ArrayList<>();
        transformedVectors = new ArrayList<>();

        // add vectors from the parameters to an arraylist
        // used to store the points of this vectorobject
        for(Vector2f v : vectorsIn) {
            vectors.add(v);
        }

        // initialize vector properties
        position = new Vector2f(0, 0);
        scale = 1;
        rotation = 0;
        color = Color.GREEN;
    }

    // Make changes to the world that transformations will be applied through.
    public void updateWorld() {
        world = Matrix3x3f.identity();
        world = world.mul(Matrix3x3f.scale(scale,scale));
        world = world.mul(Matrix3x3f.rotate(rotation));
        world = world.mul(Matrix3x3f.translate(new Vector2f(position.x, position.y)));
    }

    // Render the vector object with the applied tranformations
    // Also hold on to the list of transformed points so that they
    // don't have to be recalulated with getWorldVectors() every time
    public void render(Graphics g) {
        Vector2f[] newVectors = getWorldVectors();  // Apply transformations to all vectors
        
        g.setColor(color);
        
        
        transformedVectors.clear();   // Empty the list of transformed vectors
                                      // to make room for new ones
        Vector2f prev = newVectors[newVectors.length - 1];
        Vector2f next = null;
        for (int i = 0; i < newVectors.length; i++) {
            transformedVectors.add(prev); // Store the transformed point
            next = newVectors[i];         // setup the "next" point
            
            // Apply viewport transformations and draw from that instead
            Vector2f prevView = view.mul(prev);
            Vector2f nextView = view.mul(next);
            g.drawLine((int)prevView.x, (int)prevView.y, (int)nextView.x, (int)nextView.y);
            
            // reassign prev value for next loop iteration
            prev = next;
        }
    }

    // Create a list of Vector2f with world transformations applied
    public Vector2f[] getWorldVectors() {
        Vector2f[] worldVectors = new Vector2f[vectors.size()];
        for (int i = 0; i < worldVectors.length; i++) {
            worldVectors[i] = new Vector2f(world.mul(vectors.get(i)));
        }
        return worldVectors;
    }
    
    // reapply transformations to vectors when the world is updated
    // and the transformed vectors are old and needs to also be updated
    public void repopulateTransformedVectors() {
    	transformedVectors.clear();
        Vector2f[] newVectors = getWorldVectors();  // Apply transformations to all vectors
        for (Vector2f nV : newVectors) {
        	transformedVectors.add(nV);
        }

    }

    // Return a list of world-transformed vectors
    public ArrayList<Vector2f> getTransformedVectors() {
        return transformedVectors; 
    }

    // calculate a square hitbox for this
    // vectorobject by finding its corners
    private Vector2f[] calculateHitbox() {
        // Vectors for bottom-left, top-right
        Vector2f bl, tr;

        if (getTransformedVectors().size() == 0) {
            bl = tr = new Vector2f(0.0f, 0.0f);
        }
        else {
            // create new instances so that they don't reference from the list
            bl = new Vector2f(transformedVectors.get(0));
            tr = new Vector2f(transformedVectors.get(0));

            // find the min & max for the x & y 
            for (Vector2f v : transformedVectors) {
                if (v.x < bl.x) bl.x = v.x;
                if (v.x > tr.x) tr.x = v.x;
                if (v.y < bl.y) bl.y = v.y;
                if (v.y > tr.y) tr.y = v.y;
            }
        }
        // return a 2D array of vector2f objects where
        // [0] bottom left corner, [1] top right corner
        return new Vector2f[] {bl, tr};
    }

    // Check if the vector is within the object's hitbox
    public boolean vectorIsWithin(Vector2f v) {
        Vector2f[] corners = this.calculateHitbox();
        Vector2f bl = corners[0];
        Vector2f tr = corners[1];
        // return if vector is within the square hitbox
        return v.x >= bl.x
            && v.x <= tr.x
            && v.y >= bl.y
            && v.y <= tr.y;
    }

    // check if one VectorObject has collided with another
    public boolean isCollidingWith(VectorObject obj) {
        // check if any points on the object is within this one
        for (Vector2f ov : obj.transformedVectors) {
            if (this.vectorIsWithin(ov)) {
                return true;
            }
        }
        return false;
    }

    // set the viewport matrix
    public void setView(Matrix3x3f viewIn) {
        view = viewIn;
    }

    // get the object's position
    public Vector2f getPosition() {
        return position;
    }

    // set the object's position
    public void setPosition(Vector2f positionIn) {
        position = positionIn;
    }

    // get the object's scale
    public float getScale() {
        return scale;
    }

    // set the object's scale
    public void setScale(float scaleIn) {
        scale = scaleIn;
    }

    // get the object's rotation
    public float getRotation() {
        return rotation;
    }

    // set the object's rotation
    public void setRotation(float rotationIn) {
        rotation = rotationIn;
    }

    // get the object's color
    public Color getColor() {
        return color;
    }

    // set the object's color
    public void setColor(Color colorIn) {
        color = colorIn;
    }
}