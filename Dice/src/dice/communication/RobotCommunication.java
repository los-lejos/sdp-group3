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
	
	public void init() {
		attacker = new BluetoothRobotConnection(RobotType.ATTACKER);
		this.initRobotConnection(attacker);
		
		//defender = new BluetoothRobotConnection(RobotType.DEFENDER);
		//this.initRobotConnection(defender);
		
		//this.sendInstruction(new RobotInstruction())
	}
	
	public void close() {
		attacker.closeConnection();
		defender.closeConnection();
	}
	
	private void initRobotConnection(BluetoothRobotConnection robot) {
		try {
			robot.openConnection();
			robot.handshake();
			robot.start();
		} catch (BluetoothCommunicationException e) {
			e.printStackTrace();
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
			e.printStackTrace();
		} catch (BluetoothCommunicationException e) {
			e.printStackTrace();
		}
	}
}
