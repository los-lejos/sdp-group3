package robot.test;

import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;


public class DefenceTester {

	public static void main(String[] args) throws Exception {
		NXTMotor lateral = new NXTMotor(MotorPort.C);
		lateral.setPower(100);
		lateral.forward();
		Thread.sleep(2000);
		lateral.stop();
	}


}
