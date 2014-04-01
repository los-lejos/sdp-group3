package robot.test;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;

public class AttackerTester {
	
	private static final NXTRegulatedMotor leftMotor = Motor.B;
	private static final NXTRegulatedMotor rightMotor = Motor.A;
	
	private static final int tireDiameterMm = 62;
	private static int trackWidthMm = 136; // Actual measured - 119, this works better

	public static void main(String[] args) throws InterruptedException {
		DifferentialPilot pilot = new DifferentialPilot(tireDiameterMm, trackWidthMm, leftMotor, rightMotor, false);
		
		pilot.setTravelSpeed(pilot.getMaxTravelSpeed());
		pilot.travel(200, true);
		pilot.rotate(90, true);
		pilot.travel(200, true);
		
		while(pilot.isMoving());
		
		pilot.stop();
	}
}
