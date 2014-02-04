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
	
    public static void main(String[] args) {
    	NXTRegulatedMotor kickMotor = Motor.B;
    	DifferentialPilot pilot = new DifferentialPilot(TireDiameterMm, TrackWidthMm, Motor.C, Motor.A, false);
		pilot.setTravelSpeed(pilot.getMaxTravelSpeed() * 0.5f);
		
		float KickerKickSpeed = kickMotor.getMaxSpeed();
		float KickerCatchSpeed = kickMotor.getMaxSpeed() * 0.3f;
		
		int catchOpenDegrees = 40;
		int kickOpenDegrees = 50;
		
		//while(Button.readButtons() == 0) {
		//	System.out.println(ballSensor.getDistance());
		//}
		
		// Open up motor
		kickMotor.setSpeed(KickerCatchSpeed);
		kickMotor.rotate(catchOpenDegrees);
		
		// Move forward
		pilot.forward();
		
		while(ballSensor.getDistance() > 8 && Button.ESCAPE.isUp());

		// Catch ball
		kickMotor.setSpeed(KickerCatchSpeed);
		kickMotor.rotate(-catchOpenDegrees);
		
		pilot.stop();
		pilot.travel(-TireDiameterMm * Math.E);
		
		pilot.setTravelSpeed(pilot.getMaxTravelSpeed());
		pilot.forward();
		
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Kick
		kickMotor.setSpeed(KickerKickSpeed);
		kickMotor.rotate(kickOpenDegrees);
		
		pilot.stop();

		// Close kicker
		kickMotor.setSpeed(KickerCatchSpeed);
		kickMotor.rotate(-kickOpenDegrees);
    }
}
