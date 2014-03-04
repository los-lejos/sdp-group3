package robot.defender;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.SensorPort;
import robot.KickerController;

public class DefenceKickerController extends KickerController {
	
	private I2CPort I2Cport;
	private I2CSensor I2Csensor;
	
	private static final byte FORWARD = (byte) 1;
	private static final byte BACKWARD = (byte) 2;
	private static final byte STOP = (byte) 0;
	
	private static final byte KICK_SPEED = (byte) 200;
	private static final byte CATCH_SPEED = (byte) 120;

	@SuppressWarnings("deprecation")
	public DefenceKickerController() {
		// Set up multiplexer stuff
		I2Cport = SensorPort.S4;
		I2Cport.i2cEnable(I2CPort.STANDARD_MODE);
		I2Csensor = new I2CSensor(I2Cport);
		I2Csensor.setAddress(0xB4);
	}
	
	protected void performOpen() {
		// Shut fully in case open
		I2Csensor.sendData(0x01, FORWARD);
		I2Csensor.sendData(0x02, KICK_SPEED);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println("Kicker wait exception");
		}
		I2Csensor.sendData(0x01, STOP);
		
		// Open
		I2Csensor.sendData(0x01, BACKWARD);
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			System.out.println("Kicker wait exception");
		}
		I2Csensor.sendData(0x01, STOP);
	}

	protected void performKick() {
		// Release ball
		I2Csensor.sendData(0x02, KICK_SPEED);
		I2Csensor.sendData(0x01, BACKWARD);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			System.out.println("Kicker wait exception");
		}
		I2Csensor.sendData(0x01, STOP);
	}
	
	protected void performGrab() {
		System.out.println("Grabbed.");
		I2Csensor.sendData(0x02, CATCH_SPEED);
		I2Csensor.sendData(0x01, FORWARD);
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			System.out.println("Kicker wait exception");
		}
		
		I2Csensor.sendData(0x01, STOP);
	}
}
