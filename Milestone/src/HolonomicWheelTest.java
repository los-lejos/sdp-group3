import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

/*
 * Simple movement test for robot with holonomic wheels
 */


public class HolonomicWheelTest {
	
	public static void main(String[] args) {
	
		NXTRegulatedMotor motor1 = Motor.A;
		NXTRegulatedMotor motor2 = Motor.C;
		motor1.setSpeed(100);
		motor2.setSpeed(100);
		
		Button.waitForAnyPress();
		
		// first motor forwards
		motor1.forward();
		Button.waitForAnyPress();
		motor1.stop();
		
		// second motor forwards
		motor2.forward();
		Button.waitForAnyPress();
		motor2.stop();
		
		// first motor backwards
		motor1.backward();	
		Button.waitForAnyPress();
		motor1.stop();
		
		// second motor backwards
		motor2.backward();
		Button.waitForAnyPress();
		motor1.stop();
		
		// turn on axis
		motor1.forward();
		motor2.backward();
		Button.waitForAnyPress();
		motor1.stop();
		motor2.stop();
		
		// turn on axis (other direction)
		motor1.backward();
		motor2.forward();
		Button.waitForAnyPress();
		motor1.stop();
		motor2.stop();
		
		// both motors forwards
		motor1.forward();
		motor2.forward();
		Button.waitForAnyPress();
		motor1.stop();
		motor2.stop();
		
		// both motors backwards
		motor1.backward();
		motor2.backward();
		Button.waitForAnyPress();
		motor1.stop();
		motor2.stop();
		
		Button.waitForAnyPress();
	
	}

}
