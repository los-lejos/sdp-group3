import lejos.nxt.Button;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;


public class LateralMovementTest {
	
	public static void travel(NXTMotor motor, int power, double distance, String direction) throws Exception{
		motor.setPower(power); // between 0% and 100%
		if (direction.equals("left")){
			motor.forward();
		} else if (direction.equals("right")){
			motor.backward();
		} else {
			System.out.println("Unknown direction");
		}
		// The following is an example of "time = distance/speed". It is multiplied by 1000 because "Thread.sleep" is in milliseconds.
		if (power == 100){
			Thread.sleep((int) (distance*1000/48)); // the speed of the robot (having considered its current weight) is 48cm/sec at 100% power
		} else if (power == 90){
			Thread.sleep((int) (distance*1000/40.4)); // the speed of the robot (having considered its current weight) is 40.4cm/sec at 90% power									
		} else if (power == 80){
			Thread.sleep((int) (distance*1000/33.1)); // the speed of the robot (having considered its current weight) is 33.1cm/sec at 80% power
		} else if (power == 70){
			Thread.sleep((int) (distance*1000/29.8)); // the speed of the robot (having considered its current weight) is 29.8cm/sec at 70% power
		} else {
			System.out.println("Better stick to values of 100%, 90%, 80%, 70%");
		}			
		motor.stop();
	}

	public static void main(String[] args) throws Exception {
/*
		RegulatedMotor kickMotor = Motor.A;		
		
//		kickMotor.setSpeed(kickMotor.getMaxSpeed());
		kickMotor.setSpeed(100);
		kickMotor.forward();		
		
		Motor.C.setSpeed(200);
		Motor.C.forward();
		Thread.sleep(1000);
		Motor.C.stop();
*/

		NXTMotor lateralMotor = new NXTMotor(MotorPort.C);

		travel (lateralMotor, 100, 48, "left");		
		Button.waitForAnyPress();
		
		travel (lateralMotor, 90, 40.4, "left");		
		
		Button.waitForAnyPress();
		
		travel (lateralMotor, 80, 33.1, "left");		
		Button.waitForAnyPress();
		
		travel (lateralMotor, 70, 29.8, "left");		
		Button.waitForAnyPress();
		
		travel (lateralMotor, 60, 14.85, "left");		
		Button.waitForAnyPress();

	}
	
}
