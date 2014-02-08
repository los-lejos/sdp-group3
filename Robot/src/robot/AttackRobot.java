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
		pilot.rotate(-angle, true);
		//while (pilot.isMoving() && !interrupted);
		pilot.travel(distance * 10, true);
		//if (interrupted) stop();
	}

	protected void kickToward(int heading) {
		if (this.hasBall()) {
			kickMotor.setSpeed(kickSpeed);
			pilot.rotate(heading, true);
			//while (pilot.isMoving() && !interrupted);
			//if (!interrupted) {
				kickMotor.rotate(50, true);
				this.hasBall = false;
			//} else {
				stop();
			//+}
		} else {
			System.out.println("Bad KICK attempt.");
		}
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
		int angle;
		
		if (heading >= 0 && heading < 180) {
			angle = heading;
		} else if (heading >= 180 && heading < 360) {
			angle = heading - 360;
		} else {
			angle = 0;
			System.out.println("Bad heading value.");
		}
		
		pilot.rotate(angle, true);
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

}
