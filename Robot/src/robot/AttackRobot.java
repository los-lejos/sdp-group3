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
	private static final int trackWidthMm = 144;
	private static final NXTRegulatedMotor kickMotor = Motor.B;
	private static final float kickSpeed = kickMotor.getMaxSpeed();
	private static final LightSensor leftLightSensor = new LightSensor(SensorPort.S4);
	private static final LightSensor rightLightSensor = new LightSensor(SensorPort.S1);
	private static final UltrasonicSensor ballSensor = new UltrasonicSensor(SensorPort.S2);
	private static final NXTRegulatedMotor leftMotor = Motor.C;
	private static final NXTRegulatedMotor rightMotor = Motor.A;
	private final DifferentialPilot pilot;
	
	public AttackRobot() {
		super(leftLightSensor, rightLightSensor, ballSensor);
		pilot = new DifferentialPilot(tireDiameterMm, trackWidthMm, leftMotor, rightMotor, true);
		kickMotor.setSpeed(kickSpeed);
	}

	@Override
	protected void moveTo(int heading, int distance) {
		if (heading >= 0 && heading < 180) {
			pilot.rotate(heading);
		} else if (heading >= 180 && heading < 360) {
			pilot.rotate(heading - 360);
		} else {
			System.out.println("Bad heading value.");
		}
		pilot.travel((float) distance / 10.0);
	}

	@Override
	protected void kickToward(int heading) {
		if (this.hasBall()) {
			pilot.rotate(heading);
			kickMotor.rotate(50);
			this.hasBall = false;
		} else {
			System.out.println("Bad KICK attempt.");
		}
	}
	
	@Override
	protected void grab() {
		if (!this.hasBall()) {
			kickMotor.rotate(-40);
			this.hasBall = true;
		} else {
			System.out.println("Bad GRAB attempt.");
		}
	}

}
