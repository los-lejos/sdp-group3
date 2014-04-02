package shared;

public final class RobotInstructions {
	
	public static final byte LENGTH = 4;
	
	// e. g. send <robot> 1 <distance> <speed percentage>
	public static final byte MOVE = 1;
	
	// e. g. send <robot> 2 <heading high> <heading low> <speed percentage>
	public static final byte ROTATE = 2;
	
	// e. g. send <robot> 3 0 0 0
	public static final byte KICK = 3;
	
	// e. g. send <robot> 4 <distance> 0 0
	public static final byte LAT_MOVE = 4;
	
	/* e. g. "send a <robot> 4 10 5 0" (set track width to 105 mm, only for attacker)*/
	public static final byte SET_TRACK_WIDTH = 5;
	
	/* e. g. "send <robot> 5 75 0 0" (set travel speed to 75% of max capacity)*/
	public static final byte SET_TRAVEL_SPEED = 6;
	
	/* e. g. "send <robot> 6 75 0 0" (set rotate speed to 75% of max capacity)*/
	public static final byte SET_ROTATE_SPEED = 7;
	
	/* e. g. "send <robot> 8 0 0 0" */
	public static final byte OPEN_KICKER = 8;

	// To DICE
	public static final byte CAUGHT_BALL = -2;
	public static final byte RELEASED_BALL = -3;

}
