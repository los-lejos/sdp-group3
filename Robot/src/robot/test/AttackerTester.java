package robot.test;

import lejos.nxt.Button;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

public class AttackerTester {

	public static void main(String[] args) throws InterruptedException {
		UltrasonicSensor ballSense = new UltrasonicSensor(SensorPort.S2);
		//AttackRobot optimusPrime = new AttackRobot();
	
/*		while(optimusPrime.getBallSensor().getDistance() > 12){
			System.out.println(optimusPrime.getBallSensor().getDistance());
			optimusPrime.moveF();
		}
//		optimusPrime.stop();
//		System.out.println("Stopped");
		optimusPrime.grab();
		System.out.println("Grabbed the ball");
		optimusPrime.kick();
		System.out.println("Kicked the ball");

*/	
/*
		optimusPrime.run();
		Thread.sleep(5000);
		optimusPrime.stop();
		
		System.out.println("Start a square movement fashion");
		optimusPrime.rotate(-90);
		optimusPrime.move(100);
		optimusPrime.rotate(90);
		optimusPrime.move(100);
		optimusPrime.rotate(90);
		optimusPrime.move(100);
		optimusPrime.rotate(90);+
		
		optimusPrime.move(100);
		optimusPrime.rotate(-90);
		System.out.println("Complete the square");
		
		optimusPrime.move(100);
		optimusPrime.rotate(-180);
		System.out.println("I should be back in the starting position :) ");
		
 		optimusPrime.move(100);
		optimusPrime.rotate(90);
*/
		
//		while (Button.readButtons() == 0) {
//			System.out.println(ballSense.getDistance());
//			if (ballSense.getDistance() <= 12){
//				optimusPrime.getKicker().grab();
//				
//				Thread.sleep(1000);
//				
//				optimusPrime.getKicker().kick();
//				break;
//			}
//		}
		while(Button.readButtons() == 0){
		System.out.println(ballSense.getDistance());
		}
		Button.waitForAnyPress();
	}

}
