package dice.strategy;


import dice.state.GameObject;
import dice.state.Vector2;

/*
 * @author Sam Stern
 * @author Andrew Johnston
 */

public final class StratMaths {
	
	/*
	 * Distance and speed thresholds
	 */
	public static final double BALL_SPEED_THRESH = 8;
	public static final double BALL_DISTANCE_THRESH = 30;
	private static final double MIN_SPEED_DIST = 40;
	private static final double MAX_SPEED_DIST = 200;
	private static final int MIN_SPEED = 50;
	private static final double SPEED_PER_DIST = (double)(100 - MIN_SPEED) / (MAX_SPEED_DIST - MIN_SPEED_DIST);

	/*
	 * Rotation thresholds
	 */
	// We want to hit minimum threshold when we're at BALL_DISTANCE_THRESH
	private static final double MAX_SPEED_ROT = Math.toRadians(170);
	private static final double MIN_SPEED_ROT = Math.toRadians(30);
	private static final int MAX_ROT_SPEED = 40;
	private static final int MIN_ROT_SPEED = 20;
	private static final double SPEED_PER_ROT = (double)(MAX_ROT_SPEED - MIN_ROT_SPEED) / (MAX_SPEED_ROT - MIN_SPEED_ROT);
	
	public static final double ROTATION_SHOOT_THRESH = Math.PI / 14;
	private static final double ROTATION_THRESH_MIN = Math.PI / 20;
	private static final double ROTATION_THRESH_MAX = Math.PI / 14;
	
	private static final double ROTATION_THRESH_PER_DIST = ROTATION_THRESH_MIN / BALL_DISTANCE_THRESH; 
	
	// Threshold for deciding if an object is nearby in Y
	public static final double Y_POS_THRESH = 40;
	
	private static final double MAX_STRAFE_DIST = 80;
	
	public static final double CORRECTION_ROT_THRESH = Math.PI / 22;
	public static final double CORRECTION_POS_THRESH = 10;

	public static double getRotationThreshold(Vector2 obj, Vector2 target) {
		double dist = obj.getEuclidean(target);
		
		double threshold = dist * ROTATION_THRESH_PER_DIST;

		if(threshold > ROTATION_THRESH_MAX) {
			threshold = ROTATION_THRESH_MAX;
		} else if(threshold < ROTATION_THRESH_MIN) {
			threshold = ROTATION_THRESH_MIN;
		}
		
		return threshold;
	}
	
	public static int speedForDist(double dist) {
		dist = Math.abs(dist);
		
		if(dist >= MAX_SPEED_DIST) {
			return 100;
		}
		
		if(dist <= MIN_SPEED_DIST) {
			return MIN_SPEED;
		}
		
		double slowingDist = MAX_SPEED_DIST - dist;
		return (int) (100 - slowingDist * SPEED_PER_DIST);
	}
	
	public static int speedForRot(double rot) {
		rot = Math.abs(rot);
		
		if(rot >= MAX_SPEED_ROT) {
			return MAX_ROT_SPEED;
		}
		
		if(rot <= MIN_SPEED_ROT) {
			return MIN_ROT_SPEED;
		}
		
		double slowingRot = MAX_SPEED_ROT - rot;
		return (int) (MAX_ROT_SPEED - slowingRot * SPEED_PER_ROT);
	}

	/** Gets the angle relative to a point directly infront of the robot.
	 * 
	 * @param state - The world state
	 * @return the angle in radians
	 */
	public static double getAngleRelativeToHorizontal(GameObject target, boolean facingLeft) {
		// get the rotation relative to a point just "infront"
		// of the defender or attacker
		Vector2 pos = target.getPos();
		Vector2 forward = null;
		
		if(!facingLeft) {
			forward = new Vector2(pos.X + 1, pos.Y);
		} else {
			forward = new Vector2(pos.X - 1, pos.Y);
		}
		
		return target.getRotationRelativeTo(forward);
	}

	public static double getStrafeDist(double ourY, double targetY,
			boolean facingLeft) {
		double dist;
		
		if (!facingLeft) {
			dist = targetY - ourY;
		} else {
			dist = ourY - targetY;
		}
		
		if(Math.abs(dist) >= MAX_STRAFE_DIST) {
			if(dist < 0) {
				dist = -MAX_STRAFE_DIST;
			} else {
				dist = MAX_STRAFE_DIST;
			}
		}
		
		return dist;
	}
}
