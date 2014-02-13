package robot;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;

/*
 * @author Owen Gillespie
 */

public class AttackRobot extends Robot {
	
	private static final int tireDiameterMm = 62;
	private static final int trackWidthMm = 142;
	private static final NXTRegulatedMotor kickMotor = Motor.B;
	private static final LightSensor leftLightSensor = new LightSensor(SensorPort.S4);
	private static final LightSensor rightLightSensor = new LightSensor(SensorPort.S1);
	private static final UltrasonicSensor ballSensor = new UltrasonicSensor(SensorPort.S2);
	private static final NXTRegulatedMotor leftMotor = Motor.C;
	private static final NXTRegulatedMotor rightMotor = Motor.A;
	private final DifferentialPilot pilot;
	
	private float kickSpeed;
	private float catchSpeed;
	private double travelSpeed;
	private double rotateSpeed;
	
	public AttackRobot() {
		super(leftLightSensor, rightLightSensor, ballSensor);
		pilot = new DifferentialPilot(tireDiameterMm, trackWidthMm, leftMotor, rightMotor, false);
		kickSpeed = kickMotor.getMaxSpeed();
		catchSpeed = kickMotor.getMaxSpeed() * 0.3f;
		travelSpeed = pilot.getMaxTravelSpeed() * 0.5;
		rotateSpeed = pilot.getMaxRotateSpeed() * 0.3;
		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);
	}
	
	@Override
	protected void grab() {
		if (!this.hasBall()) {
			kickMotor.setSpeed(catchSpeed);
			kickMotor.rotate(-40, true);
			this.hasBall = true;
		} else {
			System.out.println("Bad GRAB attempt.");
		}
	}

	@Override
	void stop() {
		pilot.stop();
	}

	@Override
	boolean isMoving() {
		return pilot.isMoving();
	}

	@Override
	void rotate(int heading) {
		pilot.rotate(heading, true);
	}

	@Override
	void move(int distance) {
		pilot.travel(distance, true);
	}

	@Override
	void kick() {
		kickMotor.setSpeed(kickSpeed);
		kickMotor.rotate(50, true);
		this.hasBall = false;
	}

	@Override
	void moveLat(int power) {
		System.out.println("MOVE_LAT not implemented for attacker.");
	}

}
