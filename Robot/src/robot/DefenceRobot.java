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
	private static final double alpha = 0.47;
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
    
    public DefenceRobot() {
    	super(leftLightSensor, rightLightSensor, ballSensor);
    	pilot = new DifferentialPilot(tireDiameterMm, trackWidthMm, leftMotor, rightMotor, false);
//    	kickSpeed = kickMotor.getMaxSpeed();
//    	catchSpeed = kickMotor.getMaxSpeed() * 0.3f;
//    	kickMotor.setSpeed(kickSpeed);
		travelSpeed = pilot.getMaxTravelSpeed() * 0.5;
		rotateSpeed = pilot.getMaxRotateSpeed() * 0.3;
		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);
		lateralMotor.setPower(0);
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
		pilot.rotate(heading, true);
	}

	@Override
	void move(int distance) {
		this.stopLat();
		pilot.travel(distance * 10, true);
	}

	@Override
	void kick() {
		System.out.println("Kicking (not really)");
		// TODO kicking
	}
	
	public void moveLat(int power) {
		this.movingLat = true;
		
		if (Math.abs(power) <= 100) {
			lateralMotor.setPower(Math.abs(power));
		} else {
			System.out.println("Bad POWER value.");
			return;
		}
		
<<<<<<< .merge_file_9MvthU
		if (power < 0) {
			lateralMotor.forward();
		} else {
			lateralMotor.backward();
		}
<<<<<<< HEAD
=======
=======
		
>>>>>>> 393d923e43715c5d0af4ac03405670ec691f7596
		Thread.sleep((int) Math.round((1000 * distance) / (power * alpha)));
		// The following is an example of "time = distance/speed". It is multiplied by 1000 because "Thread.sleep" is in milliseconds.
		/*if (power == 100){
			Thread.sleep((int) (distance*1000/48)); // the speed of the robot (having considered its current weight) is 48cm/sec at 100% power
		} else if (power == 90){
			Thread.sleep((int) (distance*1000/40.4)); // the speed of the robot (having considered its current weight) is 40.4cm/sec at 90% power									
		} else if (power == 80){
			Thread.sleep((int) (distance*1000/33.1)); // the speed of the robot (having considered its current weight) is 33.1cm/sec at 80% power
		} else if (power == 70){
			Thread.sleep((int) (distance*1000/29.8)); // the speed of the robot (having considered its current weight) is 29.8cm/sec at 70% power
		} else {
			System.out.println("Better stick to values of 100%, 90%, 80%, 70%");
		}	*/		
<<<<<<< HEAD
>>>>>>> .merge_file_fIe0uP
=======
>>>>>>> 393d923e43715c5d0af4ac03405670ec691f7596
	}
	
	public void stopLat() {
		this.movingLat = false;
		moveLat(0);
	}
	
}
