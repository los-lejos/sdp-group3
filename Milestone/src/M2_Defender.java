import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;

/*
 * @author Joris Urbaitis
 */

public class M2_Defender {
	public static final UltrasonicSensor ballSensor = new UltrasonicSensor(SensorPort.S2);
    
    private static final int TireDiameterMm = 62;
	private static final int TrackWidthMm = 144;
	
    public static void main(String[] args) {
    	ballSensor.reset();
    	ballSensor.continuous();
    	
    	NXTRegulatedMotor kickMotor = Motor.B;
    	DifferentialPilot pilot = new DifferentialPilot(TireDiameterMm, TrackWidthMm, Motor.C, Motor.A, false);
		pilot.setTravelSpeed(pilot.getMaxTravelSpeed() * 0.5f);
		
		float KickerKickSpeed = kickMotor.getMaxSpeed() * 0.5f;
		float KickerCatchSpeed = kickMotor.getMaxSpeed() * 0.3f;
		
		int catchOpenDegrees = 30;
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

		// Kick
		kickMotor.setSpeed(KickerKickSpeed);
		kickMotor.rotate(kickOpenDegrees);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Close kicker
		kickMotor.setSpeed(KickerCatchSpeed);
		kickMotor.rotate(-kickOpenDegrees);
    }
}
