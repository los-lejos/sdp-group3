package robot;

import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;

public class ColorBallSensorController extends BallSensorController {
	
	private static final int NEARBY_CUTOFF = 8;
	private static final int KICKER_CUTOFF = 8;

	private static final ColorSensor ballSensor = new ColorSensor(SensorPort.S2);
	
	public ColorBallSensorController() {
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
