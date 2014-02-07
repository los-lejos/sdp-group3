import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;


public class KickerTest {

	public static void main(String[] args) {
		NXTRegulatedMotor kickMotor = Motor.B;
		
		kickMotor.setSpeed(kickMotor.getMaxSpeed());
		kickMotor.forward();
		
		Button.waitForAnyPress();
	}
	
}
