package robot.attacker;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import robot.Robot;

/*
 * @author Owen Gillespie
 */

public class AttackRobot extends Robot {
	
	private static final int BALL_SENSOR_NEARBY_CUTOFF = 14;
	private static final int BALL_SENSOR_IN_KICKER_CUTOFF = 10;

	private static final LightSensor leftLightSensor = new LightSensor(SensorPort.S4);
	private static final LightSensor rightLightSensor = new LightSensor(SensorPort.S1);
	private static final UltrasonicSensor ballSensor = new UltrasonicSensor(SensorPort.S2);

	public AttackRobot() {
		super(leftLightSensor, rightLightSensor,
				new AttackKickerController(),
				new AttackerMovementController());
	}
	
	@Override
	public boolean isDetectingBallInKicker() {
    	return ballSensor.getDistance() <= BALL_SENSOR_IN_KICKER_CUTOFF;
	}
	
	@Override
	public boolean isBallNearby() {
		return ballSensor.getDistance() <= BALL_SENSOR_NEARBY_CUTOFF;
	}
}