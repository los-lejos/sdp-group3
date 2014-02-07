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
	private double travelSpeed;
	private double rotateSpeed;
	
	public AttackRobot() {
		super(leftLightSensor, rightLightSensor, ballSensor);
		pilot = new DifferentialPilot(tireDiameterMm, trackWidthMm, leftMotor, rightMotor, false);
		kickSpeed = kickMotor.getMaxSpeed();
		travelSpeed = pilot.getMaxTravelSpeed() * 0.5;
		rotateSpeed = pilot.getMaxRotateSpeed() * 0.3;
		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);
		kickMotor.setSpeed(kickSpeed);
	}

	@Override
	protected void moveTo(int heading, int distance) {
		int angle;
		
		if (heading >= 0 && heading < 180) {
			angle = heading;
		} else if (heading >= 180 && heading < 360) {
			angle = heading - 360;
		} else {
			angle = 0;
			System.out.println("Bad heading value.");
		}
		
		System.out.println("Rotating " + angle + " degrees.");
		pilot.rotate(-angle);
		pilot.travel(distance * 10, true);
	}

	@Override
	protected void kickToward(int heading) {
		if (this.hasBall()) {
			pilot.rotate(heading);
			kickMotor.rotate(50, true);
			this.hasBall = false;
		} else {
			System.out.println("Bad KICK attempt.");
		}
	}
	
	@Override
	protected void grab() {
		if (!this.hasBall()) {
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

}
