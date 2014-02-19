package robot.test;

import lejos.nxt.Button;
import robot.DefenceRobot;

public class DefenceTester {

	public static void main(String[] args) throws Exception {
		DefenceRobot ballE = new DefenceRobot();
		
		while (Button.readButtons() == 0) {
			ballE.moveLat(50);
			Thread.sleep(1500);
			ballE.moveLat(-50);
			Thread.sleep(1500);
		}
		
		ballE.stopLat();
		
	}

}
