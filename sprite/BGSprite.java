package sprite;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

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

    private String[] levels = {"res/levels/maze_level_one.txt", "res/levels/maze_level_two.txt", "res/levels/end_level.txt"};
    private int currLevel = 0;
    private Vector2f torchPos = new Vector2f();
    private ArrayList<Vector2f> enemyPos = new ArrayList<>();
    private boolean isNewLevel = false;

    private ArrayList<WallSprite> list = new ArrayList<>();
    // constructor: create a bounding shape and apply transformations,
    // add to the list of bounding shapes once each is set
    public BGSprite(URL bgfile) {
        super(bgfile);
        generateMaze();
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

    public void generateMaze(){
        ArrayList<String[]> xyz = new ArrayList<>();
        list.clear();
        enemyPos.clear();
        try {
            String str = new String(Files.readAllBytes(Paths.get(levels[currLevel])));
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
                    switch (ele) {
                        case "2":
                            enemyPos.add(getPlayAreaLocation(new Vector2f(x,y)));
                            list.add(new WallSprite(new Vector2f(x++,y), ele));
                            break;
                        case "3":
                            torchPos = getPlayAreaLocation(new Vector2f(x,y));
                            list.add(new WallSprite(new Vector2f(x++,y), ele));
                        default:
                            list.add(new WallSprite(new Vector2f(x++,y), ele));
                            break;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            x = 0;
            y++;
        }
        createEnemies();
        isNewLevel = true;
        currLevel++;
    }

    @Override
    // render the bounding shapes of this sprite
    public void renderBoundingShapes(Graphics g) {
        for (VectorObject b : bounds) {
            b.render(g);
            for (WallSprite s : list){
                if(s.isSolid){
                    s.renderBoundingShapes(g);
                }
            }
        }
        for (Sprite s : GameStates.enemies){
            s.renderBoundingShapes(g);
        }
        isNewLevel = false;
    }

    @Override
    public void render(Graphics2D g2d, Matrix3x3f view) {
        super.render(g2d, view);
        for (WallSprite s : list) {
            if(s.isSolid) {
                s.updateWorldsForBounds();
                s.setViewsForBounds(view);
                s.render(g2d,view);
            }
        }
        for (Sprite s : GameStates.enemies){
            s.updateWorldsForBounds();
            s.setViewsForBounds(view);
            s.render(g2d,view);
        }
    }

    public Vector2f getTorchPos() {
        return torchPos;
    }

    private Vector2f getPlayAreaLocation(Vector2f gridLoc) {
        float x = (float) ((gridLoc.x - 7) / 7) * .75f;
        float y = (float) ((gridLoc.y - 7) / 7) * .75f;
        return new Vector2f(x, y);
    }

    public ArrayList<WallSprite> getList() {
        return list;
    }

    public void createEnemies(){
        Random random = new Random();
        for (Vector2f vectors : enemyPos){
            if(random.nextInt() % 2 == 0){
                GameStates.enemies.add(new JelloSprite(getClass().getResource("/res/img/jello_3x4.png")));
            }
            else {
                GameStates.enemies.add(new GhostSprite(getClass().getResource("/res/img/ghost_4x4.png")));
                ((GhostSprite) GameStates.enemies.get(GameStates.enemies.size() - 1)).setSpawnPos(vectors);
            }
            GameStates.enemies.get(GameStates.enemies.size() - 1).setPos(vectors);
        }
    }

    public boolean isNewLevel() {
        return isNewLevel;
    }
}