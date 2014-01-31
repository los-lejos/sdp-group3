import lejos.geom.Point;
import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Pose;

/*
 * @author Owen Gillespie
 * @author Craig Wilkinson
 * @author Joris Urbaitis
 * @author Andrew Johnston
 */

public class Milestone_1 {
	
	public enum State {
		FORWARD, TURNING_RIGHT, TURNING_LEFT
	}
	
	private static final int TireDiameterMm = 56;
	private static final int TrackWidthMm = 114;
	private static final int LightCutoff = 40;
    private static double RobotMoveSpeed;
    private static double RobotTurnSpeed;
	private static final LightSensor RightLight = new LightSensor(SensorPort.S4);
    private static final LightSensor LeftLight = new LightSensor(SensorPort.S1);
    public static final UltrasonicSensor FrontSensor = new UltrasonicSensor(SensorPort.S2);
    private static State currentState = State.FORWARD;

	public static void main(String[] args) {
		
		Pose startPose = null;
		Pose currentPose;
	    Point startPoint = null;
	    float dist = 0;
	    float heading = 0;
	    long inRangeTime = 0;
		
		// Configure pilot + tracker
		DifferentialPilot pilot = new DifferentialPilot(TireDiameterMm, TrackWidthMm, Motor.C, Motor.A, true);
		RobotMoveSpeed = pilot.getMaxTravelSpeed() * 0.4;
		RobotTurnSpeed = pilot.getMaxTravelSpeed() * 0.18;
        pilot.setTravelSpeed(RobotMoveSpeed);
        OdometryPoseProvider tracker = new OdometryPoseProvider(pilot);
        
        // Move forward until we hit white
        pilot.forward();
        while(RightLight.getLightValue() < LightCutoff &&
  		      LeftLight.getLightValue() < LightCutoff);
        
        // Store initial turn direction
        State greenDirection;
		if(RightLight.getLightValue() >= LightCutoff) {
			greenDirection = State.TURNING_LEFT;
			System.out.println("Left turn bias.");
		}
		else {
		  	greenDirection = State.TURNING_RIGHT;
		  	System.out.println("Right turn bias.");
		}
		
		// For checking we've moved away from start position
		boolean starting = true;

		// Main loop yo
        boolean running = true;
        boolean stopping = false;
        while (running) {
        	
        	System.out.println(dist);
        	// Avoid walls
        	
        	if (FrontSensor.getDistance() <= 14) {
        		System.out.println("Avoiding wall.");
        		pilot.setTravelSpeed(RobotTurnSpeed);
        		if (greenDirection == State.TURNING_LEFT) {
        			currentState = State.TURNING_LEFT;
                    pilot.steerBackward(-160);
        		} else {
        			currentState = State.TURNING_RIGHT;
                    pilot.steerBackward(160);
        		}
        	}
        	
        	// Corner case (both lights in white)
        	if (RightLight.getLightValue() >= LightCutoff && LeftLight.getLightValue() >= LightCutoff) {
        		System.out.println("Corner turn.");
        		if (greenDirection == State.TURNING_LEFT) {
                    currentState = State.TURNING_LEFT;
                    pilot.setTravelSpeed(RobotTurnSpeed);
                    pilot.steer(200);
        		} else {
                    currentState = State.TURNING_RIGHT;
                    pilot.setTravelSpeed(RobotTurnSpeed);
                    pilot.steer(-200);
        		}
        	}
        	
        	if (currentState == State.FORWARD) {
        		System.out.println("Forward.");
        		if (RightLight.getLightValue() >= LightCutoff) {
                    currentState = State.TURNING_LEFT;
                    pilot.setTravelSpeed(RobotTurnSpeed);
                    pilot.steer(40);
                } else if (LeftLight.getLightValue() >= LightCutoff) {
                    currentState = State.TURNING_RIGHT;
                    pilot.setTravelSpeed(RobotTurnSpeed);
                    pilot.steer(-40);
                }
        	
        	} else if (currentState == State.TURNING_LEFT) {
        		// Set start position if we are beginning to track edge
        		if (startPose == null) {
        			startPose = tracker.getPose();
        			startPoint = startPose.getLocation();
        		}
        		if (RightLight.getLightValue() < LightCutoff) {
	        		currentState = State.FORWARD;
	        		pilot.setTravelSpeed(RobotMoveSpeed);
	        		pilot.steer(-7);
        		}
        		
        	} else if (currentState == State.TURNING_RIGHT) {
        		// Set start position if we are beginning to track edge
        		if (startPose == null) {
        			startPose = tracker.getPose();
        			startPoint = startPose.getLocation();
        		}
        		if (LeftLight.getLightValue() < LightCutoff) {
	        		currentState = State.FORWARD;
	        		pilot.setTravelSpeed(RobotMoveSpeed);
	        		pilot.steer(7);
        		}
        		
        	} else {
        		System.out.println("Impossible state!");
        	}
        	
        	// Update distance to startPoint
        	if (startPoint != null) {
	        	currentPose = tracker.getPose();
	            dist = currentPose.distanceTo(startPoint);
	            heading = currentPose.getHeading();
        	}
	        
        	// Don't detect startPoint proximity until we've moved away
        	if (dist > 200) {
        		starting = false;
        	}
        	
        	// Detect when we're close to startPoint
        	if (!starting && !stopping && dist <= 180) {
        		System.out.println("Heading: " + heading);
        		if ((greenDirection == State.TURNING_RIGHT && heading > -95 && heading < 20) ||
        			(greenDirection == State.TURNING_LEFT && heading < 95 && heading > -20)) {
	        		inRangeTime = System.currentTimeMillis();
	        		stopping = true;
	        		System.out.println("Stopping...");
        		}
        	}
        	
        	// Keep going for a while once in range
        	if (stopping && System.currentTimeMillis() - inRangeTime >= 1700) {
        		running = false;
        	}
        	
        	// Kill by pressing something
        	if (Button.readButtons() != 0)
                running = false;
        	
        }
        
        System.out.println("Stopped!");
        pilot.stop();
        Button.waitForAnyPress();
	}

}
