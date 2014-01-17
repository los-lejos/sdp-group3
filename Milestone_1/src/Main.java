import lejos.nxt.*;

public class Main {
	
	public enum State {
		FORWARD, TURNING_RIGHT, TURNING_LEFT
	}
	
    public static NXTRegulatedMotor leftMotor = Motor.A;
    public static NXTRegulatedMotor rightMotor = Motor.C;
    public static State currentState = State.FORWARD;
	
	public static void main (String[] args) {
		LightSensor lightL = new LightSensor(SensorPort.S1);
        LightSensor lightR = new LightSensor(SensorPort.S2);
        
		lightL.calibrateLow();
        lightR.calibrateLow();

		rightMotor.setSpeed(720);
		leftMotor.setSpeed(720);
	  	leftMotor.forward();
		rightMotor.forward();

		// int turns = 0;
		
		while(Button.readButtons() == 0) {
			
			// Keep going forward until we see white.
			while(Button.readButtons() == 0 &&
                    lightL.getLightValue() < 30 &&
                    lightR.getLightValue() < 30);
			
			// Turn away from white.
			if (lightL.getLightValue() >= 30) {
				currentState = State.TURNING_RIGHT;
				rightMotor.backward();
			} else {
				currentState = State.TURNING_LEFT;
				leftMotor.backward();
			}

			// Keep turning until no white under relevant sensor.
			while(Button.readButtons() == 0 &&
                    currentState == State.TURNING_LEFT ||
                    currentState == State.TURNING_RIGHT) {
				if (lightL.getLightValue() < 30) {
					rightMotor.forward();
					currentState = State.FORWARD;
				} else {
					leftMotor.forward();
					currentState = State.FORWARD;
				}
			}

		}
		
        Button.waitForAnyPress();
	}

}
