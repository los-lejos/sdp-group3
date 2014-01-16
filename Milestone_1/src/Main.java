import lejos.nxt.*;

public class Main {
	public static void main (String[] args) {
		LightSensor light = new LightSensor(SensorPort.S1);
		light.calibrateLow();
		
		Motor.A.setSpeed(720);
		Motor.C.setSpeed(720);
	  	Motor.A.forward();
		Motor.C.forward();

		int turns = 0;

		while(Button.readButtons() == 0 && 
			    turns < 4) {

            // drive until there is some white
            while(Button.readButtons() == 0 && 
                    light.getLightValue() < 30);
            
            Motor.A.reverse();

            // turn until there is no white
            while(Button.readButtons() == 0 && 
                    light.getLightValue() > 30);

            Motor.A.forward();

            turns++;
        }
            
        Button.waitForAnyPress();
	}
}
