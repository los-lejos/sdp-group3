package dice;

import java.io.IOException;

import dice.communication.BluetoothCommunicationException;
import dice.communication.BluetoothRobotConnection;

/*
 * @author Joris S. Urbaitis
 */

public class Main {
	public static void main (String[] args) {
		BluetoothRobotConnection conn = new BluetoothRobotConnection("OptimusPrime", "0016530A553F");
		//BluetoothRobotConnection conn = new BluetoothRobotConnection("Ball-E", "0016530A5C22");
		try {
			System.out.println("Opening Bluetooth connection");
			conn.openConnection();
			System.out.println("Performing handshake");
			conn.handshake();
		} catch (BluetoothCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		conn.closeConnection();
		
		// Start the communications thread
		//conn.start();
	}
}
