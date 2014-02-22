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
 * @author Pete Stefanov
 */

public class DefenceRobot extends Robot {
	
	private static final int tireDiameterMm = 48;
	private static final int trackWidthMm = 127;
	
	private static final LightSensor leftLightSensor = new LightSensor(SensorPort.S4);
	private static final LightSensor rightLightSensor = new LightSensor(SensorPort.S1);
	private static final UltrasonicSensor ballSensor = new UltrasonicSensor(SensorPort.S2);
	
	private static final NXTRegulatedMotor leftMotor = Motor.B;
	private static final NXTRegulatedMotor rightMotor = Motor.A;
	private static final NXTMotor lateralMotor = new NXTMotor(MotorPort.C);
	
	private final DifferentialPilot pilot;
	
	private final double lateralPowerMultiplier;
	private final int lateralMinPower;
	private double travelSpeed;
	private double rotateSpeed;
	private boolean movingLat;
	private int prevPower;
    
    public DefenceRobot() {
    	super(leftLightSensor, rightLightSensor, ballSensor);
    	
    	// Set up differential pilot.
    	pilot = new DifferentialPilot(tireDiameterMm, trackWidthMm, leftMotor, rightMotor, false);
		travelSpeed = pilot.getMaxTravelSpeed() * 0.5;
		rotateSpeed = pilot.getMaxRotateSpeed() * 0.2;
		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);
		
		// Set up strafing motor.
		this.lateralPowerMultiplier = 7;
		this.lateralMinPower = 20;
		lateralMotor.setPower(0);
		lateralMotor.forward();
    }

	@Override
	public void stop() {
		this.stopLat();
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
		int absPower = Math.abs(power);
		int motorPower = (int) (lateralPowerMultiplier * absPower + lateralMinPower);
		
		this.movingLat = true;
		lateralMotor.setPower(motorPower);
		this.flipLat(power);
	}
	
	/*
	 * Updates strafing direction.
	 */
	private void flipLat(int power) {
		if (this.prevPower < 0 && power >= 0) {
			
			// Set power to 0, stop smoothly.
			lateralMotor.flt();
			
			// Allow wheel to spin out.
			try {
				lateralMotor.wait(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// Turn the other way
			lateralMotor.backward();
		} else if (this.prevPower >= 0 && power < 0) {
			lateralMotor.flt();
			
			try {
				lateralMotor.wait(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			lateralMotor.forward();
		}
	}
	
	public void stopLat() {
		this.movingLat = false;
		lateralMotor.flt();
	}
	
	// Robin: try and implement these. Good luck :)
	
	@Override
	public void kick() {
		// TODO kicking
		this.hasBall = false;
	}
	
	@Override
	public void openKicker() {
		// TODO open kicker at start
	}
	
	@Override
	public void grab() {
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
			
			this.hasBall = false;
		}
		
		// TODO close kicker (back to starting position)
	}
	
}
