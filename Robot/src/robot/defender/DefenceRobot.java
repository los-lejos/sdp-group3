package robot.defender;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import robot.Robot;

/*
 * @author Owen Gillespie
 * @author Pete Stefanov
 */

public class DefenceRobot extends Robot {
	
	private static final int BALL_SENSOR_NEARBY_CUTOFF = 9;
	private static final int BALL_SENSOR_IN_KICKER_CUTOFF = 24;

	private static final LightSensor leftLightSensor = new LightSensor(SensorPort.S3);
	private static final LightSensor rightLightSensor = new LightSensor(SensorPort.S4);
	private static final UltrasonicSensor ballSensor = new UltrasonicSensor(SensorPort.S2);

	public DefenceRobot() {
    	super(leftLightSensor, rightLightSensor,
    			new DefenceKickerController(),
    			new DefenderMovementController());
    }
	
	@Override
	public boolean isBallNearby() {
    	return ballSensor.getDistance() <= BALL_SENSOR_NEARBY_CUTOFF;
	}
	
	@Override
	public boolean isDetectingBallInKicker() {
		return ballSensor.getDistance() > BALL_SENSOR_IN_KICKER_CUTOFF;
	}
	
}