package robot.attacker;

import robot.Robot;
import robot.defender.DefenceKickerController;
import robot.defender.DefenderBallSensorController;
import robot.defender.DefenderMovementController;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;

/*
 * @author Joris Urbaitis
 */

public class AttackerMain {
	
	public static void main(String[] args) {
		LightSensor leftLightSensor = new LightSensor(SensorPort.S3);
		LightSensor rightLightSensor = new LightSensor(SensorPort.S4);

		Robot robot = new Robot(
				leftLightSensor, rightLightSensor,
    			new DefenceKickerController(),
    			new DefenderMovementController(),
    			new DefenderBallSensorController());
		
		robot.run();
	}
}