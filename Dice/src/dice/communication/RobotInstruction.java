package dice.communication; 

import shared.RobotInstructions;

/*
 * @author Joris S. Urbaitis
 */

public class RobotInstruction {

	private byte[] instruction;
	private RobotCommunicationCallback callback;
	
	private static byte strategyToRobotDistance(double distance) {
		// The pitch is 237 x 114 cm
		// The vision coordinate system is 580 x 320
		final double ratio = 237.0 / 580.0;
		return (byte)(distance * ratio);
	}
	
	private static byte[] angleToBytes(double angle) {
		assert (angle >= 0) && (angle <= 360);
		byte[] byteBytes = new byte[2];
		angle = Math.round(angle);
		// multiply by -1, because the robot code
		// works with positive anticlockwise values.
		// we need to discuss this.
		angle *= -1;
		byteBytes[0] = (byte)(angle / 10);
		byteBytes[1] = (byte)(angle % 10);
		return byteBytes;
	}
	
	public static RobotInstruction createMoveTo(double angle, double distance) {
		byte[] angleBytes = angleToBytes(angle);
		byte robotDistance = strategyToRobotDistance(distance);
		return new RobotInstruction(RobotInstructions.MOVE_TO, angleBytes[0], angleBytes[1], robotDistance);
	}
	
	public static RobotInstruction createLateralMoveTo(double distance) {
		byte robotDistance = strategyToRobotDistance(distance);
		return new RobotInstruction(RobotInstructions.LAT_MOVE_TO, robotDistance, (byte) 0, (byte) 0);
	}
	
	public static RobotInstruction createShootTo(double angle) {
		byte[] angleBytes = angleToBytes(angle);
		return new RobotInstruction(RobotInstructions.KICK_TOWARD, angleBytes[0], angleBytes[1], (byte)0);
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
