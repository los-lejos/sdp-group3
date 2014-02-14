package robot.test;

import robot.DefenceRobot;

public class DefenceTester {

	public static void main(String[] args) throws Exception {
		DefenceRobot ballE = new DefenceRobot();
		
		ballE.moveLat(30);
		Thread.sleep(2000);
		ballE.moveLat(-30);
		Thread.sleep(2000);
		ballE.moveLat(30);
		
	}

}
