package dice;

import dice.communication.BluetoothCommunicationException;
import dice.communication.BluetoothRobotConnection;

/*
 * @author Joris S. Urbaitis
 */

public class Main {
	public static void main (String[] args) {
		BluetoothRobotConnection conn = new BluetoothRobotConnection("OptimusPrime", "macaddr");
		try {
			conn.openConnection();
			conn.handshake();
		} catch (BluetoothCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Start the communications thread
		conn.start();
	}
}
