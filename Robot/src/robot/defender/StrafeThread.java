package robot.defender;

import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;

public class StrafeThread extends Thread {
	
	private enum StrafeState {
		READY, STRAFE, STOP, EXIT
	}
	
	private final int POWER = 100;
	private final int MAX_DELAY = 3000;
	
	private final NXTMotor lateralMotor;
	private StrafeState state = StrafeState.READY;
	private StrafeState newState = StrafeState.READY;
	
	private boolean forwardDirection;
	private long movementDelay = 0;
	
	private boolean isMoving = false;

	public StrafeThread() {
		this.setDaemon(true);
		lateralMotor = new NXTMotor(MotorPort.C);
	}
	
	public void cleanup() {
		this.newState = StrafeState.EXIT;
	}
	
	public boolean isMoving() {
		return this.isMoving;
	}
	
	public void move(int distance) {
		// Calculations based on power being 100
		this.movementDelay = Math.abs(distance)*14; // the speed of the robot (having considered its current weight) is 48cm/sec at 100% power
		
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
				this.isMoving = false;
				state = StrafeState.READY;
			} else if (state == StrafeState.STOP) {
				stopMotor();
				state = StrafeState.READY;
			}
			
			if(newState != StrafeState.READY) {
				state = newState;
				newState = StrafeState.READY;
			}
		}
		
		stopMotor();
	}
	
	private void moveLat() {
		this.lateralMotor.setPower(this.POWER);
		
		if(this.forwardDirection) {
			this.lateralMotor.forward();
		} else {
			this.lateralMotor.backward();
		}
		
		// Wait to move the required distance
		try {
			sleep(this.movementDelay);
		} catch (InterruptedException e) {
		}

		this.stopMotor();
	}
	
	private void stopMotor() {
		this.isMoving = false;
		this.lateralMotor.flt();
	}
}

