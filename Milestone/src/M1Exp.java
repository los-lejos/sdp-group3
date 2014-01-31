import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.robotics.navigation.DifferentialPilot;

public class M1Exp {
	
	public enum State {
		FORWARD, TURNING_RIGHT, TURNING_LEFT
	}
	
	private static final int TireDiameterMm = 56;
	private static final int TrackWidthMm = 114;
	private static final int LightCutoff = 40;
    private static double RobotMoveSpeed;
    private static double RobotTurnSpeed;
	private static final LightSensor LeftLight = new LightSensor(SensorPort.S4);
    private static final LightSensor RightLight = new LightSensor(SensorPort.S1);
    public static State currentState = State.FORWARD;

	public static void main(String[] args) {
		
		// Configure pilot
		DifferentialPilot pilot = new DifferentialPilot(TireDiameterMm, TrackWidthMm, Motor.A, Motor.C, true);
		RobotMoveSpeed = pilot.getMaxTravelSpeed() * 0.4;
		RobotTurnSpeed = pilot.getMaxTravelSpeed() * 0.25;
        pilot.setTravelSpeed(RobotMoveSpeed);
        
        // Move forward until we hit white
        pilot.forward();
        while(LeftLight.getLightValue() < LightCutoff &&
  		      RightLight.getLightValue() < LightCutoff);
        
        // Store initial turn direction
        State greenDirection;
		if(LeftLight.getLightValue() >= LightCutoff) {
			greenDirection = State.TURNING_RIGHT;
		}
		else {
		  	greenDirection = State.TURNING_LEFT;
		}
        
        boolean running = true;
        while (running) {
        	
        	// Corner case (both lights in white)
        	if (LeftLight.getLightValue() >= LightCutoff && RightLight.getLightValue() >= LightCutoff) {
        		if(greenDirection == State.TURNING_LEFT) {
        			System.out.println("Turning left.");
                    currentState = State.TURNING_LEFT;
                    pilot.setTravelSpeed(RobotTurnSpeed);
                    pilot.steer(200);
        		} else {
        			System.out.println("Turning right.");
                    currentState = State.TURNING_RIGHT;
                    pilot.setTravelSpeed(RobotTurnSpeed);
                    pilot.steer(-200);
        		}
        	}
        	
        	if (currentState == State.FORWARD) {
        		if (LeftLight.getLightValue() >= LightCutoff) {
                    currentState = State.TURNING_RIGHT;
                    pilot.setTravelSpeed(RobotTurnSpeed);
                    pilot.steer(-40);
                } else if (RightLight.getLightValue() >= LightCutoff) {
                    currentState = State.TURNING_LEFT;
                    pilot.setTravelSpeed(RobotTurnSpeed);
                    pilot.steer(40);
                }
        	} else if (currentState == State.TURNING_LEFT) {
        		if (RightLight.getLightValue() < LightCutoff) {
	        		currentState = State.FORWARD;
	        		pilot.setTravelSpeed(RobotMoveSpeed);
	        		pilot.steer(-5);
        		}
        	} else if (currentState == State.TURNING_RIGHT) {
        		if (LeftLight.getLightValue() < LightCutoff) {
	        		currentState = State.FORWARD;
	        		pilot.setTravelSpeed(RobotMoveSpeed);
	        		pilot.steer(5);
        		}
        	} else {
        		System.out.println("Impossible state!");
        	}
        	
        	if (Button.readButtons() != 0)
                running = false;
        }
        
        pilot.stop();
        Button.waitForAnyPress();
	}

}
