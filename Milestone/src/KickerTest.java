import lejos.nxt.Button;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;


public class KickerTest {
	
	public static void travel(NXTMotor motor, int power, double distance, String direction) throws Exception{
		motor.setPower(power); // between 0% and 100%
		if (direction.equals("left")){
			motor.forward();
		} else if (direction.equals("right")){
			motor.backward();
		} else {
			System.out.println("Unknown direction");
		}				
		Thread.sleep((int) (distance*1000/59.4)); // the speed of the robot (having considered its current weight) is 59.4cm/sec
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
		travel (lateralMotor, 100, 14.85, "left");

		
		Button.waitForAnyPress();
		
	}
	
}
