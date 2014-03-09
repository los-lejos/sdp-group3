package robot.attacker;

import robot.BallSensorController;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

public class AttackerBallSensorController extends BallSensorController {
	private static final int BALL_SENSOR_NEARBY_CUTOFF = 14;
	private static final int BALL_SENSOR_IN_KICKER_CUTOFF = 10;
	
	private static final UltrasonicSensor ballSensor = new UltrasonicSensor(SensorPort.S2);
	
	public AttackerBallSensorController() {
		super(ballSensor);
	}

	@Override
	public boolean isDetectingBallInKicker() {
		// If we don't have enough readings yet, assume it's there
		if(!this.hasEnoughReadings()) {
			return true;
		}
		
		// Use the min value so that we don't assume the ball is not there just because of
		// a one-off misread
		return this.getRecentReadingMin() <= BALL_SENSOR_IN_KICKER_CUTOFF;
	}
	
	@Override
	public boolean isBallNearby() {
		// Use immediate value of the sensor, since we don't want to miss the ball by accident
		return ballSensor.getDistance() <= BALL_SENSOR_NEARBY_CUTOFF;
	}
}
