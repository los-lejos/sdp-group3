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
	
	private boolean interrupted = false;
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
			this.movementDelay = (long) (absDist*16.5);
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
		} else {
			this.lateralMotor.backward();
		}
		
		// Wait to move the required distance
		long startTime = System.currentTimeMillis();
		while(!interrupted && System.currentTimeMillis() - startTime < this.movementDelay);
	}
	
	private void stopMotor() {
		this.isMoving = false;
		this.lateralMotor.flt();
	}
}

