package robot.test;

import lejos.nxt.Button;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import robot.defender.DefenceRobot;

public class DefenceTester {

	public static void main(String[] args) throws Exception {
		UltrasonicSensor ballSense = new UltrasonicSensor(SensorPort.S2);
		DefenceRobot ballE = new DefenceRobot();
	
/*		
		while (Button.readButtons() == 0) {
			ballE.moveLat(50);
			Thread.sleep(1500);
			ballE.moveLat(-50);
			Thread.sleep(1500);
		}
		
		ballE.stopLat();
		
		
		ballE.move(500);
		System.out.println("Forward - Done");
		
		ballE.moveLat(80);
		Thread.sleep(5000);
		ballE.stopLat();
		System.out.println("Left - Done");
		
		ballE.move(-500);
		System.out.println("Backward - Done");
		
		ballE.moveLat(-80);
		Thread.sleep(5000);
		ballE.stopLat();
		System.out.println("Right - Done");
		System.out.println("Done :) ");
*/
		while (Button.readButtons() == 0) {
			System.out.println(ballSense.getDistance());
			if (ballSense.getDistance() <= 10){
				ballE.getKicker().grab();
				
				ballE.getKicker().kick();
			}
		}
		
		Button.waitForAnyPress();
	}

}
