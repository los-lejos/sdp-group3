package robot;

import java.io.IOException;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.SensorPort;

import robot.communication.BluetoothCommunicationException;
import robot.communication.BluetoothDiceConnection;
import shared.RobotInstructions;

public class DefenceKickerThread extends Thread {
	
	private I2CPort I2Cport;
	private I2CSensor I2Csensor;
	
	private static final byte FORWARD = (byte) 1;
	private static final byte BACKWARD = (byte) 2;
	//private static final byte FORWARD = (byte) 2;
	//private static final byte BACKWARD = (byte) 1;
	
	private static final byte STOP = (byte) 0;
	private static final byte KICK_SPEED = (byte) 200;
	private static final byte CATCH_SPEED = (byte) 120;
	
	private final BluetoothDiceConnection conn;
	
	private KickerState state = KickerState.READY;
	
	@SuppressWarnings("deprecation")
	public DefenceKickerThread(BluetoothDiceConnection conn) {
		this.setDaemon(true);
		this.conn = conn;
		
		// Set up multiplexer stuff
		I2Cport = SensorPort.S4;
		I2Cport.i2cEnable(I2CPort.STANDARD_MODE);
		I2Csensor = new I2CSensor(I2Cport);
		I2Csensor.setAddress(0xB4);
		//I2Csensor.setAddress(0x5A);
	}
	
	@Override
	public void run() {
		open();
		while (state != KickerState.EXIT) {
			if (state == KickerState.KICK) {
				kick();
				state = KickerState.READY;
			} else if (state == KickerState.GRAB) {
				grab();
				state = KickerState.READY;
			}
		}
		//grab();
	}
	
	private void open() {
		// Shut fully in case open
		System.out.println(I2Csensor.sendData(0x01, FORWARD));
		System.out.println(I2Csensor.sendData(0x02, KICK_SPEED));
		try {
			sleep(1000);
		} catch (InterruptedException e) {
			System.out.println("Kicker wait exception");
		}
		I2Csensor.sendData(0x01, STOP);
		
		// Open
		I2Csensor.sendData(0x01, BACKWARD);
		try {
			sleep(50);
		} catch (InterruptedException e) {
			System.out.println("Kicker wait exception");
		}
		I2Csensor.sendData(0x01, STOP);
	}

	private void kick() {
		// Release ball
		I2Csensor.sendData(0x02, KICK_SPEED);
		I2Csensor.sendData(0x01, BACKWARD);
		try {
			sleep(200);
		} catch (InterruptedException e) {
			System.out.println("Kicker wait exception");
		}
		I2Csensor.sendData(0x01, STOP);
		
		// Tell DICE we don't have the ball anymore.
		byte[] hasBallResponse = {RobotInstructions.RELEASED_BALL, 0, 0, 0};
		
		try {
			conn.send(hasBallResponse);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BluetoothCommunicationException e) {
			e.printStackTrace();
		}
	}
	
	private void grab() {
		System.out.println("Grabbed.");
		I2Csensor.sendData(0x02, CATCH_SPEED);
		I2Csensor.sendData(0x01, FORWARD);
		try {
			sleep(200);
		} catch (InterruptedException e) {
			System.out.println("Kicker wait exception");
		}
		I2Csensor.sendData(0x01, STOP);
		
		// Notify DICE that we have the ball
		byte[] hasBallResponse = {RobotInstructions.CAUGHT_BALL, 0, 0, 0};
		
		try {
			conn.send(hasBallResponse);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BluetoothCommunicationException e) {
			e.printStackTrace();
		}
	}
	
	public void setKickerState(KickerState state) {
		this.state = state;
	}

}
