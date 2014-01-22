import lejos.geom.Point;
import lejos.nxt.*;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Pose;

public class Main {
	
	public enum State {
		FORWARD, TURNING_RIGHT, TURNING_LEFT
	}
	
    public static final NXTRegulatedMotor leftMotor = Motor.A;
    public static final NXTRegulatedMotor rightMotor = Motor.C;
    
    public static State currentState = State.FORWARD;
    
    private static final int LightCutoff = 40;
    private static final double RobotMoveSpeed = 80;
    private static final double RobotTurnSpeed = 5;
    
    private static final int TireDiameterMm = 56;
    private static final int TrackWidthMm = 113;
    
	public static void main (String[] args) {
		LightSensor leftLight = new LightSensor(SensorPort.S1);
        LightSensor rightLight = new LightSensor(SensorPort.S4);
        
        DifferentialPilot pilot = new DifferentialPilot(TireDiameterMm, TrackWidthMm, leftMotor, rightMotor, true);
        
        // tracker provides a Pose updated every time pilot performs a move
        OdometryPoseProvider tracker = new OdometryPoseProvider(pilot);
        
        pilot.setTravelSpeed(RobotMoveSpeed);
        pilot.setRotateSpeed(RobotTurnSpeed);

        // start moving to begin with
        pilot.forward();

		// Busy wait for a sensor to hit the white ground
		while(leftLight.getLightValue() < LightCutoff &&
		      rightLight.getLightValue() < LightCutoff);
		
		// we will need to return here later
		Pose startPose = tracker.getPose();
		Point startPoint = startPose.getLocation();
		boolean starting = true;

		State greenDirection;
		if(leftLight.getLightValue() >= LightCutoff) {
			greenDirection = State.TURNING_RIGHT;
		}
		else {
		  	greenDirection = State.TURNING_LEFT;
		}

        // begin main loop
        boolean running = true;
        while (running) {
        	if(leftLight.getLightValue() >= LightCutoff && rightLight.getLightValue() >= LightCutoff) {
        		// Prefer the direction in which we started walking around the pitch
        		if(greenDirection == State.TURNING_LEFT) {
        			System.out.println("Turning left.");
                    currentState = State.TURNING_LEFT;
                    pilot.rotateLeft();
        		} else {
        			System.out.println("Turning right.");
                    currentState = State.TURNING_RIGHT;
                    pilot.rotateRight();
        		}
        	}
            
            if (currentState == State.FORWARD) {
                if (leftLight.getLightValue() >= LightCutoff) {
                    System.out.println("Turning right.");
                    currentState = State.TURNING_RIGHT;
                    pilot.rotateRight();
                } else if (rightLight.getLightValue() >= LightCutoff) {
                    System.out.println("Turning left.");
                    currentState = State.TURNING_LEFT;
                    pilot.rotateLeft();
                }
            } else if (currentState == State.TURNING_RIGHT || currentState == State.TURNING_LEFT) {
                if (leftLight.getLightValue() < LightCutoff &&
                    rightLight.getLightValue() < LightCutoff) {
                    System.out.println("Going forward.");
                    currentState = State.FORWARD;
                    pilot.forward();
                }
            } else {
                System.out.println("Impossible state!");
            }


            if (Button.readButtons() != 0)
                running = false;
            
            Pose currentPose = tracker.getPose();
            float dist = currentPose.distanceTo(startPoint);
        	System.out.println(dist);
        	
        	if(dist > 160) {
        		starting = false;
        	}
        	// for defender 60 works, attacker 100
        	else if(!starting && dist <= 60 /* empirical */) {
        		running = false;
        	}
        }

		pilot.stop();
		
        Button.waitForAnyPress();
	}

}