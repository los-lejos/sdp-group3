package dice.communication;

import java.io.IOException;

import dice.Log;

/*
 * @author Joris S. Urbaitis
 */

public class BluetoothRobotCommunicator implements RobotCommunicator {

	private BluetoothRobotConnection conn;
	private RobotType robotType;

	public void init(RobotType robot, RobotEventListener eventListener) {
		this.robotType = robot;
		
		if(conn != null) {
			Log.logError("Communicator already initialized");
			return;
		}

		conn = new BluetoothRobotConnection(robot, eventListener);
		
		try {
			conn.openConnection();
			conn.handshake();
			conn.start();
		} catch (BluetoothCommunicationException e) {
			Log.logException(e);
		}
	}
	
	public void close() {
		if(conn != null) {
			try {
				conn.closeConnection();
			} catch (IOException e) {
				Log.logError("Error closing connection: " + e.getMessage());
			}
		}
	}

	public void sendInstruction(RobotInstruction instruction) {
		if(conn == null) {
			Log.logError("Must call init before sending instruction");
			return;
		}
		
		try {
			Log.logInfo("Sending isntruction to " + this.robotType.toString());
			conn.send(instruction);
		} catch (IOException e) {
			Log.logError("Error sending instruction to " + this.robotType.toString() + ": " + e.getMessage());
		} catch (BluetoothCommunicationException e) {
			Log.logError("Error sending instruction to " + this.robotType.toString() + ": " + e.getMessage());
		}
	}
}