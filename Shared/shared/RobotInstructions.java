package shared;

public final class RobotInstructions {
	
	public static final byte LENGTH = 5;
	
	// To robot
	public static final byte MOVE_TO = 1;
	public static final byte KICK_TOWARD = 2;
	public static final byte LAT_MOVE_TO = 3;
	public static final byte SET_TRACK_WIDTH = 4;
	public static final byte SET_TRAVEL_SPEED = 5;
	public static final byte SET_ROTATE_SPEED = 6;

	
	// To DICE
	public static final byte CAUGHT_BALL = -2;
	public static final byte RELEASED_BALL = -3;

}
