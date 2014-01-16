import lejos.nxt.*;

public class Main {
	public static void main (String[] args) {
		LightSensor light = new LightSensor(SensorPort.S1);
		light.calibrateLow();
		
		Motor.A.setSpeed(720);
		Motor.C.setSpeed(720);
	  	Motor.A.forward();
		Motor.C.forward();

		while(Button.readButtons() == 0 && 
				light.getLightValue() < 30);
		
		Motor.A.stop();
		Motor.C.stop();
		Button.waitForAnyPress();
	}
}