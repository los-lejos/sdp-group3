import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.robotics.RegulatedMotor;


public class KickerTest {

	public static void main(String[] args) throws InterruptedException {
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
		NXTMotor mamkamu = new NXTMotor(MotorPort.C);
		
		mamkamu.setPower(100);
		mamkamu.forward();
		
		Button.waitForAnyPress();
	}
	
}
