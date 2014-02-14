package robot.communication;

import java.util.Arrays;

import shared.RobotInstructions;

/*
 * @author Joris S. Urbaitis
 */

public class IssuedInstruction {

	private byte type;
	private byte[] response;
	private byte param1;
	private byte param2;
	private byte param3;
	
	public IssuedInstruction(byte[] instruction) {
		this.response = instruction;
		this.type = instruction[1];
		this.param1 = instruction[2];
		this.param2 = instruction[3];
		this.param3 = instruction[4];
	}
	
	public byte[] getCompletedResponse() {
		return response;
	}
	
	public byte getType() {
		return this.type;
	}
	
	public byte[] getParameters() {
		byte[] parameters = {this.param1, this.param2, this.param3};
		if (type == RobotInstructions.MOVE_TO) {
			return parameters;
		} else if (type == RobotInstructions.KICK_TOWARD) {
			byte[] truncParams = Arrays.copyOfRange(parameters, 0, 2);
			return truncParams;
		} else if (type == RobotInstructions.LAT_MOVE_TO) {
			System.out.println("Moving laterally.");
			return Arrays.copyOfRange(parameters, 0, 1);
			
		} else {
			System.out.println("Bad instruction.");
			return parameters;
		
			
		}
	}
	
}
