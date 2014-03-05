package robot.attacker;

import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import robot.KickerController;

public class AttackKickerController extends KickerController {

	private final NXTMotor motor;
	
	public AttackKickerController() {
		motor = new NXTMotor(MotorPort.B);
		motor.setPower(100);
	}

	@Override
	protected void performOpen() throws InterruptedException {
		// Shut fully in case open
		motor.forward();
		Thread.sleep(1000);

		// Open
		motor.backward();
		Thread.sleep(120);

		motor.stop();
	}

	@Override
	protected void performKick() throws InterruptedException {
		// Release ball
		motor.backward();
		Thread.sleep(100);

		motor.stop();
		Thread.sleep(600);

		// Return to default position
		motor.forward();
		Thread.sleep(360);

		motor.stop();
		
		// Open
		motor.backward();
		Thread.sleep(120);

		motor.stop();
	}
	
	@Override
	protected void performGrab() throws InterruptedException {
		motor.setPower(50);
		motor.forward();
		Thread.sleep(50);

		motor.stop();
		motor.setPower(100);
	}
}