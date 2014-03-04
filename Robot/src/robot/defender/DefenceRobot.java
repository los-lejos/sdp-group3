package robot.defender;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import robot.Robot;
import robot.StrafeThread;

/*
 * @author Owen Gillespie
 * @author Pete Stefanov
 */

public class DefenceRobot extends Robot {
	
	private static final int FRONT_SENSOR_CUTOFF = 12;
	
	private static final int tireDiameterMm = 48;
	private static final int trackWidthMm = 127;
	private static final LightSensor leftLightSensor = new LightSensor(SensorPort.S3);
	private static final LightSensor rightLightSensor = new LightSensor(SensorPort.S4);
	private static final UltrasonicSensor ballSensor = new UltrasonicSensor(SensorPort.S2);
	private static final NXTRegulatedMotor leftMotor = Motor.B;
	private static final NXTRegulatedMotor rightMotor = Motor.A;

	private final DifferentialPilot pilot;
	
	private double maxTravelSpeed;
	private double maxRotateSpeed;
	private final StrafeThread strafeThread;

	private double travelSpeed;
	private double rotateSpeed;
    
	public DefenceRobot() {
    	super(leftLightSensor, rightLightSensor, ballSensor, FRONT_SENSOR_CUTOFF, new DefenceKickerController());
    	
    	// Set up differential pilot.
    	pilot = new DifferentialPilot(tireDiameterMm, trackWidthMm, leftMotor, rightMotor, false);
    	maxTravelSpeed = pilot.getMaxTravelSpeed();
		maxRotateSpeed = pilot.getMaxRotateSpeed();
		travelSpeed = maxTravelSpeed * 0.5;
		rotateSpeed = maxRotateSpeed * 0.4;
		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);

		strafeThread = new StrafeThread();
		strafeThread.start();
    }
	
	@Override
	public void stop() {
		stopLat();
		pilot.stop();
	}

	@Override
	public boolean isMoving() {
		return pilot.isMoving() || this.strafeThread.isMoving();
	}

	@Override
	public void rotate(int heading) {
		this.stopLat();
		pilot.rotate(heading, true);
	}

	@Override
	public void move(int distance) {
		this.stopLat();
		pilot.travel(distance, true);
	}
	
	@Override
	public void moveLat(int distance) {
		strafeThread.move(distance);
	}
	
	public void stopLat() {
		strafeThread.stop();
	}

	@Override
	public void cleanup() {
		strafeThread.cleanup();
	}

	@Override
	public void setTrackWidth(int width) {
		throw new UnsupportedOperationException("setTrackWidth");
	}

	@Override
	public void setTravelSpeed(int speedPercentage) {
		travelSpeed = speedPercentage * 0.01 * maxTravelSpeed;
		pilot.setTravelSpeed(travelSpeed);
	}

	@Override
	public void setRotateSpeed(int speedPercentage) {
		rotateSpeed = speedPercentage * 0.01 * maxRotateSpeed;
		pilot.setRotateSpeed(rotateSpeed);
	}
}