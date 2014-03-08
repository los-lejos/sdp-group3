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
	
	private static final int FRONT_SENSOR_CUTOFF = 10;

	private static final LightSensor leftLightSensor = new LightSensor(SensorPort.S3);
	private static final LightSensor rightLightSensor = new LightSensor(SensorPort.S4);
	private static final UltrasonicSensor ballSensor = new UltrasonicSensor(SensorPort.S2);
	
    
	public DefenceRobot() {
    	super(leftLightSensor, rightLightSensor,
    			ballSensor, FRONT_SENSOR_CUTOFF,
    			new DefenceKickerController(),
    			new DefenderMovementController());
    }
	
}