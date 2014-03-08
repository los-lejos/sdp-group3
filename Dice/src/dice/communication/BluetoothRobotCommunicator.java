package dice.communication;

import java.io.IOException;

import dice.Log;

/*
 * @author Joris S. Urbaitis
 */

public class BluetoothRobotCommunicator implements RobotCommunicator {

	private BluetoothRobotConnection conn;
	private RobotType robotType;

	@Override
	public void init(RobotType robot, RobotEventListener eventListener) {
		this.robotType = robot;
		
		if(conn != null) {
			try {
				conn.closeConnection();
			} catch (IOException e) {
				Log.logError("Error while closing connection");
			}
		}

		conn = new BluetoothRobotConnection(robot, eventListener);
		
		try {
			conn.openConnection();
			conn.start();
		} catch (BluetoothCommunicationException e) {
			Log.logException(e);
			conn = null;
		}
	}
	
	@Override
	public void close() {
		if(conn != null) {
			try {
				conn.closeConnection();
			} catch (IOException e) {
				Log.logError("Error closing connection: " + e.getMessage());
			}
		}
	}
	
	@Override
	public boolean isConnected() {
		return conn != null;
	}

	@Override
	public void sendInstruction(RobotInstruction instruction) {
		if(conn == null) {
			Log.logError("Must call init before sending instruction");
			return;
		}
		
		try {
			Log.logInfo("Sending instruction to " + this.robotType.toString());
			conn.send(instruction);
		} catch (IOException e) {
			Log.logError("Error sending instruction to " + this.robotType.toString() + ": " + e.getMessage());
		} catch (BluetoothCommunicationException e) {
			Log.logError("Error sending instruction to " + this.robotType.toString() + ": " + e.getMessage());
		}
	}
}
