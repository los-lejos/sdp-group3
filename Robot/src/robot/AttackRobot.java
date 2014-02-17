package robot;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
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
	private static final NXTMotor kickMotor = new NXTMotor(MotorPort.B);
	private static final LightSensor leftLightSensor = new LightSensor(SensorPort.S4);
	private static final LightSensor rightLightSensor = new LightSensor(SensorPort.S1);
	private static final UltrasonicSensor ballSensor = new UltrasonicSensor(SensorPort.S2);
	private static final NXTRegulatedMotor leftMotor = Motor.C;
	private static final NXTRegulatedMotor rightMotor = Motor.A;
	private final DifferentialPilot pilot;
	
	private double travelSpeed;
	private double rotateSpeed;
	
	public AttackRobot() {
		super(leftLightSensor, rightLightSensor, ballSensor);
		pilot = new DifferentialPilot(tireDiameterMm, trackWidthMm, leftMotor, rightMotor, false);
		travelSpeed = pilot.getMaxTravelSpeed() * 0.5;
		rotateSpeed = pilot.getMaxRotateSpeed() * 0.3;
		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);
		kickMotor.setPower(50);
		this.openKicker();
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
	void moveLat(int power) {
		System.out.println("MOVE_LAT not implemented for attacker.");
	}
	
	// Robin: try and implement these. Good luck :)
	
	@Override
	void kick() {
		// TODO kicking
		this.hasBall = false;
	}

	public void openKicker() {
		// TODO open kicker at start
	}
	
	@Override
	protected void grab() {
		if (!this.hasBall) {
			// TODO close around ball.
			this.hasBall = true;
		} else {
			System.out.println("Bad GRAB attempt.");
		}
		
	}
	
	@Override
	public void cleanup() {
		if (this.hasBall) {
			// TODO release ball (kick it away)
		}
		// TODO close kicker (back to starting position)
	}

}
