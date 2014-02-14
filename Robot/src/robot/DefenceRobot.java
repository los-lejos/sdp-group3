package robot;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
//import robot.navigation.HolonomicPilot;

/*
 * @author Owen Gillespie
 * @author Pete Stefanov
 */

public class DefenceRobot extends Robot {
	
	private static final int tireDiameterMm = 48; // TODO placeholder value
	private static final int trackWidthMm = 127;
//	private static final NXTRegulatedMotor kickMotor = Motor.B;
	private static final LightSensor leftLightSensor = new LightSensor(SensorPort.S4);
	private static final LightSensor rightLightSensor = new LightSensor(SensorPort.S1);
	private static final UltrasonicSensor ballSensor = new UltrasonicSensor(SensorPort.S2);
	private static final NXTRegulatedMotor leftMotor = Motor.B;
	private static final NXTRegulatedMotor rightMotor = Motor.A;
	NXTMotor lateralMotor = new NXTMotor(MotorPort.C);
	private final DifferentialPilot pilot;
	
//	private float kickSpeed;
//	private float catchSpeed;
	private double travelSpeed;
	private double rotateSpeed;
	private boolean movingLat;
	private double lastPower;
    
    public DefenceRobot() {
    	super(leftLightSensor, rightLightSensor, ballSensor);
    	pilot = new DifferentialPilot(tireDiameterMm, trackWidthMm, leftMotor, rightMotor, false);
//    	kickSpeed = kickMotor.getMaxSpeed();
//    	catchSpeed = kickMotor.getMaxSpeed() * 0.3f;
//    	kickMotor.setSpeed(kickSpeed);
		travelSpeed = pilot.getMaxTravelSpeed();
		rotateSpeed = pilot.getMaxRotateSpeed() * 0.2;
		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);
		lateralMotor.setPower(0);
		lateralMotor.forward();
    }

	@Override
	protected void grab() {
//		if (!this.hasBall()) {
//			kickMotor.setSpeed(catchSpeed);
//			kickMotor.rotate(-40, true);
//			this.hasBall = true;
//		} else {
//			System.out.println("Bad GRAB attempt.");
//		}
	}

	@Override
	void stop() {
		this.stopLat();
		pilot.stop();
	}

	@Override
	boolean isMoving() {
		return pilot.isMoving() || this.movingLat;
	}

	@Override
	void rotate(int heading) {
		this.stopLat();
		if (heading >= 0 && heading <= 180) {
			pilot.rotate(heading, true);
		} else if (heading > 180 && heading < 360) {
			pilot.rotate(- (heading - 180), true);
		}
	}

	@Override
	void move(int distance) {
		this.stopLat();
		pilot.travel(distance * 30, true);  // empirical (?!)
	}

	@Override
	void kick() {
		System.out.println("Kicking (not really)");
		// TODO kicking
	}
	
	public void moveLat(int power) {
		this.movingLat = true;
		this.lateralMotor.setPower((int) Math.round(7 * Math.abs(power)) + 20);
	
		if (power < 0) {
			lateralMotor.forward();
		} else {
			lateralMotor.backward();
		}
	}
	
	public void stopLat() {
		this.movingLat = false;
		this.lateralMotor.setPower(0);
	}
	
}
