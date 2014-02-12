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
    
    public DefenceRobot() {
    	super(leftLightSensor, rightLightSensor, ballSensor);
//    	pilot = new HolonomicPilot(tireDiameterMm, forwardMotor, lateralMotor);
    	pilot = new DifferentialPilot(tireDiameterMm, trackWidthMm, leftMotor, rightMotor, false);
//    	kickSpeed = kickMotor.getMaxSpeed();
//    	catchSpeed = kickMotor.getMaxSpeed() * 0.3f;
//    	kickMotor.setSpeed(kickSpeed);
//    	pilot.setTravelSpeed(travelSpeed, travelSpeed);
		travelSpeed = pilot.getMaxTravelSpeed() * 0.5;
		rotateSpeed = pilot.getMaxRotateSpeed() * 0.3;
		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);
    }

	public void moveTo(int heading, int distance) {
		// Probably going to be...
//		pilot.travel(distance, heading, true);
/*		int angle;
		
		if (heading >= 0 && heading < 180) {
			angle = heading;
		} else if (heading >= 180 && heading < 360) {
			angle = heading - 360;
		} else {
			angle = 0;
			System.out.println("Bad heading value.");
		}
*/		
//		System.out.println("Rotating " + angle + " degrees.");
		System.out.println("Rotating " + heading + " degrees.");
//		pilot.rotate(-angle, true);
		pilot.rotate(heading);
		//while (pilot.isMoving() && !interrupted);
//		pilot.travel(distance * 10, true);
		pilot.travel(distance*10);
		//if (interrupted) stop();
		pilot.stop();
	}
/*
	protected void kickToward(int heading) {
		// TODO how is this going to work? Look at this
		// after robot design complete.
		if (this.hasBall()) {
			kickMotor.setSpeed(kickSpeed);
			kickMotor.rotate(50);
			this.hasBall = false;
		} else {
			System.out.println("Bad KICK attempt.");
		}
	}
*/	
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
		pilot.stop();
	}

	@Override
	boolean isMoving() {
		return pilot.isMoving();
	}

	@Override
	void rotate(int heading) {
/*		int angle;
		
		if (heading >= 0 && heading < 180) {
			angle = heading;
		} else if (heading >= 180 && heading < 360) {
			angle = heading - 360;
		} else {
			angle = 0;
			System.out.println("Bad heading value.");
		}
		
		pilot.rotate(angle, true);
*/
		pilot.rotate(heading);
	}

	@Override
	void move(int distance) {
		pilot.travel(distance*10, true);
	}

	@Override
	void kick() {
		
	}
	
	public void travelSidewise(int power, double distance, String direction) throws Exception{
		System.out.println("Moving: " + distance + " to the " + direction);
		lateralMotor.setPower(power); // between 0% and 100%
		if (direction.equals("left")){
			lateralMotor.forward();
		} else if (direction.equals("right")){
			lateralMotor.backward();
		} else {
			System.out.println("Unknown direction");
		}
		// The following is an example of "time = distance/speed". It is multiplied by 1000 because "Thread.sleep" is in milliseconds.
		if (power == 100){
			Thread.sleep((int) (distance*1000/48)); // the speed of the robot (having considered its current weight) is 48cm/sec at 100% power
		} else if (power == 90){
			Thread.sleep((int) (distance*1000/40.4)); // the speed of the robot (having considered its current weight) is 40.4cm/sec at 90% power									
		} else if (power == 80){
			Thread.sleep((int) (distance*1000/33.1)); // the speed of the robot (having considered its current weight) is 33.1cm/sec at 80% power
		} else if (power == 70){
			Thread.sleep((int) (distance*1000/29.8)); // the speed of the robot (having considered its current weight) is 29.8cm/sec at 70% power
		} else {
			System.out.println("Better stick to values of 100%, 90%, 80%, 70%");
		}			
		lateralMotor.stop();
	}
}
