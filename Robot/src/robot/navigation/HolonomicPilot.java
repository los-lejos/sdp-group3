package robot.navigation;

import java.util.ArrayList;

import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.MoveListener;

/*
 * @author Owen Gillespie
 * 
 * This class gives us something similar to DifferentialPilot, but
 * for the holonomic design.
 * 
 * TODO Odometry: how do we figure out our new location given wheel diameter?
 *  
 * TODO Implementing MoveController (needed if we want to use Navigator) forces
 * 		is into implementing some methods that IMO don't make much sense given
 * 		the robot design:
 * 			eg. travel(distance) (since we strafe, just travelling
 * 				forwards/backwards by a set amount is pretty unhelpful).
 * 		- What if we calculate the distance from a = current location, to b = goal/aiming location,
 * 		this way we can travel the distance = |b - a|... maybe it will be helpful in this scenario
 * 
 *  Actually finish writing this class
 *  - TODO Done?
 */

public class HolonomicPilot {
	
	private double wheelDiameter; // Needed for odometry.
	private int[] travelSpeed; // we can store the speed of the forwardMotor at position 0 and lateralMotor at position 1
	private int acceleration; // there is only one variable because it is only sensible to add acceleration to the back/forward movement
	private RegulatedMotor forwardMotor;
	private RegulatedMotor lateralMotor;
	private ArrayList<MoveListener> listeners = new ArrayList<MoveListener>();
	
	public HolonomicPilot(final double wheelDiameter, final RegulatedMotor forwardMotor, final RegulatedMotor lateralMotor) {
		this.wheelDiameter = wheelDiameter;
		this.forwardMotor = forwardMotor;
		this.lateralMotor = lateralMotor;
	}
	
	// TODO We will need to inform MoveListeners, for example, when a move is started/completed.
	
	public void addMoveListener(MoveListener listener) {
		listeners.add(listener);
	}

	/*
	 * NOTE: forward(), backward(), left() and right() depend on motor orientation.
	 * Will need to look at the actual robot to figure out the correct configuration.
	 */

	public void forward() {
		forwardMotor.forward();
		
	}

	
	public void backward() {
		forwardMotor.backward();
		
	}

	public void left() {
		lateralMotor.forward();
	}
	
	public void right() {
		lateralMotor.backward();
	}

	
	public void stop() {
		forwardMotor.stop(true);
		lateralMotor.stop(true);
		waitComplete();
	}

	public boolean isMoving() {
		return forwardMotor.isMoving() || lateralMotor.isMoving();
	}
	
	// Travel a given amount in a given direction.
	// A navigator class could use this in an implementation of goTo(x, y, heading).
	// Or x, y, heading could come from PC, if we choose to do it that way.
	public void travel(double distance, int heading, boolean immediateReturn) {
		// Do some travelling here.
		if (!immediateReturn) waitComplete();
	}
	
	public void setTravelSpeed(int speedF, int speedL) {
		forwardMotor.setSpeed(speedF);
		lateralMotor.setSpeed(speedL); 
		this.travelSpeed[0] = speedF;
		this.travelSpeed[1] = speedL;
	}
	
	public void setAcceleration(int acceleration) {
		forwardMotor.setAcceleration(acceleration);
		this.acceleration = acceleration;
	}
	
	public int getAcceleration() {
		return acceleration;
	}

	
	public int[] getTravelSpeed() {
		return travelSpeed;
	}

	
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