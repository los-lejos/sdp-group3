package robot.test;

import robot.DefenceRobot;

public class DefenceTester {

	public static void main(String[] args) throws Exception {
		DefenceRobot ballE = new DefenceRobot();
		
		ballE.moveTo(0, 30);
		ballE.moveTo(40, 0);
		ballE.moveTo(-90, 0);
		ballE.moveTo(45, -30);
		
		ballE.travelSidewise(80, 30, "left");
		Thread.sleep(2000);
		ballE.travelSidewise(100, 60, "right");
		Thread.sleep(2000);
		ballE.travelSidewise(80, 30, "left");
		
	}

}
