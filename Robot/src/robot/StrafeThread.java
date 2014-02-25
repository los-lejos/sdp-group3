package robot;

import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;

public class StrafeThread extends Thread {
	
	private final NXTMotor lateralMotor;
	private final double lateralPowerMultiplier;
	private final int lateralMinPower;
	
	private StrafeState state = StrafeState.READY;
	private int power = 0;
	private int prevPower = 0;
	
	public StrafeThread() {
		this.setDaemon(true);
		lateralMotor = new NXTMotor(MotorPort.C);
		
		// Set up strafing motor.
		this.lateralPowerMultiplier = 7;
		this.lateralMinPower = 20;
		lateralMotor.setPower(this.power);
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
		System.out.println("Exited loop");
		stopLat();
	}
	
	private void moveLat(int power) {
		System.out.println("MoveLat called in Strafethread.");
		int absPower = Math.abs(power);
		int motorPower = (int) (lateralPowerMultiplier * absPower + lateralMinPower);
		System.out.println(motorPower);
		lateralMotor.setPower(motorPower);
		this.flipLat(power);
		System.out.println(lateralMotor.getPower());
	}
	
	/*
	 * Updates strafing direction.
	 */
	private void flipLat(int power) {
		System.out.println(prevPower);
		System.out.println(power);
		
		if (this.prevPower < 0 && power >= 0) {
			// Set power to 0, stop smoothly.
			lateralMotor.flt();
			System.out.println("Floated");
			
			// Allow wheel to spin out.
			try {
				sleep(800);
				System.out.println("Slept.");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// Turn the other way
			lateralMotor.backward();
			System.out.println("Flipped backward.");
		} else if (this.prevPower >= 0 && power < 0) {
			lateralMotor.flt();
			System.out.println("Floated");
			
			try {
				sleep(800);
				System.out.println("Slept.");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			lateralMotor.forward();
			System.out.println("Flipped forward.");
		}
	}
	
	private void stopLat() {
		lateralMotor.flt();
	}
	
	public void updateLat(StrafeState state, int power) {
		this.prevPower = this.power;
		this.power = power;
		this.state = state;
	}
	
	public void updateLat(StrafeState state) {
		updateLat(state, 0);
	}
}

