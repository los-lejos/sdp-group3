import lejos.nxt.*;

// remember that reverse is forward and vice versa due
// to the motor orientations!
public class Main {
	
	public enum State {
		FORWARD, TURNING_RIGHT, TURNING_LEFT
	}
	
    public static final NXTRegulatedMotor leftMotor = Motor.A;
    public static final NXTRegulatedMotor rightMotor = Motor.C;
    
    public static long lastSensor = 0;
    public static State currentState = State.FORWARD;
    
    private static final int LightCutoff = 40;
    private static final int RobotMoveSpeed = 720;
    private static final int RobotTurnSpeed = 720;
    private static final long ReverseTime = 2000;
    
	
	public static void main (String[] args) {
		LightSensor leftLight = new LightSensor(SensorPort.S1);
        LightSensor rightLight = new LightSensor(SensorPort.S4);
        
		System.out.println("Press a button for a great time!");
        Button.waitForAnyPress();

        // start moving to begin with
        rightMotor.setSpeed(RobotMoveSpeed);
        leftMotor.setSpeed(RobotMoveSpeed);
        leftMotor.backward();
        rightMotor.backward();

        // begin main loop
        boolean running = true;
        while (running) {

            // System.out.println(leftLight.getLightValue());
            
            if (currentState == State.FORWARD) {

                if (leftLight.getLightValue() >= LightCutoff) {
                    System.out.println("Turning right.");
                    currentState == State.TURNING_RIGHT;
                    rightMotor.forward();
                } else if (rightLight.getLightValue() >= LightCutoff) {
                    System.out.println("Turning left.");
                    currentState == State.TURNING_LEFT;
                    leftMotor.forward();
                }

            } else if (currentState == State.TURNING_RIGHT) {
                
                if (leftLight.getLightValue() < LightCutoff) {
                    System.out.println("Going forward.");
                    currentState == State.FORWARD;
                    rightMotor.backward();
                }

            } else if (currentState == State.TURNING_LEFT) {

                if (rightLight.getLightValue() < LightCutoff) {
                    System.out.println("Going forward.");
                    currentState == State.FORWARD;
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
