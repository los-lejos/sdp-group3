package robot.attacker;

import robot.Robot;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;

/*
 * @author Joris Urbaitis
 */

public class AttackerMain {
	
	public static void main(String[] args) {
		LightSensor leftLightSensor = new LightSensor(SensorPort.S4);
		LightSensor rightLightSensor = new LightSensor(SensorPort.S1);
		
		Robot robot = new Robot(
				leftLightSensor, rightLightSensor,
				new AttackKickerController(),
				new AttackerMovementController(),
				new AttackerBallSensorController());

		robot.run();
	}
}