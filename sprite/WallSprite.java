package sprite;

import world.Matrix3x3f;
import world.Vector2f;

import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

/**
 * Created by ekbuc on 4/18/2018.
 */
public class WallSprite extends Sprite{
    public boolean isSolid;
    public WallSprite(Vector2f location, String type) throws MalformedURLException {
        super(Paths.get("res/walltile_1x1.png").toUri().toURL());
        pos = GetPlayAreaLocation(location);
        System.out.println(pos);
        isSolid = type.equals("1");
    }

    private static final Vector2f[] boundVectors = {
            new Vector2f(0.11f, -0.11f),
            new Vector2f(-0.11f, -0.11f),
            new Vector2f(-0.11f, 0.11f),
            new Vector2f(0.11f, 0.11f)
    };

    private Vector2f GetPlayAreaLocation(Vector2f gridLoc){
        float x = (gridLoc.x - 7) / 7;
        float y = (gridLoc.y - 7) / 7;
        float normX = x > 0 ? x - 0.2f : x + 0.2f;
        float normY = y > 0 ? y - 0.2f : y + 0.2f;
        return new Vector2f(normX,normY);
    }
}

