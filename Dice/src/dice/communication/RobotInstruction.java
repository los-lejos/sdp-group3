package dice.communication; 

import shared.RobotInstructions;

/*
 * @author Joris S. Urbaitis
 */

public class RobotInstruction {
	
	private RobotType robotType;
	private byte[] instruction;
	private RobotCommunicationCallback callback;
	
	public RobotInstruction(byte instructionType, byte param1, byte param2, RobotType robotType, RobotCommunicationCallback callback) {
		this.robotType = robotType;
		this.callback = callback;
		
		// Position 0 in the instruction is reserved for the unique id of this instruction
		this.instruction = new byte[RobotInstructions.LENGTH];
		this.instruction[1] = instructionType;
		this.instruction[2] = param1;
		this.instruction[3] = param2;
	}
	
	public RobotType getRobotType() {
		return robotType;
	}
	
	public byte[] getInstruction() {
		return instruction;
	}
	
	public RobotCommunicationCallback getCallback() {
		return callback;
	}
}
