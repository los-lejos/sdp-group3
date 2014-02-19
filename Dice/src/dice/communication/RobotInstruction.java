package dice.communication; 

import shared.RobotInstructions;

/*
 * @author Joris S. Urbaitis
 */

public class RobotInstruction {

	private byte[] instruction;
	private RobotCommunicationCallback callback;
	

	public static RobotInstruction CreateMoveTo(long angle, byte distance) {
		byte angleUpper = (byte)(angle / 10);
		byte angleLower = (byte)(angle % 10);

		return new RobotInstruction(RobotInstructions.MOVE_TO, angleUpper, angleLower, distance);
	}
	
	public static RobotInstruction CreateLateralMoveTo(byte distance) {
		return new RobotInstruction(RobotInstructions.LAT_MOVE_TO, distance, (byte) 0, (byte) 0);
	}
	
	public static RobotInstruction CreateShootTo(long angle) {
		byte angleUpper = (byte)(angle / 10);
		byte angleLower = (byte)(angle % 10);
		
		return new RobotInstruction(RobotInstructions.MOVE_TO, angleUpper, angleLower, (byte)0);
	}
	
	public RobotInstruction(byte instructionType, byte param1, byte param2, byte param3) {
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
	
	public void setCallback(RobotCommunicationCallback callback) {
		this.callback = callback;
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
