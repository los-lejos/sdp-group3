package dice.strategy;

import dice.communication.RobotType;
import dice.state.Vector2;

/*
 * @author Sam Stern
 * 
 * class containing methods which determine where to kick the ball.
 */

public class KickTargets {
	
	private RobotType target;

	
	public KickTargets(RobotType target){
		this.target = target;
	}
	
	// where to aim when kicking at the goal
	public static Vector2 shootTarget() {
		//TODO
		return null;
	}

}
