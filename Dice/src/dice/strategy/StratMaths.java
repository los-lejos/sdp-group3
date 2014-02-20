package dice.strategy;


import dice.state.GameObject;
import dice.state.Vector2;
import dice.state.WorldState;

/*
 * @author Sam Stern
 * @author Andrew Johnston
 */

public final class StratMaths {
	
	// maximum angle relative to the goal we'll tolerate shooting at it from
	public static final int SHOOT_ANGLE_TOLERANCE = 30; // arbitrary, adjust at your pleasure
	
	// tolerance if we want to find out if something's 'in the area of' a position
	public static final double POSITION_FUZZ = 10.0; // arbitrary, make it nicer
	
	public static boolean canReach(Vector2 v, GameObject o) {
		//TODO
		return true;
	}
	
	public static Vector2 whereToIntercept(GameObject target, GameObject ball) {
		//		return ball.projectPath.getCoordinateAt();
		return null;
	}
	
	public static Vector2 whereToRecievePass(WorldState state) {
		//TODO
		return null;
	}
	
	public static byte cartestanToPolarR(Vector2 v) {
		return (byte) Math.sqrt(Math.pow(v.X, 2) + Math.pow(v.Y, 2));
	}
	
	public static long cartesianToPolarTheta(Vector2 v) {
		return (long) Math.round(Math.toDegrees(Math.atan2(v.Y, v.X)));
	}
	
	public static boolean willCollideWithBall(GameObject target) {
		//TODO
		return false;
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

		// is where it is more-or-less in the same Y as me?
		if (whereIAm.Y >= (whereItIs.Y - POSITION_FUZZ) &&
				whereIAm.Y <= (whereItIs.Y + POSITION_FUZZ)) {
			return true;
		}
		
		return false;
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
}
