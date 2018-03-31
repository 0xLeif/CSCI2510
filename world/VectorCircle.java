package world;

import java.awt.*;

public class VectorCircle extends VectorObject {

    // the radius of the circle
    public float radius;

    // construct a circle, a single point with a radius
    public VectorCircle(Vector2f center, float radiusIn) {
        super(new Vector2f[] {center});
        radius = radiusIn;
    }

    // a vector is within a VectorCircle if the distance
    // from that vector to the center of the circle is
    // less than the radius of the circle
    @Override
    public boolean vectorIsWithin(Vector2f p) {
        Vector2f newPoint = view.mul(p);
        Vector2f center = view.mul(transformedVectors.get(0));
        double a = Math.pow(newPoint.x - center.x, 2);
        double b = Math.pow(newPoint.y - center.y, 2);
        double hyp = Math.sqrt(a + b);
        return hyp < radius;
    }

    // render this as a circle, rather than point to point
    @Override
    public void render(Graphics g) {
        // redundant to match the render function of the superclass
        Vector2f[] newVectors = getWorldVectors();
        g.setColor(Color.GREEN);
        transformedVectors.clear(); 
        transformedVectors.add(newVectors[0]);

        // apply viewport transformations and draw the circle
        Vector2f newCenter = view.mul(transformedVectors.get(0));
        Vector2f tl = new Vector2f(newCenter.x - radius, newCenter.y - radius);
        g.drawOval((int) tl.x, (int) tl.y, (int) radius*2, (int) radius*2);
    }

}