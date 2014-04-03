package robot.attacker;

import robot.ColorBallSensorController;
import robot.HolonomicMovementController;
import robot.Robot;

/*
 * @author Joris Urbaitis
 */

public class AttackerMain {
	
	public static void main(String[] args) {
		Robot robot = new Robot(
    			new AttackerKickerController(),
    			new HolonomicMovementController(true),
    			new ColorBallSensorController());
		
		robot.run();
	}
}