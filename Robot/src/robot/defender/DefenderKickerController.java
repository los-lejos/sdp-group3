package robot.defender;

import robot.KickerController;
import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.SensorPort;

public class DefenderKickerController extends KickerController {

	private static final int REGISTER_ADDRESS_STATE = 0x01;
	private static final int REGISTER_ADDRESS_SPEED = 0x02;
	
	private static final byte FORWARD = (byte) 1;
	private static final byte BACKWARD = (byte) 2;
	private static final byte STOP = (byte) 0;
	
	private static final byte RETAINMENT_SPEED = (byte)30;
	private static final byte KICK_SPEED = (byte) 255;
	private static final byte CATCH_SPEED = (byte) 100;
	
	private static final int DELAY_OPEN = 90;
	private static final int DELAY_KICK = 40;
	private static final int DELAY_KICK_END = 500;
	private static final int DELAY_CLOSE = 130;
	private static final int DELAY_STOP = 200;
	
	private I2CPort I2Cport;
	private I2CSensor I2Csensor;

	@SuppressWarnings("deprecation")
	public DefenderKickerController() {
		// Set up multiplexer stuff
		I2Cport = SensorPort.S4;
		I2Cport.i2cEnable(I2CPort.STANDARD_MODE);
		I2Csensor = new I2CSensor(I2Cport);
		I2Csensor.setAddress(0xB4);
		I2Csensor.sendData(REGISTER_ADDRESS_SPEED, KICK_SPEED);
	}
	
	@Override
	protected void stop() {
		I2Csensor.sendData(REGISTER_ADDRESS_STATE, STOP);
	}

	@Override
	protected void performOpen() throws InterruptedException {
		I2Csensor.sendData(REGISTER_ADDRESS_SPEED, CATCH_SPEED);
		
		// Shut fully in case open
		I2Csensor.sendData(REGISTER_ADDRESS_STATE, FORWARD);
		Thread.sleep(DELAY_CLOSE);
		
		I2Csensor.sendData(REGISTER_ADDRESS_STATE, STOP);
		Thread.sleep(DELAY_STOP);

		// Open
		I2Csensor.sendData(REGISTER_ADDRESS_STATE, BACKWARD);
		Thread.sleep(DELAY_OPEN);
		I2Csensor.sendData(REGISTER_ADDRESS_STATE, STOP);
		Thread.sleep(DELAY_STOP);
	}

	@Override
	protected void performKick() throws InterruptedException {
		I2Csensor.sendData(REGISTER_ADDRESS_SPEED, KICK_SPEED);
		
		// Release ball
		I2Csensor.sendData(REGISTER_ADDRESS_STATE, BACKWARD);
		Thread.sleep(DELAY_KICK);

		I2Csensor.sendData(REGISTER_ADDRESS_STATE, STOP);
		Thread.sleep(DELAY_KICK_END);
		
		// Return to normal
		this.performOpen();
	}
	
	@Override
	protected void performGrab() throws InterruptedException {
		I2Csensor.sendData(REGISTER_ADDRESS_SPEED, CATCH_SPEED);
		I2Csensor.sendData(REGISTER_ADDRESS_STATE, FORWARD);
		Thread.sleep(DELAY_CLOSE);

		I2Csensor.sendData(REGISTER_ADDRESS_SPEED, RETAINMENT_SPEED);
		I2Csensor.sendData(REGISTER_ADDRESS_STATE, FORWARD);
		Thread.sleep(DELAY_STOP);
	}
}
