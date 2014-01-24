package dice.communication;

/*
 * @author Joris S. Urbaitis
 */

public class RobotCommunication {
	
	private static RobotCommunication instance;
	
	private BluetoothRobotConnection attacker, defender;
	
	public static RobotCommunication getInstance() {
		return instance;
	}

	public void sendInstruction(RobotInstruction instruction) {
		if(instruction.getRobotType() == RobotType.ATTACKER) {
			
		} else {
			
		}
	}
}
