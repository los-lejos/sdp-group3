package robot.test;

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;

public class DefenceTester {

	public static void main(String[] args) throws Exception {
		ColorSensor s = new ColorSensor(SensorPort.S2);
		
		while(Button.ESCAPE.isUp()) {
			System.out.println(s.getColorID());
			System.out.println(s.getColor().getRed());
		}
	}

}
