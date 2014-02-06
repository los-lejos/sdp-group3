package dice.communication; 

import shared.RobotInstructions;

/*
 * @author Joris S. Urbaitis
 */

public class RobotInstruction {
	
	private RobotType robotType;
	private byte[] instruction;
	private RobotCommunicationCallback callback;
	
	public static RobotInstruction CreateMoveTo(long angle, byte distance, RobotType robotType, RobotCommunicationCallback callback) {
		byte angleUpper = (byte)(angle / 10);
		byte angleLower = (byte)(angle % 10);
		
		return new RobotInstruction(RobotInstructions.MOVE_TO, angleUpper, angleLower, distance, robotType, callback);
	}
	
	public static RobotInstruction CreateShootTo(long angle, RobotType robotType, RobotCommunicationCallback callback) {
		byte angleUpper = (byte)(angle / 10);
		byte angleLower = (byte)(angle % 10);
		
		return new RobotInstruction(RobotInstructions.MOVE_TO, angleUpper, angleLower, (byte)0, robotType, callback);
	}
	
	public RobotInstruction(byte instructionType, byte param1, byte param2, byte param3, RobotType robotType, RobotCommunicationCallback callback) {
		this.robotType = robotType;
		this.callback = callback;
		
		// Position 0 in the instruction is reserved for the unique id of this instruction
		this.instruction = new byte[RobotInstructions.LENGTH];
		this.instruction[1] = instructionType;
		this.instruction[2] = param1;
		this.instruction[3] = param2;
		this.instruction[4] = param3;
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
