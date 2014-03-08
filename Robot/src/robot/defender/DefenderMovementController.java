package robot.defender;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;
import robot.MovementController;

public class DefenderMovementController extends MovementController {
	
	private static final NXTRegulatedMotor leftMotor = Motor.B;
	private static final NXTRegulatedMotor rightMotor = Motor.A;
	
	private static final int tireDiameterMm = 48;
	private static final int trackWidthMm = 127;

	private final DifferentialPilot pilot;
	private final StrafeThread strafeThread;
	
	private double maxTravelSpeed;
	private double maxRotateSpeed;

	private double travelSpeed;
	private double rotateSpeed;

	public DefenderMovementController() {
		pilot = new DifferentialPilot(tireDiameterMm, trackWidthMm, leftMotor, rightMotor, false);
    	maxTravelSpeed = pilot.getMaxTravelSpeed();
		maxRotateSpeed = pilot.getMaxRotateSpeed();
		travelSpeed = maxTravelSpeed * 0.4;
		rotateSpeed = maxRotateSpeed * 0.3;
		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);

		strafeThread = new StrafeThread();
		strafeThread.start();
	}

	@Override
	public void stop() {
		stopLat();
		pilot.stop();
	}

	@Override
	public boolean isMoving() {
		return pilot.isMoving() || this.strafeThread.isMoving();
	}

	@Override
	protected void performRotate(int heading) {
		this.stopLat();
		pilot.rotate(heading, true);
	}

	@Override
	protected void performMove(int distance) {
		this.stopLat();
		pilot.travel(distance, true);
	}
	
	@Override
	protected void performMoveLat(int distance) {
		strafeThread.move(distance);
	}
	
	private void stopLat() {
		strafeThread.stop();
	}

	@Override
	public void cleanup() {
		super.cleanup();
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
