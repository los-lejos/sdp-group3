package robot.attacker;

import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import robot.KickerController;

public class AttackKickerController extends KickerController {

	private final NXTMotor motor;

	private final int DELAY_OPEN = 90;
	private final int DELAY_CLOSE = 140;
	private final int DELAY_KICK = 120;
	private final int DELAY_KICK_CLOSE = 40;
	
	private final int DEFAULT_POWER = 100;
	private final int GRAB_POWER = 50;
	
	public AttackKickerController() {
		motor = new NXTMotor(MotorPort.B);
		motor.setPower(DEFAULT_POWER);
	}

	@Override
	protected void performOpen() throws InterruptedException {
		// Shut fully in case open
		motor.forward();
		Thread.sleep(DELAY_CLOSE);

		// Open
		motor.backward();
		Thread.sleep(DELAY_OPEN);

		motor.stop();
	}

	@Override
	protected void performKick() throws InterruptedException {
		// Shut fully in case open
		motor.forward();
		Thread.sleep(DELAY_CLOSE);
		
		// Release ball
		motor.backward();
		Thread.sleep(DELAY_KICK);

		motor.stop();
		Thread.sleep(600);

		// Return to default position
		motor.forward();
		Thread.sleep(DELAY_KICK_CLOSE);

		motor.stop();
	}
	
	@Override
	protected void performGrab() throws InterruptedException {
		motor.setPower(GRAB_POWER);
		motor.forward();
		Thread.sleep(DELAY_CLOSE);

		motor.stop();
		motor.setPower(DEFAULT_POWER);
	}
}