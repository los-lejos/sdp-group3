import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
// import lejos.nxt.LightSensor;

/*
 * @author Owen Gillespie
 */

public class PilotTesting {
	
	public enum State {
		FORWARD, TURNING_RIGHT, TURNING_LEFT
	}
	
    public static final UltrasonicSensor frontSensor = new UltrasonicSensor(SensorPort.S2);
    public static State currentState = State.FORWARD;
    
    //private static double RobotMoveSpeed;
    private static double RobotTurnSpeed;
    
    private static final int TireDiameterMm = 56;
    private static final int TrackWidthMm = 114;
    
	public static void main (String[] args) {

		DifferentialPilot pilot = new DifferentialPilot(TireDiameterMm, TrackWidthMm, Motor.C, Motor.A, true);
		//RobotMoveSpeed = pilot.getMaxTravelSpeed() * 0.4;
		RobotTurnSpeed = pilot.getMaxTravelSpeed() * 0.18;
        //OdometryPoseProvider tracker = new OdometryPoseProvider(pilot);
        //Pose startPose;
        //float startHeading;
        //float headingDiff;
        
        //RobotMoveSpeed = pilot.getMaxTravelSpeed() * 0.5;
        RobotTurnSpeed = pilot.getMaxRotateSpeed() * 0.15;
        pilot.setTravelSpeed(RobotTurnSpeed);
        // pilot.setRotateSpeed(RobotTurnSpeed);
        //startPose = tracker.getPose();
        //startHeading = startPose.getHeading();

        // Left
        // pilot.steer(200);
        // Right
        // pilot.steer(-200);
        // Right
        // pilot.steerBackward(200);
        // Left
        // pilot.steerBackward(-200);
        
        boolean running = true;
        while (running) {
        	// headingDiff = tracker.getPose().getHeading() - startHeading;
        	// System.out.println(RightLight.getLightValue());
        	System.out.println(frontSensor.getDistance());
        	if (Button.readButtons() != 0) {
        		running = false;
        	}
        }
        
        pilot.stop();
        Button.waitForAnyPress();
	}

}
