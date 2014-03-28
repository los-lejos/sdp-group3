package robot.test;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;


public class DefenceTester {
	
	private static final NXTRegulatedMotor leftMotor = Motor.B;
	private static final NXTRegulatedMotor rightMotor = Motor.A;
	
	private static final int tireDiameterMm = 48;
	private static final int trackWidthMm = 127;

	private static DifferentialPilot pilot;

	public static void main(String[] args) throws Exception {
		pilot = new DifferentialPilot(tireDiameterMm, trackWidthMm, leftMotor, rightMotor, false);
		
		pilot.setTravelSpeed(pilot.getMaxTravelSpeed());
		
		pilot.rotate(180, true);
		Thread.sleep(150);
		pilot.travel(50);
		
		while(pilot.isMoving()) {
			System.out.println("aa");
		}
	}


}
