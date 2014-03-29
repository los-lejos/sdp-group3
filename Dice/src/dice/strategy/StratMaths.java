package dice.strategy;


import dice.state.BoundedLine;
import dice.state.GameObject;
import dice.state.Goal;
import dice.state.UnboundedLine;
import dice.state.Vector2;
import dice.state.WorldState;

/*
 * @author Sam Stern
 * @author Andrew Johnston
 */

public final class StratMaths {

	// tolerance if we want to find out if something's 'in the area of' a position
	public static final double POSITION_FUZZ = 20.0; // arbitrary, make it nicer

	public static final double BALL_SPEED_THRESH = 8;
	public static final double BALL_DISTANCE_THRESH = 60;
	private static final double MAX_SPEED_DIST = 200;
	private static final int MIN_SPEED = 30;
	private static final double SPEED_PER_DIST = (double)(100 - MIN_SPEED) / (MAX_SPEED_DIST - BALL_DISTANCE_THRESH);
	
	private static final double MAX_SPEED_ROT = Math.toRadians(170);
	private static final double MIN_SPEED_ROT = Math.toRadians(30);
	private static final int MAX_ROT_SPEED = 30;
	private static final int MIN_ROT_SPEED = 15;
	private static final double SPEED_PER_ROT = (double)(MAX_ROT_SPEED - MIN_SPEED) / (MAX_SPEED_ROT - MIN_SPEED_ROT);
	
	public static final double ROTATION_SHOOT_THRESH = Math.PI / 10;
	private static final double ROTATION_THRESH_MIN = Math.PI / 24;
	private static final double ROTATION_THRESH_MAX = Math.PI / 15;
	
	// Threshold for deciding if an object is nearby in Y
	public static final double Y_POS_THRESH = 35;
	
	public static final double CORRECTION_ROT_THRESH = ROTATION_THRESH_MIN;
	
	// We want to hit minimum threshold when we're at BALL_DISTANCE_THRESH
	private static final double ROTATION_THRESH_PER_DIST = ROTATION_THRESH_MIN / BALL_DISTANCE_THRESH; 
	
	public static final double SHOOT_AIM_ADJUSTMENT = 3;
	
	public static boolean canReach(Vector2 v, GameObject o) {
		//TODO
		return true;
	}
	
	public static Vector2 whereToIntercept(GameObject target, GameObject ball) {
		//		return ball.projectPath.getCoordinateAt();
		return null;
	}
	
	public static Vector2 whereToRecievePass(WorldState state) {
		//TODO needs to be properly implemented
		return state.getOurAttacker().getPos();
	}

	/**
	 * Method which will tell you whether an object is in front
	 * of another object (straight line...ish)
	 * 
	 * @param me object it might be in front of
	 * @param it object which might be in front of me
	 * @return true if yes, false if no
	 */
	public static boolean isInFrontOf(GameObject me, GameObject it) {
		Vector2 whereIAm = me.getPos();
		Vector2 whereItIs = it.getPos();
		
		Vector2 itStart = new Vector2(whereItIs.X, whereItIs.Y - POSITION_FUZZ);
		Vector2 itEnd = new Vector2(whereItIs.X, whereItIs.Y + POSITION_FUZZ);

		UnboundedLine meLine = new UnboundedLine(whereIAm, me.getRotation());
		BoundedLine itLine = new BoundedLine(itStart,itEnd);
		
		return (itLine.withinBounds(meLine.intersect(itLine)));
    }
	
	public static Double getBetweenY(GameObject ball, Vector2 ourGoal) {
		return (ball.getPos().Y +ourGoal.Y)/2;
	}
	
	public static Vector2 relativePos(GameObject referenceFrame, GameObject obj) {
		Vector2 rfPos = referenceFrame.getPos();
		Vector2 objPos = obj.getPos();
		return new Vector2(objPos.X-rfPos.X,objPos.Y-rfPos.Y);
	}
	
	public static Vector2 relativePos(GameObject referenceFrame, Vector2 objPos) {
		Vector2 rfPos = referenceFrame.getPos();
		return new Vector2(objPos.X-rfPos.X,objPos.Y-rfPos.Y);
	}
	
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
		
		if(dist <= BALL_DISTANCE_THRESH) {
			return MIN_SPEED;
		}
		
		double slowingDist = dist - BALL_DISTANCE_THRESH;
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
		
		double slowingRot = rot - MIN_SPEED_ROT;
		return (int) (100 - slowingRot * SPEED_PER_ROT);
	}

	/*
	 * if we are left of the goal shoot at the left post, if we are to the right of the goal then
	 * shoot at the right of the goal. otherwise shoot at the goal center
	 */
	public static Vector2 whereToShoot(GameObject robot, WorldState state) {
		
		Goal opGoal = state.getOppGoal();
		
		
		/*if (robot.getPos().Y > opGoal.getTopPost().Y) {
			return new Vector2(opGoal.getTopPost().X, opGoal.getTopPost().Y - SHOOT_AIM_ADJUSTMENT);
		} else if (robot.getPos().Y < opGoal.getBottomPost().Y){
			return new Vector2(opGoal.getBottomPost().X, opGoal.getBottomPost().Y+ SHOOT_AIM_ADJUSTMENT);
		} else {
			return opGoal.getGoalCenter();
		}
		*/
		
		return opGoal.getGoalCenter();
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
			WorldState.Side side) {
		if (side == WorldState.Side.LEFT) {
			return targetY - ourY;
		} else {
			return ourY - targetY;
		}
	}
}
