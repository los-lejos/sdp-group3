import lejos.nxt.*;

public class Main {
	
	public enum State {
		FORWARD, TURNING_RIGHT, TURNING_LEFT
	}
	
    public static final NXTRegulatedMotor leftMotor = Motor.A;
    public static final NXTRegulatedMotor rightMotor = Motor.C;
    public static State currentState = State.FORWARD;
    
    public static long lastSensor = 0;
    
    private static final int LightCutoff = 40;
    private static final int RobotMoveSpeed = 720;
    private static final int RobotTurnSpeed = 720;
    private static final long ReverseTime = 2000;
    
	
	public static void main (String[] args) {
		LightSensor lightL = new LightSensor(SensorPort.S1);
        LightSensor lightR = new LightSensor(SensorPort.S4);
        
		System.out.println("Press a button for a great time!");
        Button.waitForAnyPress();

		rightMotor.setSpeed(RobotMoveSpeed);
		leftMotor.setSpeed(RobotMoveSpeed);
	  	leftMotor.backward();
		rightMotor.backward();

		while(Button.readButtons() == 0) {
			System.out.println(lightL.getLightValue());
			// Keep going forward until we see white.
			while(Button.readButtons() == 0 &&
                    lightL.getLightValue() < LightCutoff &&
                    lightR.getLightValue() < LightCutoff)
			{
				System.out.println(lightL.getLightValue());
			}
			
			// Turn away from white.
			// when detecting with left sensor, turn right
			if (lightL.getLightValue() >= LightCutoff) {
				// if both sensors detected white at roughly the same time
				if (lightR.getLightValue() >= LightCutoff) {
					System.out.println("reversing");
					
					// reverse for a period, not ideal
					leftMotor.forward();
					rightMotor.forward();
					
					long timeNow = System.currentTimeMillis();
					while (System.currentTimeMillis() < timeNow + ReverseTime);
					
					leftMotor.backward();
					rightMotor.backward();
				} else {
					System.out.println("Turning right");

					lastSensor = System.currentTimeMillis();

					currentState = State.TURNING_RIGHT;
					rightMotor.setSpeed(RobotTurnSpeed);
					leftMotor.setSpeed(RobotTurnSpeed);
					rightMotor.forward();
					
					// Keep turning until no white under relevant sensor.
					while(Button.readButtons() == 0 &&
		                    currentState == State.TURNING_RIGHT) {
						System.out.println(lightL.getLightValue());
						if (lightL.getLightValue() < LightCutoff) {
							rightMotor.setSpeed(RobotMoveSpeed);
							leftMotor.setSpeed(RobotMoveSpeed);
							rightMotor.backward();
							currentState = State.FORWARD;
						}
					}
				}
			} else { // when detecting with left sensor, turn right
				// if both sensors detected white at roughly the same time
				if (lightL.getLightValue() >= LightCutoff) {
					System.out.println("reversing");
					
					// reverse for a period, not ideal
					leftMotor.forward();
					rightMotor.forward();
					
					long timeNow = System.currentTimeMillis();
					while (System.currentTimeMillis() < timeNow + ReverseTime);
					
					leftMotor.backward();
					rightMotor.backward();
				} else {
					System.out.println("Turning left");

					lastSensor = System.currentTimeMillis();

					currentState = State.TURNING_LEFT;
					rightMotor.setSpeed(RobotTurnSpeed);
					leftMotor.setSpeed(RobotTurnSpeed);
					leftMotor.forward();
					
					// Keep turning until no white under relevant sensor.
					while(Button.readButtons() == 0 &&
		                    currentState == State.TURNING_LEFT) {
						System.out.println(lightR.getLightValue());
						if (lightR.getLightValue() < LightCutoff) {		
							rightMotor.setSpeed(RobotMoveSpeed);
							leftMotor.setSpeed(RobotMoveSpeed);
							leftMotor.backward();
							currentState = State.FORWARD;
						}
					}
				}
			}
		}
		
        Button.waitForAnyPress();
	}

}
