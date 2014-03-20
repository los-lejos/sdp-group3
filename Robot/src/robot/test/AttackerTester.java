package robot.test;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;

public class AttackerTester {
	
	private static final NXTRegulatedMotor leftMotor = Motor.C;
	private static final NXTRegulatedMotor rightMotor = Motor.A;
	
	private static final int tireDiameterMm = 62;
	private static int trackWidthMm = 136; // Actual measured - 119, this works better

	public static void main(String[] args) throws InterruptedException {
		DifferentialPilot pilot = new DifferentialPilot(tireDiameterMm, trackWidthMm, leftMotor, rightMotor, false);
		
		pilot.setTravelSpeed(pilot.getMaxTravelSpeed());
		pilot.forward();
		
		Thread.sleep(1000);
		
		pilot.setTravelSpeed(pilot.getMaxTravelSpeed() * 0.5);
		Thread.sleep(1000);
		
		pilot.stop();
	}
}
