package shared;

public final class RobotInstructions {
	
	public static final byte LENGTH = 5;
	
	// To robot
	public static final byte MOVE_TO = 1;
	/* e. g. "send <robot> 1 10 5 10" (turn 105 degrees and go 10 cm(?) forward)*/
	public static final byte KICK_TOWARD = 2;
	/* TODO ??? */
	public static final byte LAT_MOVE_TO = 3;
	/* TODO ??? */
	public static final byte SET_TRACK_WIDTH = 4;
	/* e. g. "send a <robot> 4 10 5 0" (set track width to 105 mm, only for attacker)*/
	public static final byte SET_TRAVEL_SPEED = 5;
	/* e. g. "send <robot> 5 75 0 0" (set travel speed to 75% of max capacity)*/
	public static final byte SET_ROTATE_SPEED = 6;
	/* e. g. "send <robot> 6 75 0 0" (set rotate speed to 75% of max capacity)*/

	
	// To DICE
	public static final byte CAUGHT_BALL = -2;
	public static final byte RELEASED_BALL = -3;

}
