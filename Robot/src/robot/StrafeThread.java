package robot;

import lejos.nxt.Motor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.NXTRegulatedMotor;

public class StrafeThread extends Thread {
	
	private enum StrafeState {
		READY, STRAFE, STOP, EXIT
	}
	
	private final int POWER = 100;
	private final int MAX_DELAY = 3000;
	private final double ROTATION_LEFT_MULTIPLIER = 0.1;
	private final double ROTATION_RIGHT_MULTIPLIER = 0.1;
	
	private final NXTMotor lateralMotor;
	private final NXTRegulatedMotor rightWheel = Motor.A;
	private final NXTRegulatedMotor leftWheel = Motor.B;
	
	private StrafeState state = StrafeState.READY;
	private StrafeState newState = StrafeState.READY;
	
	private boolean forwardDirection;
	private long movementDelay = 0;
	
	private boolean interrupted = false;
	private boolean isMoving = false;

	public StrafeThread() {
		this.setDaemon(true);
		lateralMotor = new NXTMotor(MotorPort.C);
		
		// Set speed of correction rotations
		float maxSpeed = this.leftWheel.getMaxSpeed();
		float leftRotateSpeed = (float) (maxSpeed * ROTATION_LEFT_MULTIPLIER);
		float rightRotateSpeed = (float) (maxSpeed * ROTATION_RIGHT_MULTIPLIER);
		this.leftWheel.setSpeed(leftRotateSpeed);
		this.rightWheel.setSpeed(rightRotateSpeed);
	}
	
	public void cleanup() {
		this.newState = StrafeState.EXIT;
	}
	
	public boolean isMoving() {
		return this.isMoving;
	}
	
	public void move(int distance) {
		this.interrupted = true;
		
		// Calculations based on power being 100,
		// can find a function that fits this, but this is simpler to adjust
		int absDist = Math.abs(distance);
		if(absDist <= 5) {
			this.movementDelay = (long) (absDist*28);
		} else if(absDist <= 15) {
			this.movementDelay = (long) (absDist*21);
		} else if(absDist <= 20) {
			this.movementDelay = (long) (absDist*18);
		} else {
			this.movementDelay = (long) (absDist*14);
		}

		if(this.movementDelay > MAX_DELAY) {
			this.movementDelay = MAX_DELAY;
		}
		
		this.forwardDirection = distance > 0;
		this.isMoving = true;

		this.newState = StrafeState.STRAFE;
	}
	
	public void stop() {
		this.isMoving = false;
		this.newState = StrafeState.STOP;
	}
	
	@Override
	public void run() {
		while (state != StrafeState.EXIT) {
			if (state == StrafeState.STRAFE) {
				moveLat();
				state = StrafeState.READY;
			} else if (state == StrafeState.STOP) {
				stopMotor();
				state = StrafeState.READY;
			}
			
			interrupted = false;
			
			if(newState != StrafeState.READY) {
				state = newState;
				newState = StrafeState.READY;
			} else if(isMoving) {
				this.isMoving = false;
				this.stopMotor();
			}
		}
		
		stopMotor();
	}
	
	private void moveLat() {
		this.lateralMotor.setPower(this.POWER);
		
		if(this.forwardDirection) {
			this.lateralMotor.forward();
			this.leftWheel.forward();
		} else {
			this.lateralMotor.backward();
			this.rightWheel.forward();
		}
		
		// Wait to move the required distance
		long startTime = System.currentTimeMillis();
		while(!interrupted && System.currentTimeMillis() - startTime < this.movementDelay);
		stopMotor();
	}
	
	private void stopMotor() {
		this.isMoving = false;
		this.lateralMotor.flt();
		this.leftWheel.flt();
		this.rightWheel.flt();
	}
}

