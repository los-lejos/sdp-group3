import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;

/*
 * Simple movement test for robot with holonomic wheels
 */


public class DefenderWheels {
	
	private static final int TireDiameterMm = 62;
	private static final int TrackWidthMm = 144;
	
	public static void main(String[] args) {
	
//		NXTRegulatedMotor motor1 = Motor.A;
//		NXTRegulatedMotor motor2 = Motor.C;
//		motor1.setSpeed(100);
//		motor2.setSpeed(100);
//		
//		Button.waitForAnyPress();
//		
//		// first motor forwards
//		motor1.forward();
//		Button.waitForAnyPress();
//		motor1.stop();
//		
//		// second motor forwards
//		motor2.forward();
//		Button.waitForAnyPress();
//		motor2.stop();
//		
//		// first motor backwards
//		motor1.backward();	
//		Button.waitForAnyPress();
//		motor1.stop();
//		
//		// second motor backwards
//		motor2.backward();
//		Button.waitForAnyPress();
//		motor1.stop();
//		
//		// turn on axis
//		motor1.forward();
//		motor2.backward();
//		Button.waitForAnyPress();
//		motor1.stop();
//		motor2.stop();
//		
//		// turn on axis (other direction)
//		motor1.backward();
//		motor2.forward();
//		Button.waitForAnyPress();
//		motor1.stop();
//		motor2.stop();
//		
//		// both motors forwards
//		motor1.forward();
//		motor2.forward();
//		Button.waitForAnyPress();
//		motor1.stop();
//		motor2.stop();
//		
//		// both motors backwards
//		motor1.backward();
//		motor2.backward();
//		Button.waitForAnyPress();
//		motor1.stop();
//		motor2.stop();
		
		NXTRegulatedMotor motorLeft = Motor.A;
		NXTRegulatedMotor motorRight = Motor.B;
		NXTRegulatedMotor motorLateral = Motor.C;
		
		DifferentialPilot pilot = new DifferentialPilot(TireDiameterMm, TrackWidthMm, motorLeft, motorRight, false);
		pilot.setTravelSpeed(pilot.getMaxTravelSpeed() * 0.5);
		motorLateral.setSpeed(motorLateral.getMaxSpeed());

		motorLateral.backward();
		motorRight.setSpeed(200);
		motorRight.backward();
		//motorLeft.setSpeed(300);
		//motorLeft.forward();
		Button.waitForAnyPress();
		motorLateral.stop();
		motorRight.stop();
		//motorLeft.stop();
		
		motorLateral.forward();
		motorLeft.setSpeed(200);
		motorLeft.backward();
		//motorRight.setSpeed(300);
		//motorRight.forward();
		Button.waitForAnyPress();
		motorLateral.stop();
		motorLeft.stop();
		//motorRight.stop();
	}

}
