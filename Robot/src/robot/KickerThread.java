package robot;

import java.io.IOException;

import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import robot.communication.BluetoothCommunicationException;
import robot.communication.BluetoothDiceConnection;
import shared.RobotInstructions;

public class KickerThread extends Thread {
	
	private final NXTMotor motor;
	private final BluetoothDiceConnection conn;
	
	private KickerState state = KickerState.READY;
	
	public KickerThread(BluetoothDiceConnection conn) {
		this.setDaemon(true);
		this.conn = conn;
		motor = new NXTMotor(MotorPort.B);
		motor.setPower(100);
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
		grab();
	}
	
	private void open() {
		System.out.println("Opening kicker...");
		
		// Shut fully in case open
		motor.forward();
		try {
			sleep(1000);
		} catch (InterruptedException e) {
			System.out.println("Kicker wait exception");
		}
		
		// Open
		motor.backward();
		try {
			sleep(85);
		} catch (InterruptedException e) {
			System.out.println("Kicker wait exception");
		}
		motor.stop();
		
		System.out.println("Kicker is ready.");
	}

	private void kick() {
		// Release ball
		motor.forward();
		try {
			sleep(100);
		} catch (InterruptedException e) {
			System.out.println("Kicker wait exception");
		}
		motor.stop();
		try {
			sleep(600);
		} catch (InterruptedException e) {
			System.out.println("Kicker wait exception");
		}
		
		// Return to default position
		motor.backward();
		try {
			sleep(100);
		} catch (InterruptedException e) {
			System.out.println("Kicker wait exception");
		}
		motor.stop();
		
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
		motor.forward();
		try {
			sleep(300);
		} catch (InterruptedException e) {
			System.out.println("Kicker wait exception");
		}
		motor.stop();
		
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
