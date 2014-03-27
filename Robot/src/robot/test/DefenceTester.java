package robot.test;

import java.util.Arrays;

import lejos.nxt.Button;
import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.SensorPort;

public class DefenceTester {
	
	private static I2CPort I2Cport;
	private static I2CSensor I2Csensor;
	
	private static final int REGISTER_ADDRESS_STATE = 0x01;
	private static final int REGISTER_ADDRESS_SPEED = 0x02;
	
	private static final byte FORWARD = (byte) 2;
	private static final byte BACKWARD = (byte) 1;
	private static final byte STOP = (byte) 0;
	
	private static final byte KICK_SPEED = (byte) 100;

	public static void main(String[] args) throws Exception {
		I2Cport = SensorPort.S4;
		I2Cport.i2cEnable(I2CPort.STANDARD_MODE);
		//I2Csensor = new I2CSensor(I2Cport, 0x5A, I2CPort.STANDARD_MODE, I2CSensor.TYPE_CUSTOM);
		I2Csensor = new I2CSensor(I2Cport);
		//I2Csensor.setAddress(0x5A);
		I2Csensor.setAddress(0xB4);

		I2Csensor.sendData(REGISTER_ADDRESS_SPEED, KICK_SPEED);
		
		// Shut fully in case open
		I2Csensor.sendData(REGISTER_ADDRESS_STATE, FORWARD);
		while(Button.readButtons() == 0) {
			System.out.println("running");
		}
		
		I2Csensor.sendData(REGISTER_ADDRESS_STATE, STOP);
	}

}
