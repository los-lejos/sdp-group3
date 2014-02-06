package dice.communication;

import java.io.IOException;

/*
 * @author Joris S. Urbaitis
 */

public class BluetoothRobotCommunicator implements RobotCommunicator {

	private BluetoothRobotConnection conn;

	public void init(RobotType robot) {
		if(conn != null) {
			System.out.println("Communicator already initialized");
			return;
		}

		conn = new BluetoothRobotConnection(robot);
		
		try {
			conn.openConnection();
			conn.handshake();
			conn.start();
		} catch (BluetoothCommunicationException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	public void close() {
		if(conn != null) {
			try {
				conn.closeConnection();
			} catch (IOException e) {
				System.out.println("Error closing connection: " + e.getMessage());
			}
		}
	}

	public void sendInstruction(RobotInstruction instruction) {
		if(conn == null) {
			System.out.println("Must call init before sending instruction");
			return;
		}
		
		try {
			conn.send(instruction);
		} catch (IOException e) {
			System.out.println("Error sending instruction to " + instruction.getRobotType().toString() + ": " + e.getMessage());
		} catch (BluetoothCommunicationException e) {
			System.out.println("Error sending instruction to " + instruction.getRobotType().toString() + ": " + e.getMessage());
		}
	}
}
