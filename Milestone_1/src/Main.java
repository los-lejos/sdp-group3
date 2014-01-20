import lejos.nxt.*;

public class Main {
	
	public enum State {
		FORWARD, TURNING_RIGHT, TURNING_LEFT
	}
	
    public static final NXTRegulatedMotor leftMotor = Motor.A;
    public static final NXTRegulatedMotor rightMotor = Motor.C;
    
    public static long lastSensor = 0;
    public static State currentState = State.FORWARD;
    
    private static final int LightCutoff = 40;
    private static final int RobotMoveSpeed = 200;
    private static final int RobotTurnSpeed = 50;

	public static void main (String[] args) {
		LightSensor leftLight = new LightSensor(SensorPort.S1);
        LightSensor rightLight = new LightSensor(SensorPort.S4);

        // start moving to begin with
        rightMotor.setSpeed(RobotMoveSpeed);
        leftMotor.setSpeed(RobotMoveSpeed);
        leftMotor.backward();
        rightMotor.backward();
        
		  // Busy wait for a sensor to hit the white ground
		  while(leftLight.getLightValue() < LightCutoff &&
		          rightLight.getLightValue() < LightCutoff);

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
                    rightMotor.setSpeed(RobotTurnSpeed);
                    leftMotor.setSpeed(RobotTurnSpeed);
                    leftMotor.forward();	
                    rightMotor.backward();
        		} else {
        			System.out.println("Turning right.");
                    currentState = State.TURNING_RIGHT;
                    rightMotor.setSpeed(RobotTurnSpeed);
                    leftMotor.setSpeed(RobotTurnSpeed);
                    rightMotor.forward();
                    leftMotor.backward();
        		}
        	}
            
            if (currentState == State.FORWARD) {

                if (leftLight.getLightValue() >= LightCutoff) {
                    System.out.println("Turning right.");
                    currentState = State.TURNING_RIGHT;
                    rightMotor.setSpeed(RobotTurnSpeed);
                    leftMotor.setSpeed(RobotTurnSpeed);
                    rightMotor.forward();
                } else if (rightLight.getLightValue() >= LightCutoff) {
                    System.out.println("Turning left.");
                    currentState = State.TURNING_LEFT;
                    rightMotor.setSpeed(RobotTurnSpeed);
                    leftMotor.setSpeed(RobotTurnSpeed);
                    leftMotor.forward();
                }

            } else if (currentState == State.TURNING_RIGHT) {
                
                if (leftLight.getLightValue() < LightCutoff &&
                    rightLight.getLightValue() < LightCutoff) {
                    System.out.println("Going forward.");
                    currentState = State.FORWARD;
                    rightMotor.setSpeed(RobotMoveSpeed);
                    leftMotor.setSpeed(RobotMoveSpeed);
                    rightMotor.backward();
                }

            } else if (currentState == State.TURNING_LEFT) {

                if (rightLight.getLightValue() < LightCutoff &&
                	leftLight.getLightValue() < LightCutoff) {
                    System.out.println("Going forward.");
                    currentState = State.FORWARD;
                    rightMotor.setSpeed(RobotMoveSpeed);
                    leftMotor.setSpeed(RobotMoveSpeed);
                    leftMotor.backward();
                }

            } else
                System.out.println("Impossible state!");


            if (Button.readButtons() != 0)
                running = false;

        }


		
        Button.waitForAnyPress();
	}

}