package dice.communication;

import java.io.IOException;

/*
 * @author Joris S. Urbaitis
 */

public class RobotCommunication {
	
	private static RobotCommunication instance = new RobotCommunication();
	
	private BluetoothRobotConnection attacker, defender;

	public static RobotCommunication getInstance() {
		return instance;
	}
	
	private RobotCommunication() {}
	
	public void init(RobotType robot) {
		if(robot == RobotType.ATTACKER) {
			attacker = new BluetoothRobotConnection(RobotType.ATTACKER);
			this.initRobotConnection(attacker);
		} else {
			defender = new BluetoothRobotConnection(RobotType.DEFENDER);
			this.initRobotConnection(defender);
		}
	}
	
	public void close() {
		if(attacker != null) {
			try {
				attacker.closeConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(defender != null) {
			try {
				defender.closeConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void initRobotConnection(BluetoothRobotConnection robot) {
		try {
			robot.openConnection();
			robot.handshake();
			robot.start();
		} catch (BluetoothCommunicationException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public void sendInstruction(RobotInstruction instruction) {
		try {
			if(instruction.getRobotType() == RobotType.ATTACKER) {
				attacker.send(instruction);
			} else {
				defender.send(instruction);
			}
		} catch (IOException e) {
			System.out.println("Error sending instruction to " + instruction.getRobotType().toString() + ": " + e.getMessage());
		} catch (BluetoothCommunicationException e) {
			System.out.println("Error sending instruction to " + instruction.getRobotType().toString() + ": " + e.getMessage());
		}
	}
}
