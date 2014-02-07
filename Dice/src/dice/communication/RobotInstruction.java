package dice.communication; 

import shared.RobotInstructions;

/*
 * @author Joris S. Urbaitis
 */

public class RobotInstruction {

	private byte[] instruction;
	private RobotCommunicationCallback callback;
	
	public RobotInstruction(byte instructionType, byte param1, byte param2, byte param3, RobotCommunicationCallback callback) {
		this.callback = callback;
		
		// Position 0 in the instruction is reserved for the unique id of this instruction
		this.instruction = new byte[RobotInstructions.LENGTH];
		this.instruction[1] = instructionType;
		this.instruction[2] = param1;
		this.instruction[3] = param2;
		this.instruction[4] = param3;
	}

	public byte[] getInstruction() {
		return instruction;
	}
	
	public RobotCommunicationCallback getCallback() {
		return callback;
	}
	
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < this.instruction.length; i++) {
		   result.append(this.instruction[i]);
		}
		
		return result.toString();
	}
}
