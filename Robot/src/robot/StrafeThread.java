package robot;

import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;

public class StrafeThread extends Thread {
	
	private final NXTMotor lateralMotor;
	private final double lateralPowerMultiplier;
	private final int lateralMinPower;
	
	private StrafeState state = StrafeState.READY;
	private int power;
	private int prevPower;
	
	public StrafeThread(DefenceRobot robot) {
		this.setDaemon(true);
		lateralMotor = new NXTMotor(MotorPort.C);
		
		// Set up strafing motor.
		this.lateralPowerMultiplier = 7;
		this.lateralMinPower = 20;
		lateralMotor.setPower(0);
		lateralMotor.forward();
	}
	
	@Override
	public void run() {
		while (state != StrafeState.EXIT) {
			if (state == StrafeState.STRAFE) {
				moveLat(power);
				state = StrafeState.READY;
			} else if (state == StrafeState.STOP) {
				stopLat();
				state = StrafeState.READY;
			}
		}
	}
	
	private void moveLat(int power) {
		int absPower = Math.abs(power);
		int motorPower = (int) (lateralPowerMultiplier * absPower + lateralMinPower);
		lateralMotor.setPower(motorPower);
		this.flipLat(power);
	}
	
	/*
	 * Updates strafing direction.
	 */
	private void flipLat(int power) {
		if (this.prevPower < 0 && power >= 0) {
			
			// Set power to 0, stop smoothly.
			lateralMotor.flt();
			
			// Allow wheel to spin out.
			try {
				lateralMotor.wait(800);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// Turn the other way
			lateralMotor.backward();
		} else if (this.prevPower >= 0 && power < 0) {
			lateralMotor.flt();
			
			try {
				lateralMotor.wait(800);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			lateralMotor.forward();
		}
	}
	
	private void stopLat() {
		lateralMotor.flt();
	}
	
	public void updateLat(StrafeState state, int power) {
		this.power = power;
		this.state = state;
	}
	
	public void updateLat(StrafeState state) {
		updateLat(state, 0);
	}
}

