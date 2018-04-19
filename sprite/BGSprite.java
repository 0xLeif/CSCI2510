package sprite;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

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
    private ArrayList<WallSprite> list = new ArrayList<>();
    // constructor: create a bounding shape and apply transformations,
    // add to the list of bounding shapes once each is set
    public BGSprite(URL bgfile) {
        super(bgfile);
        GenerateMaze("input/maze_level_one.txt");
        // left boundary
        VectorObject leftBound = new VectorObject(boundVectors);
        leftBound.rotation = (float)Math.toRadians(90.0);
        leftBound.position = new Vector2f(-1.1f, 0.0f);
        addBound(leftBound);

        // bottom boundary
        VectorObject bottomBound = new VectorObject(boundVectors);
        bottomBound.position = new Vector2f(0f, -1.1f);
        addBound(bottomBound);

        // top boundary        
        VectorObject topBound = new VectorObject(boundVectors);
        topBound.position = new Vector2f(0f, 1.1f);
        addBound(topBound);

        // right boundary
        VectorObject rightBound = new VectorObject(boundVectors);
        rightBound.rotation = (float)Math.toRadians(90.0);
        rightBound.position = new Vector2f(1.1f, 0f);
        addBound(rightBound);
    }

    private void GenerateMaze(String mazeFile){
        ArrayList<String[]> xyz = new ArrayList<>();
        try {
            String str = new String(Files.readAllBytes(Paths.get(mazeFile)));
            for(String line : str.split("\n")) {
                xyz.add(line.split(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        int x, y;
        x = y = 0;
        for(String[] line : xyz) {
            for(String ele : line) {
                try {
                    list.add(new WallSprite(new Vector2f(x++,y), ele));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            x = 0;
            y++;
        }
    }

    @Override
    public void render(Graphics2D g2d, Matrix3x3f view) {
        super.render(g2d, view);
        for (WallSprite s : list) {
            if(s.isSolid) {
                s.render(g2d,view);
            }
        }
    }
}