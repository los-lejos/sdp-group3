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
	public static final double POSITION_FUZZ = 10.0; // arbitrary, make it nicer
	
	public static final double ROTATION_SHOOT_THRESH = Math.PI / 8;
	private static final double ROTATION_THRESH_MIN = Math.PI / 20;
	private static final double ROTATION_THRESH_MAX = Math.PI / 10;
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
	
	public static double getRotationTreshold(Vector2 obj, Vector2 target) {
		double dist = obj.getEuclidean(target);
		
		double threshold = dist * (Math.PI / 1000.0);
		
		if(threshold > ROTATION_THRESH_MAX) {
			threshold = ROTATION_THRESH_MAX;
		} else if(threshold < ROTATION_THRESH_MIN) {
			threshold = ROTATION_THRESH_MIN;
		}
		
		return threshold;
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
}
