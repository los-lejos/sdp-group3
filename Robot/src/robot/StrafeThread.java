package robot;

import java.io.IOException;

import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import robot.communication.BluetoothCommunicationException;
import robot.communication.BluetoothDiceConnection;
import shared.RobotInstructions;

public class StrafeThread extends Thread {
	
	private enum StrafeState {
		READY, STRAFE, STOP, EXIT
	}
	
	private final int POWER = 100;
	private final int MAX_DELAY = 3000;

	private final NXTMotor lateralMotor;

	private Object updateLock = new Object();
	
	private StrafeState state = StrafeState.READY;
	private boolean forwardDirection;
	private long movementDelay = 0;
	
	private StrafeState newState = StrafeState.READY;
	private boolean newForwardDirection;
	private long newMovementDelay = 0;

	private boolean interrupted = false;
	private boolean isMoving = false;
	
	private boolean isAttacker;
	
	private BluetoothDiceConnection conn;

	public StrafeThread(boolean isAttacker) {
		this.isAttacker = isAttacker;
		this.setDaemon(true);
		lateralMotor = new NXTMotor(MotorPort.C);
		lateralMotor.flt();
	}
	
	public void setCommunicator(BluetoothDiceConnection conn) {
		this.conn = conn;
	}
	
	public void cleanup() {
		this.newState = StrafeState.EXIT;
	}
	
	public boolean isMoving() {
		return this.isMoving;
	}
	
	public void move(int distance) {
		synchronized(updateLock) {
			this.interrupted = true;
			
			// Calculations based on power being 100,
			// can find a function that fits this, but this is simpler to adjust
			int absDist = Math.abs(distance);
			if(absDist <= 5) {
				this.newMovementDelay = (long) (absDist*38);
			} else if(absDist <= 15) {
				this.newMovementDelay = (long) (absDist*26);
			} else {
				this.newMovementDelay = (long) (absDist*22);
			}
	
			if(this.newMovementDelay > MAX_DELAY) {
				this.newMovementDelay = MAX_DELAY;
			}
			
			this.newForwardDirection = distance > 0;
			this.newState = StrafeState.STRAFE;
		}
	}
	
	public void stop() {
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

			synchronized(updateLock) {
				if(newState != StrafeState.READY) {
					state = newState;
					newState = StrafeState.READY;
					movementDelay = newMovementDelay;
					forwardDirection = newForwardDirection;
					interrupted = false;
				}
			}
			
			if(this.isMoving && state == StrafeState.READY) {
				stopMotor();
			}
		}
		
		stopMotor();
	}
	
	private void moveLat() {
		//this.sendStrafeStartMessage();
		
		this.isMoving = true;
		
		this.lateralMotor.setPower(this.POWER);
		
		if(this.forwardDirection) {
			this.lateralMotor.forward();
		} else {
			this.lateralMotor.backward();
		}
		
		// Wait to move the required distance
		long startTime = System.currentTimeMillis();
		while(!interrupted && System.currentTimeMillis() - startTime < this.movementDelay);

		//this.sendStrafeEndMessage();
	}
	
	private void stopMotor() {
		this.isMoving = false;
		this.lateralMotor.stop();

//		// Wait for motor to wind down
//		try {
//			Thread.sleep(300);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
	
	private void sendStrafeStartMessage() {
		// Notify DICE that we started strafing
		byte[] startedStrafing = {RobotInstructions.STRAFE_START, 0, 0, 0};
		
		try {
			conn.send(startedStrafing);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BluetoothCommunicationException e) {
			e.printStackTrace();
		}
	}
	
	private void sendStrafeEndMessage() {
		// Notify DICE that we stopped strafing
		byte[] startedStrafing = {RobotInstructions.STRAFE_END, 0, 0, 0};
		
		try {
			conn.send(startedStrafing);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BluetoothCommunicationException e) {
			e.printStackTrace();
		}
	}
}

