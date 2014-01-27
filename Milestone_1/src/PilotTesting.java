import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;

public class PilotTesting {
	
	public enum State {
		FORWARD, TURNING_RIGHT, TURNING_LEFT
	}
	
    public static final NXTRegulatedMotor leftMotor = Motor.A;
    public static final NXTRegulatedMotor rightMotor = Motor.C;
    
    public static State currentState = State.FORWARD;
    
    private static double RobotMoveSpeed;
    private static double RobotTurnSpeed;
    
    private static final int TireDiameterMm = 56;
    private static final int TrackWidthMm = 114;
    
	public static void main (String[] args) {

        DifferentialPilot pilot = new DifferentialPilot(TireDiameterMm, TrackWidthMm, leftMotor, rightMotor, true);
        
        RobotMoveSpeed = pilot.getMaxTravelSpeed() * 0.6;
        RobotTurnSpeed = pilot.getMaxRotateSpeed() * 0.1;
        pilot.setTravelSpeed(RobotMoveSpeed);
        pilot.setRotateSpeed(RobotTurnSpeed);

        for (int i = 0; i < 4; i++) {
        	pilot.travel(50);
        	long beforeTime = System.currentTimeMillis();
        	while (System.currentTimeMillis() - beforeTime < 1500)
        		pilot.rotateLeft();
        }
        
        pilot.stop();
        Button.waitForAnyPress();
	}

}
