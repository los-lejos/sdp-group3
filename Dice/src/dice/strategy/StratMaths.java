package dice.strategy;


import dice.state.GameObject;
import dice.state.Vector2;

/*
 * @author Sam Stern
 */

final class StratMaths {
	
	protected static boolean canReach(Vector2 v, GameObject o) {
		//TODO
		return true;
	}
	
	public static Vector2 whereToIntercept(GameObject target, GameObject ball) {
//		return ball.projectPath.getCoordinateAt();
		return null;
	}
	
	protected static byte cartestanToPolarR(Vector2 v) {
		return (byte) Math.sqrt(Math.pow(v.X, 2) + Math.pow(v.Y, 2));
	}
	
	protected static long cartesianToPolarTheta(Vector2 v) {
		return (long) Math.round(Math.toDegrees(Math.atan2(v.Y, v.X)));
	}
	
	protected static boolean willCollideWithBall(GameObject target) {
		//TODO
		return false;
	}
	
	protected static Double getBetweenY(GameObject ball, Vector2 ourGoal) {
		return (ball.getPos().Y +ourGoal.Y)/2;
	}
	
	protected static Vector2 relativePos(GameObject referenceFrame, GameObject obj) {
		Vector2 rfPos = referenceFrame.getPos();
		Vector2 objPos = obj.getPos();
		return new Vector2(objPos.X-rfPos.X,objPos.Y-rfPos.Y);
	}
	protected static Vector2 relativePos(GameObject referenceFrame, Vector2 objPos) {
		Vector2 rfPos = referenceFrame.getPos();
		return new Vector2(objPos.X-rfPos.X,objPos.Y-rfPos.Y);
	}
}
