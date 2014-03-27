package robot.defender;

import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;
import robot.BallSensorController;

public class DefenderBallSensorController extends BallSensorController {
	
	private static final int NEARBY_CUTOFF = 6;
	private static final int KICKER_CUTOFF = 6;

	private static final ColorSensor ballSensor = new ColorSensor(SensorPort.S2);
	
	public DefenderBallSensorController() {
		super(ballSensor);
	}
	
	@Override
	public boolean isBallNearby() {
		return ballSensor.getColor().getRed() >= NEARBY_CUTOFF;
	}
	
	@Override
	public boolean isDetectingBallInKicker() {
		if(!this.hasEnoughReadings()) {
			return true;
		}
		
		return this.getRecentReadingMax() >= KICKER_CUTOFF;
	}

}
