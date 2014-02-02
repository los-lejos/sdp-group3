package robot.navigation;

import java.util.ArrayList;

import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveController;
import lejos.robotics.navigation.MoveListener;

/*
 * @author Owen Gillespie
 * 
 * This class gives us something similar to DifferentialPilot, but
 * for the holonomic design.
 * 
 * TODO Is trackWidth relevant? Need to figure out odometry stuff for this design.
 * 		Can't just steal this directly from DifferentialPilot since angle info
 * 		depends on differences in wheel turns.
 * TODO Implementing MoveController (needed if we want to use Navigator) forces
 * 		is into implementing some methods that IMO don't make much sense given
 * 		the robot design:
 * 			eg. travel(distance) (since we strafe, just travelling
 * 				forwards/backwards by a set amount is pretty unhelpful).
 * TODO Actually finish writing this class
 */

public class HolonomicPilot implements MoveController {
	
	private double wheelDiameter;
	private double trackWidth;
	private double travelSpeed;
	private RegulatedMotor forwardMotor;
	private RegulatedMotor lateralMotor;
	private Move currentMovement;
	private ArrayList<MoveListener> listeners = new ArrayList<MoveListener>();
	
	public HolonomicPilot(final double wheelDiameter, final double trackWidth, final RegulatedMotor forwardMotor, final RegulatedMotor lateralMotor) {
		this.wheelDiameter = wheelDiameter;
		this.trackWidth = trackWidth;
		this.forwardMotor = forwardMotor;
		this.lateralMotor = lateralMotor;
	}
	
	@Override
	public Move getMovement() {
		return currentMovement;
	}

	// TODO We will need to inform MoveListeners, for example, when a move is started/completed.
	@Override
	public void addMoveListener(MoveListener listener) {
		listeners.add(listener);
	}

	/*
	 * NOTE: forward(), backward(), left() and right() depend on motor orientation.
	 * Will need to look at the actual robot to figure out the correct configuration.
	 * 
	 */
	
	@Override
	public void forward() {
		forwardMotor.forward();		
	}

	@Override
	public void backward() {
		forwardMotor.backward();
		
	}

	public void left() {
		lateralMotor.forward();
	}
	
	public void right() {
		lateralMotor.backward();
	}

	@Override
	public void stop() {
		forwardMotor.stop(true);
		lateralMotor.stop(true);
		waitComplete();
	}

	@Override
	public boolean isMoving() {
		return forwardMotor.isMoving() || lateralMotor.isMoving();
	}

	@Override
	public void travel(double distance) {
		this.travel(distance, false);
	}

	@Override
	public void travel(double distance, boolean immediateReturn) {
		// TODO do some forward/backward travelling here
		if (!immediateReturn) waitComplete();		
	}
	
	// Travel a given amount in a given direction.
	// A navigator class could use this in an implementation of goTo(x, y, heading).
	// Or x, y, heading could come from PC, if we choose to do it that way.
	public void travel(double distance, double heading, boolean immediateReturn) {
		if (!immediateReturn) waitComplete();
	}

	// TODO This sucks since we would probably like to set lateral speed independently
	// from forward/backward speed. Included since (right now, anyway) we need this
	// if we want to implement MoveController. (Do we? We could write a custom navigator
	// that takes in a HolonomicPilot rather than a MoveController...)
	@Override
	public void setTravelSpeed(double speed) {
		forwardMotor.setSpeed((int) speed);
		lateralMotor.setSpeed((int) speed); 
		this.travelSpeed = speed;
	}
	
	// TODO setAccelerationSpeed might be a nice/useful method.
	//		(Seriously, we should do some acceleration. It will look so cool, guys.)

	@Override
	public double getTravelSpeed() {
		return travelSpeed;
	}

	@Override
	public double getMaxTravelSpeed() {
		return Math.min(forwardMotor.getMaxSpeed(), lateralMotor.getMaxSpeed());
	}
	
	public void waitComplete() {
		while (isMoving()) {
			forwardMotor.waitComplete();
			lateralMotor.waitComplete();
		}
	}
	
}