package robot.communication;

import shared.RobotInstructions;

/*
 * @author Joris S. Urbaitis
 */

public class IssuedInstruction {

	private byte[] instruction;
	
	public IssuedInstruction(byte[] instruction) {
		this.instruction = instruction;
	}

	public byte getType() {
		return this.instruction[0];
	}
	
	public int[] getParameters() {
		int type = this.getType();
		int distance;
		
		switch(type) {
		case RobotInstructions.MOVE:
			distance = instruction[1];
			return new int[] { distance };
		case RobotInstructions.ROTATE:
			int heading = extractInt(instruction[1], instruction[2]);
			return new int[] { heading };
		case RobotInstructions.KICK:
			return new int[] { };
		case RobotInstructions.LAT_MOVE:
			distance = instruction[1];
			return new int[] { distance };
		case RobotInstructions.SET_TRACK_WIDTH:
			int mm = extractInt(instruction[1], instruction[2]);
			return new int[] { mm };
		case RobotInstructions.SET_TRAVEL_SPEED:
			return new int[] { instruction[1] };
		case RobotInstructions.SET_ROTATE_SPEED:
			return new int[] { instruction[1] };
		default: 
			System.out.println("Unknown instruction: " + type);
			return null;
		}
	}
	
	private static int extractInt(byte high, byte low) {
    	return  (10 * high) + low;
    }
}
