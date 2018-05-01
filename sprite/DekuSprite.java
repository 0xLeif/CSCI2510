package sprite;

import java.awt.event.*;
import java.net.URL;

import input.*;
import world.*;

public class DekuSprite extends Sprite {

	// sprite numbers for LDUR facing
	public static final int FACE_D = 0;
	public static final int FACE_U = 4;
	public static final int FACE_L = 12;
	public static final int FACE_R = 8;

	// 0-4 for subframes in 4 frame animation
	public static final int FRAME1 = 0;
	public static final int FRAME2 = 1;
	public static final int FRAME3 = 2;
	public static final int FRAME4 = 3;

	// booleans for keyboard input detection (WASD)
	public boolean kbUp;
	public boolean kbLeft;
	public boolean kbDown;
	public boolean kbRight;

	// direction + subframes
	public int facing; // which of 4 directions is he facing?
	public int facingFrame; // 1 of 4 subframes in 'x' direction
	public int frameInterval; // frames passed before changing facingFrame
	public int movingFramesPassed; // number of frames spent moving

	// movement
	public boolean moving; // if deku is currently moving
	public float vel; // movement speed

	// the bounding shape for deku is one rectangle
	private static final Vector2f[] boundVectors = { new Vector2f(0.025f, -0.025f), new Vector2f(-0.025f, -0.025f),
			new Vector2f(-0.025f, 0.01f), new Vector2f(0.025f, 0.01f) };

	// constructor: initialize values and create the sprite
	// as well as the bounding shape
	public DekuSprite(URL file) {
		super(file);
		VectorObject primaryBound = new VectorObject(boundVectors);
		addBound(primaryBound);

		vel = 0.02f; // velocity only applied while moving
		facing = FACE_D; // face downward first
		facingFrame = FRAME2; // second frame is "idle" frame
		frameInterval = 15; // frames until changing stepping frame
	}

	// set the position of deku but also move his bounding shape
	public void setPos(Vector2f newPos) {
		pos = newPos;
		bounds.get(0).position = newPos;
	}

	// update various characteristics of deku
	public void update(KeyboardInput kb, BGSprite bg) {

		// poll the keyboard to check for movement inputs
		kbUp = kb.keyDown(KeyEvent.VK_W);
		kbLeft = kb.keyDown(KeyEvent.VK_A);
		kbDown = kb.keyDown(KeyEvent.VK_S);
		kbRight = kb.keyDown(KeyEvent.VK_D);

		// if we are standing still...
		if (!moving) {
			// ...and if movement is detected...
			if (movementKeyIsPressed()) {
				facingFrame = FRAME3; // frame 3 is the first stepping frame
				moving = true; // start moving!
			}
		}
		// if we are already moving...
		else {
			// ...and no keys are pressed, stop!
			if (!movementKeyIsPressed()) {
				facingFrame = FRAME2; // frame 2 is a standstill frame
				moving = false; // stop moving!
			}
			// otherwise, continue in the same direction
			else {
				movingFramesPassed++; // record a movement frame
				calculateAndSetFacingFrame(); // determine which of 4 frames in
												// 'x' direction to use
			}
		}

		setFacingDirection(); // set the direction facing LDUR
		applyVelocity(bg); // apply a movement translation

		// set the number of the next sprite frame to render
		currentSpriteNum = facing + facingFrame;
	}

	// the movement frame should change every 'frameInterval' frames
	private void calculateAndSetFacingFrame() {
		if (movingFramesPassed % frameInterval == 0) {
			facingFrame = (facingFrame + 1) % 4;
		}
	}

	// based on keyboard input, change
	// the direction the sprite faces
	private void setFacingDirection() {
		// FACE_X is an int, denoting the first
		// instance of a sprite in
		// that direction on the sprite sheet
		if (kbUp) {
			facing = FACE_U;
		} else if (kbLeft) {
			facing = FACE_L;
		} else if (kbDown) {
			facing = FACE_D;
		} else if (kbRight) {
			facing = FACE_R;
		}
	}

	// return true if any of four movement keys WASD are pressed
	private boolean movementKeyIsPressed() {
		return kbUp || kbLeft || kbDown || kbRight;
	}

	// apply movement to the sprite
	public void applyVelocity(BGSprite bg) {
		// don't apply the movement, but record
		// what the change in pos WILL be
		Vector2f deltaPos = new Vector2f();
		if (kbUp) {
			deltaPos.y += vel;
		} else if (kbLeft) {
			deltaPos.x -= vel;
		} else if (kbDown) {
			deltaPos.y -= vel;
		} else if (kbRight) {
			deltaPos.x += vel;
		}

		// hold on to the old pos + the new pos
		Vector2f oldPos = new Vector2f(pos);
		Vector2f newPos = pos.add(deltaPos);

		// apply the new position change to the bounding shapes
		translateBounds(deltaPos);
		bounds.get(0).updateWorld();

		// this is necessary to make sure the bounding shapes
		// are in the right position for this timestep, otherwise
		// they will be one step behind!
		bounds.get(0).repopulateTransformedVectors();

		// move the sprite to the new position
		setPos(newPos);

		// if a collision with the background is detected
		if (isCollidingWith(bg)) {
			// use the old position pre-collision for the sprite
			// and bounding shapes instead of the new position
			bounds.get(0).position = oldPos;
			setPos(oldPos);
		}

		for (WallSprite wallSprite : bg.getList()) {
			if (isCollidingWith(wallSprite)) {
				bounds.get(0).position = oldPos;
				setPos(oldPos);
			}
		}

	}

}