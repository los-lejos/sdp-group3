package robot;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;

public class HolonomicMovementController extends MovementController {
	
	private static final NXTRegulatedMotor leftMotor = Motor.B;
	private static final NXTRegulatedMotor rightMotor = Motor.A;
	
	private static final int tireDiameterMm = 48;
	private static final int trackWidthMm = 117;

	private final DifferentialPilot pilot;
	private final StrafeThread strafeThread;
	
	private double maxTravelSpeed;
	private double maxRotateSpeed;

	private double travelSpeed;
	private double rotateSpeed;

	public HolonomicMovementController() {
		pilot = new DifferentialPilot(tireDiameterMm, trackWidthMm, leftMotor, rightMotor, false);
    	maxTravelSpeed = pilot.getMaxTravelSpeed();
		maxRotateSpeed = pilot.getMaxRotateSpeed();
		travelSpeed = maxTravelSpeed * 0.7;
		rotateSpeed = maxRotateSpeed * 0.3;
		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);

		strafeThread = new StrafeThread();
		strafeThread.start();
	}

	@Override
	public void stop() {
		pilot.stop();
	}
	
	@Override
	public void stopLateral() {
		strafeThread.stop();
	}

	@Override
	public boolean isMoving() {
		return pilot.isMoving() || this.strafeThread.isMoving();
	}
	
	@Override
	public boolean isStrafing() {
		return this.strafeThread.isMoving();
	}
	
	@Override
	public boolean isDriving() {
		return pilot.isMoving();
	}

	@Override
	protected void performRotate(int heading) {
		pilot.rotate(heading, true);
	}

	@Override
	protected void performMove(int distance) {
		pilot.travel(distance, true);
	}
	
	@Override
	protected void performMoveLat(int distance) {
		strafeThread.move(distance);
	}

	@Override
	public void cleanup() {
		strafeThread.cleanup();
	}

	@Override
	public void setTrackWidth(int width) {
		throw new UnsupportedOperationException("setTrackWidth");
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
}
