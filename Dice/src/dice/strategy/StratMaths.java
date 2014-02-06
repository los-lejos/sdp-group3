package dice.strategy;


import dice.state.GameObject;
import dice.state.Vector2;

/*
 * @author Sam Stern
 * 
 * class containing methods which determine where to kick the ball.
 */

final class StratMaths {

	protected static Vector2 getShootVector() {
		//TODO
		return null;
	}
	
	protected static boolean canBlock(Vector2 v, GameObject o) {
		//TODO
		return true;
		
	}
	
	protected static byte cartestanToPolarR(Vector2 v) {
		return (byte) Math.sqrt(Math.pow(v.X, 2) + Math.pow(v.Y, 2));
	}
	
	protected static long cartesianToPolarTheta(Vector2 v) {
		return Math.round(Math.toDegrees(Math.atan2(v.Y, v.X)));
	}
	
	protected static boolean willCollideWithBall() {
		//TODO
		return false;
	}
}
