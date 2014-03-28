package robot.attacker;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;
import robot.MovementController;

public class AttackerMovementController extends MovementController {

	private static final NXTRegulatedMotor leftMotor = Motor.C;
	private static final NXTRegulatedMotor rightMotor = Motor.A;
	
	private static final int tireDiameterMm = 62;
	private static int trackWidthMm = 136; // Actual measured - 119, this works better
	
	private DifferentialPilot pilot;
	
	private double maxTravelSpeed;
	private double maxRotateSpeed;

	private double travelSpeed;
	private double rotateSpeed;
	
	public AttackerMovementController() {
		pilot = new DifferentialPilot(tireDiameterMm, trackWidthMm, leftMotor, rightMotor, false);
		
		maxTravelSpeed = pilot.getMaxTravelSpeed();
		maxRotateSpeed = pilot.getMaxRotateSpeed();
		travelSpeed = maxTravelSpeed * 0.7;
		rotateSpeed = maxRotateSpeed * 0.2;

		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);
	}
	
	@Override
	public void stop() {
		pilot.stop();
	}
	
	@Override
	public void stopLateral() {
	}

	@Override
	public boolean isMoving() {
		return pilot.isMoving();
	}

	@Override
	public void performRotate(int heading) {
		pilot.rotate(heading, true);
	}

	@Override
	public void performMove(int distance) {
		pilot.travel(distance, true);
	}
	
	@Override
	public void performMoveLat(int distance) {
		// Not throwing an exception since we want robustness, not correctness
		System.out.println("Lateral movement is not possible for the attacker");
	}
	
	@Override
	public void setTrackWidth(int width) {
		// Speed stays default anyway???
		// Not to be used 
		pilot.stop();
		trackWidthMm = width;
		pilot = new DifferentialPilot(tireDiameterMm, trackWidthMm, leftMotor, rightMotor, false);
		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);
	}

	@Override
	public void setTravelSpeed(int speedPercentage) {
		travelSpeed = speedPercentage * 0.01 * maxTravelSpeed;
		pilot.setTravelSpeed(travelSpeed);
	}

	@Override
	public void setRotateSpeed(int speedPercentage) {
		rotateSpeed = speedPercentage * 0.01 * maxRotateSpeed;
		pilot.setRotateSpeed(rotateSpeed);
	}

	@Override
	public void cleanup() {
		
	}
}
