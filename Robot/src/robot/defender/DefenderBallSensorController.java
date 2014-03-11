package robot.defender;

import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import robot.BallSensorController;

public class DefenderBallSensorController extends BallSensorController {
	
	private static final int BALL_SENSOR_NEARBY_CUTOFF = 9;
	private static final int BALL_SENSOR_IN_KICKER_CUTOFF = 20;

	private static final UltrasonicSensor ballSensor = new UltrasonicSensor(SensorPort.S2);
	
	public DefenderBallSensorController() {
		super(ballSensor);
	}
	
	@Override
	public boolean isBallNearby() {
    	return ballSensor.getDistance() <= BALL_SENSOR_NEARBY_CUTOFF;
	}
	
	@Override
	public boolean isDetectingBallInKicker() {
		// If we don't have enough readings yet, assume it's there
		//if(!this.hasEnoughReadings()) {
			return true;
		//}

		//return this.getRecentReadingMin() >= BALL_SENSOR_IN_KICKER_CUTOFF;
	}

}
