package robot.defender;

import robot.ColorBallSensorController;
import robot.HolonomicMovementController;
import robot.Robot;

/*
 * @author Joris Urbaitis
 */

public class DefenderMain {
	
	public static void main(String[] args) {
		Robot robot = new Robot(
    			new DefenderKickerController(),
    			new HolonomicMovementController(false),
    			new ColorBallSensorController(false));
		
		robot.run();
	}
}
 