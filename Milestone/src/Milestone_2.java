import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;


/*
 * @author Joris Urbaitis
 */

public class Milestone_2 {
	//private static final LightSensor RightLight = new LightSensor(SensorPort.S4);
    //private static final LightSensor LeftLight = new LightSensor(SensorPort.S1);
    public static final UltrasonicSensor ballSensor = new UltrasonicSensor(SensorPort.S2);
    
    private static final int TireDiameterMm = 62;
	private static final int TrackWidthMm = 144;
	
	private static double RobotMoveSpeed, RobotTurnSpeed;
    
    public static void main(String[] args) {
    	NXTRegulatedMotor kickMotor = Motor.B;
    	DifferentialPilot pilot = new DifferentialPilot(TireDiameterMm, TrackWidthMm, Motor.C, Motor.A, false);
    	RobotMoveSpeed = pilot.getMaxTravelSpeed();
		RobotTurnSpeed = pilot.getMaxTravelSpeed() * 0.18;
		pilot.setTravelSpeed(RobotMoveSpeed);
		
		//while(Button.readButtons() == 0) {
		//	System.out.println(ballSensor.getDistance());
		//}
		
		// Open up motor
		kickMotor.setSpeed(800);
		kickMotor.rotate(40);
		
		pilot.forward();
		
		while(ballSensor.getDistance() > 8 && Button.ESCAPE.isUp());

		kickMotor.setSpeed(800);
		kickMotor.rotate(-40);
		kickMotor.setSpeed(kickMotor.getMaxSpeed());
		kickMotor.rotate(50);
		
		pilot.stop();
		
		// Close the motor
		kickMotor.setSpeed(400);
		kickMotor.rotate(-50);
		
		Button.ESCAPE.waitForPressAndRelease();
    }
}
