package dice;

import java.io.IOException;

import dice.communication.BluetoothCommunicationException;
import dice.communication.BluetoothRobotConnection;
import dice.communication.RobotType;

/*
 * @author Joris S. Urbaitis
 */

public class Main {
	public static void main (String[] args) {
		BluetoothRobotConnection conn = new BluetoothRobotConnection(RobotType.ATTACKER);
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
