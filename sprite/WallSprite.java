package sprite;

import world.Vector2f;
import java.net.MalformedURLException;
import java.nio.file.Paths;

public class WallSprite extends Sprite{
    public boolean isSolid;
    public WallSprite(Vector2f location, String type) throws MalformedURLException {
        super(Paths.get("res/walltile_1x1.png").toUri().toURL());
        pos = GetPlayAreaLocation(location);
        //System.out.println(pos);
        isSolid = type.equals("1");
    }

    private static final Vector2f[] boundVectors = {
            new Vector2f(0.11f, -0.11f),
            new Vector2f(-0.11f, -0.11f),
            new Vector2f(-0.11f, 0.11f),
            new Vector2f(0.11f, 0.11f)
    };

    private Vector2f GetPlayAreaLocation(Vector2f gridLoc){
        float x = (float) ((gridLoc.x - 7) / 7) * .75f;
        float y = (float) ((gridLoc.y - 7) / 7) * .75f;
        return new Vector2f(x,y);
    }
}

