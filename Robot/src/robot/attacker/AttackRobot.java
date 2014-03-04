package robot.attacker;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import robot.Robot;

/*
 * @author Owen Gillespie
 */

public class AttackRobot extends Robot {
	
	private static final int tireDiameterMm = 62;
	private static int trackWidthMm = 136; // Actual measured - 119, this works better
	
	private static final NXTRegulatedMotor leftMotor = Motor.C;
	private static final NXTRegulatedMotor rightMotor = Motor.A;
	
	private static final LightSensor leftLightSensor = new LightSensor(SensorPort.S4);
	private static final LightSensor rightLightSensor = new LightSensor(SensorPort.S1);
	private static final UltrasonicSensor ballSensor = new UltrasonicSensor(SensorPort.S2);
	
	private double maxTravelSpeed;
	private double maxRotateSpeed;

	private DifferentialPilot pilot;
	
	private double travelSpeed;
	private double rotateSpeed;
	
	public AttackRobot() {
		super(leftLightSensor, rightLightSensor, ballSensor, new AttackKickerController());
		
		pilot = new DifferentialPilot(tireDiameterMm, trackWidthMm, leftMotor, rightMotor, false);
		maxTravelSpeed = pilot.getMaxTravelSpeed();
		maxRotateSpeed = pilot.getMaxRotateSpeed();
		travelSpeed = maxTravelSpeed * 0.5;
		rotateSpeed = maxRotateSpeed * 0.3;
		
		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);
	}

	@Override
	public void stop() {
		pilot.stop();
	}

	@Override
	public boolean isMoving() {
		return pilot.isMoving();
	}

	@Override
	public void rotate(int heading) {
		pilot.rotate(heading, true);
	}

	@Override
	public void move(int distance) {
		pilot.travel(distance, true);
	}
	
	@Override
	public void moveLat(int power) {
		throw new UnsupportedOperationException("Lateral movement is not possible for the attacker");
	}
	
	@Override
	public void setTrackWidth(int width) {
		// Speed stays default anyway???
		// Not to be used 
		pilot.stop();
		trackWidthMm = width;
		pilot = new DifferentialPilot(tireDiameterMm, trackWidthMm, leftMotor, rightMotor, false);
		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);
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

	@Override
	public void cleanup() {
	}
}