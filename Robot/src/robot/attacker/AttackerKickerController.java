package robot.attacker;

import robot.KickerController;
import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.SensorPort;

public class AttackerKickerController extends KickerController {

	private static final int REGISTER_ADDRESS_STATE = 0x01;
	private static final int REGISTER_ADDRESS_SPEED = 0x02;
	
	private static final byte FORWARD = (byte) 1;
	private static final byte BACKWARD = (byte) 2;
	private static final byte STOP = (byte) 0;
	
	private static final byte RETAINMENT_SPEED = (byte)30;
	private static final byte KICK_SPEED = (byte) 255;
	private static final byte CATCH_SPEED = (byte) 100;
	
	private static final int DELAY_OPEN = 125;
	private static final int DELAY_KICK = 100;
	private static final int DELAY_CLOSE = 450;
	private static final int DELAY_CLEANUP = 45;
	
	private I2CPort I2Cport;
	private I2CSensor I2Csensor;

	@SuppressWarnings("deprecation")
	public AttackerKickerController() {
		// Set up multiplexer stuff
		I2Cport = SensorPort.S4;
		I2Cport.i2cEnable(I2CPort.STANDARD_MODE);
		I2Csensor = new I2CSensor(I2Cport);
		I2Csensor.setAddress(0xB4);
		I2Csensor.sendData(REGISTER_ADDRESS_SPEED, CATCH_SPEED);
	}

	@Override
	protected void performOpen() throws InterruptedException {
		System.out.println("OPEN");
		I2Csensor.sendData(REGISTER_ADDRESS_SPEED, CATCH_SPEED);
		
		// Shut fully in case open
		I2Csensor.sendData(REGISTER_ADDRESS_STATE, FORWARD);
		Thread.sleep(DELAY_CLOSE);
		I2Csensor.sendData(REGISTER_ADDRESS_STATE, STOP);
		
		// Open
		I2Csensor.sendData(REGISTER_ADDRESS_STATE, BACKWARD);
		Thread.sleep(DELAY_OPEN);

		I2Csensor.sendData(REGISTER_ADDRESS_STATE, STOP);
	}

	@Override
	protected void performKick() throws InterruptedException {
		System.out.println("KICK");
		// GOGOGO
		I2Csensor.sendData(REGISTER_ADDRESS_SPEED, KICK_SPEED);
		
		// Release ball
		I2Csensor.sendData(REGISTER_ADDRESS_STATE, BACKWARD);
		Thread.sleep(DELAY_KICK);
		I2Csensor.sendData(REGISTER_ADDRESS_STATE, STOP);

		performCleanup();
		
		// Hack: don't ask
		performCleanup();
		
	}
	
	@Override
	protected void performGrab() throws InterruptedException {
		System.out.println("GRAB");
		// Reduce speed
		I2Csensor.sendData(REGISTER_ADDRESS_SPEED, CATCH_SPEED);
		
		// Close around ball
		I2Csensor.sendData(REGISTER_ADDRESS_STATE, FORWARD);
		Thread.sleep(DELAY_CLOSE);

		// Keep gripping that ball
		I2Csensor.sendData(REGISTER_ADDRESS_SPEED, RETAINMENT_SPEED);
		I2Csensor.sendData(REGISTER_ADDRESS_STATE, FORWARD);
	}
	
	@Override
	protected void performCleanup() throws InterruptedException {
		System.out.println("CLEANUP");
		// Kicker in chillaxed mode
		I2Csensor.sendData(REGISTER_ADDRESS_SPEED, CATCH_SPEED);
		
		// Close fully
		I2Csensor.sendData(REGISTER_ADDRESS_STATE, FORWARD);
		Thread.sleep(DELAY_CLOSE);
		
		// Return to good "closed" position
		I2Csensor.sendData(REGISTER_ADDRESS_STATE, BACKWARD);
		Thread.sleep(DELAY_CLEANUP);
		
		I2Csensor.sendData(REGISTER_ADDRESS_STATE, STOP);
	}
}
