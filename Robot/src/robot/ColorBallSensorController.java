package robot;

import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;

public class ColorBallSensorController extends BallSensorController {
	
	private static int NEARBY_CUTOFF = 6;
	private static int KICKER_CUTOFF = 6;

	private static final ColorSensor ballSensor = new ColorSensor(SensorPort.S2);
	
	public ColorBallSensorController(boolean isAttacker) {
		super(ballSensor);
		
		if(isAttacker) {
			NEARBY_CUTOFF = 7;
			KICKER_CUTOFF = 7;
		} else {
			NEARBY_CUTOFF = 8;
			KICKER_CUTOFF = 8;
		}
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
