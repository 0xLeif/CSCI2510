package sprite;

import world.Vector2f;
import world.VectorObject;

import java.net.MalformedURLException;
import java.nio.file.Paths;

public class WallSprite extends Sprite {
    public boolean isSolid;

    public WallSprite(Vector2f location, String type) throws MalformedURLException {
        super(Paths.get("res/img/walltile_1x1.png").toUri().toURL());
        pos = getPlayAreaLocation(location);
        isSolid = type.equals("1");
        VectorObject bound = new VectorObject(boundVectors);
        bound.setPosition(pos);
        addBound(bound);
    }

    private Vector2f[] boundVectors = {
            new Vector2f(0.05f, -0.05f),
            new Vector2f(-0.05f, -0.05f),
            new Vector2f(-0.05f, 0.05f),
            new Vector2f(0.05f, 0.05f)
    };

    private Vector2f getPlayAreaLocation(Vector2f gridLoc) {
        float x = (float) ((gridLoc.x - 7) / 7) * .75f;
        float y = (float) ((gridLoc.y - 7) / 7) * .75f;
        return new Vector2f(x, y);
    }
}

