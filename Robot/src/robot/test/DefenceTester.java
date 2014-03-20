package robot.test;

import lejos.nxt.Button;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;

public class DefenceTester {

	public static void main(String[] args) throws Exception {
		NXTMotor lateralMotor = new NXTMotor(MotorPort.C);
		boolean dir = false;
		
		while(Button.ESCAPE.isUp()) {
			if(Button.ENTER.isDown()) {
				System.out.println("Move");
				lateralMotor.setPower(100);
				if(dir) {
					lateralMotor.forward();
				} else {
					lateralMotor.backward();
				}
				
				dir = !dir;
				
				Thread.sleep(1000);
				lateralMotor.stop();
			}
		}
	}

}
