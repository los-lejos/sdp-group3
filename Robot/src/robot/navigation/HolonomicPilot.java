package robot.navigation;

import java.util.ArrayList;
import java.lang.Math;

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
	
	private int wheelToCenterDist = 145; // Distance between point of rotation and front (left-right) wheel. Necessary for design 1
	private int wheelDiameter; // Needed for odometry.
	private int forwardSpeed; // Speed for going forwards/backwards - assigned to lateralMotor
	private int lateralSpeed; // Speed for going left/right - assigned to forwardMotor
	private int acceleration; // there is only one variable because it is only sensible to add acceleration to the back/forward movement
	private RegulatedMotor forwardMotor;
	private RegulatedMotor lateralMotor;
	private ArrayList<MoveListener> listeners = new ArrayList<MoveListener>();
	
	public HolonomicPilot(final int wheelDiameter, final RegulatedMotor forwardMotor, final RegulatedMotor lateralMotor) {
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
		lateralMotor.forward();		
	}

	
	public void backward() {
		lateralMotor.backward();
		
	}

	public void left() {
		forwardMotor.forward();
	}
	
	public void right() {
		forwardMotor.backward();
	}

	
	public void stop() {
		forwardMotor.stop(true);
		lateralMotor.stop(true);
		waitComplete();
	}

	public boolean isMoving() {
		return forwardMotor.isMoving() || lateralMotor.isMoving();
	}
	
	public void travel(double distance, int heading, boolean immediateReturn) {
		// All the formulae depend on the direction of the motors (forward rotation side).
		
		// Design 1 (No straight line?) (Check sketch on GitHub)
		// The use of the motors is a bit confusing so here is how they are used:
		// *forwardMotor*, the one at the front, moves the robot parallelly to the goal line
		// *lateralMotor*, the one on the side of the former, moves the robot perpendicularly to the goal line
		
		double arcLength = heading*Math.PI*wheelToCenterDist/180; // Basically the length of a arch of a circle = turning towards the point we have to go to
		double wheelsCircumference = wheelDiameter*Math.PI; 
		double numOfMotorDiskRevolutionsF = arcLength*360/wheelsCircumference; // Calculates the degrees through which the forwardMotor rotates to make the robot turn
				
		double numOfMotorDiskRevolutionsL = distance*360/wheelsCircumference; // Calculates the degrees through which the lateralMotor rotates to make the robot reach the aiming point
		
		forwardMotor.rotate((int) numOfMotorDiskRevolutionsF); 
		lateralMotor.rotate((int) numOfMotorDiskRevolutionsL); 		
		
		//forwardMotor.rotate((int) (2*heading*wheelToCenterDist/wheelDiameter)); // The formula looks ridiculous but that is the final look after some simplifications 
		//lateralMotor.rotate((int) (distance*360/(wheelDiameter*Math.PI))); // Almost the same thing applies here, too
		
		// Design 2 (Same turning policy but different location of the wheels) (Check sketch on GitHub)
		// The use of the motors is self-explanatory
		// lateralMotor.rotate(heading*Math.pow(distance_Wheel_To_RotationPoint, 2)/wheelDiameter);
		// forwardMotor.rotate(distance*360/wheelDiameter*Math.PI);
		
		
		// Design 3 (No turning :( - both wheels centralised) (Check sketch on GitHub)
		// In case the previous designs don't work
		// The use of the motors is self-explanatory
		// lateralMotor.rotate(distance*Math.sin(heading)*360/wheelDiameter*Math.PI);
		// forwardMotor.rotate(distance*Math.cos(heading)*360/wheelDiameter*Math.PI);
		
		
		
		if (!immediateReturn) waitComplete();
	}
	
	public void setTravelSpeed(int speedF, int speedL) {
		lateralMotor.setSpeed(speedF);
		forwardMotor.setSpeed(speedL);
		this.forwardSpeed = speedF;
		this.lateralSpeed = speedL;
	}
	
	public void setAcceleration(int acceleration) {
		forwardMotor.setAcceleration(acceleration);
		this.acceleration = acceleration;
	}
	
	public int getAcceleration() {
		return acceleration;
	}

	
	public int getForwardSpeed() {
		return forwardSpeed;
	}
	
	public int getLateralSpeed() {
		return lateralSpeed;
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