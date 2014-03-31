package robot.attacker;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import robot.ColorBallSensorController;
import robot.HolonomicMovementController;
import robot.Robot;

/*
 * @author Joris Urbaitis
 */

public class AttackerMain {
	
	public static void main(String[] args) {
		LightSensor leftLightSensor = new LightSensor(SensorPort.S3);
		LightSensor rightLightSensor = new LightSensor(SensorPort.S4);

		Robot robot = new Robot(
				leftLightSensor, rightLightSensor,
    			new AttackerKickerController(),
    			new HolonomicMovementController(),
    			new ColorBallSensorController());
		
		robot.run();
	}
}