	package robot;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;

/*
 * @author Owen Gillespie
 * @author Pete Stefanov
 */

public class DefenceRobot extends Robot {
	
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
	private final DefenceKickerThread kickerThread;
	private final StrafeThread strafeThread;

	private double travelSpeed;
	private double rotateSpeed;
	private boolean movingLat = false;
    
	public DefenceRobot() {
    	super(leftLightSensor, rightLightSensor, ballSensor);
    	
    	// Set up differential pilot.
    	pilot = new DifferentialPilot(tireDiameterMm, trackWidthMm, leftMotor, rightMotor, false);
    	maxTravelSpeed = pilot.getMaxTravelSpeed();
		maxRotateSpeed = pilot.getMaxRotateSpeed();
		travelSpeed = maxTravelSpeed * 0.5;
		rotateSpeed = maxRotateSpeed * 0.4;
		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);

		kickerThread = new DefenceKickerThread(conn);
		kickerThread.start();
		
		strafeThread = new StrafeThread();
		strafeThread.start();
    }
	
	@Override
	public void stop() {
		strafeThread.updateLat(StrafeState.STOP);
		this.movingLat = false;
		pilot.stop();
	}

	@Override
	public boolean isMoving() {
		return pilot.isMoving() || this.movingLat;
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
	
	public void moveLat(int power) {
		strafeThread.updateLat(StrafeState.STRAFE, power);
		this.movingLat = true;
	}
	
	public void stopLat() {
		strafeThread.updateLat(StrafeState.STOP);
		this.movingLat = false;
	}

	@Override
	public void kick() {
		kickerThread.setKickerState(KickerState.KICK);
		this.hasBall = false;
	}

	@Override
	public void grab() {
		kickerThread.setKickerState(KickerState.GRAB);
	}

	@Override
	public void cleanup() {
		strafeThread.updateLat(StrafeState.EXIT);
		this.movingLat = false;
		kickerThread.setKickerState(KickerState.EXIT);
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