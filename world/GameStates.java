package world;

import sprite.Sprite;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.AreaAveragingScaleFilter;
import java.util.*;
public class GameStates {
	public static boolean isPlaying = false;
	public static int gameTime = 60;
	public static Timer timer = new Timer();
	public static ArrayList<Sprite> enemies = new ArrayList<>();
}