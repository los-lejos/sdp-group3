package dice.communication; 

import java.util.Arrays;

import dice.state.WorldState;

import shared.RobotInstructions;

/*
 * @author Joris S. Urbaitis
 */

public class RobotInstruction {

	private byte[] instruction;

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
	
	private static byte boundSpeed(int speedPercent) {
		if(speedPercent > 100) {
			speedPercent = 100;
		} else if(speedPercent < 1) {
			speedPercent = 1;
		}
		
		return (byte)speedPercent;
	}
	
	public static RobotInstruction createStrafeAndMoveAndKick(double moveDist, double strafeDist) {
		byte dist = strategyToRobotDistance(moveDist);
		
		if(Math.abs(strafeDist) > WorldState.PITCH_HEIGHT) {
			strafeDist = strafeDist > 0 ? WorldState.PITCH_HEIGHT : -WorldState.PITCH_HEIGHT;
		}
		
		byte strafe = strategyToRobotDistance(strafeDist);
		return new RobotInstruction(RobotInstructions.STRAFE_AND_MOVE_AND_KICK, dist, strafe, (byte)0);
	}
	
	public static RobotInstruction createMoveAndKick(double dist) {
		byte robotDistance = strategyToRobotDistance(dist);
		return new RobotInstruction(RobotInstructions.MOVE_AND_KICK, robotDistance, (byte)100, (byte)0);
	}
	
	public static RobotInstruction createOpenKicker() {
		return new RobotInstruction(RobotInstructions.OPEN_KICKER, (byte)0, (byte)0, (byte)0);
	}
	
	public static RobotInstruction createCloseKicker() {
		return new RobotInstruction(RobotInstructions.CLOSE_KICKER, (byte)0, (byte)0, (byte)0);
	}
	
	public static RobotInstruction createMove(double distance, int speedPercent) {
		byte robotDistance = strategyToRobotDistance(distance);
		byte speed = boundSpeed(speedPercent);
		return new RobotInstruction(RobotInstructions.MOVE, robotDistance, speed, (byte)0);
	}
	
	public static RobotInstruction createRotate(double angle, int speedPercent) {
		angle = Math.toDegrees(angle);
		byte[] angleBytes = angleToBytes(angle);
		byte speed = boundSpeed(speedPercent);
		return new RobotInstruction(RobotInstructions.ROTATE, angleBytes[0], angleBytes[1], speed);
	}
	
	public static RobotInstruction createLateralMove(double distance) {
		if(Math.abs(distance) > WorldState.PITCH_HEIGHT) {
			distance = distance > 0 ? WorldState.PITCH_HEIGHT : -WorldState.PITCH_HEIGHT;
		}
		
		byte robotDistance = strategyToRobotDistance(distance);
		return new RobotInstruction(RobotInstructions.LAT_MOVE, robotDistance, (byte) 0, (byte)0);
	}
	
	public static RobotInstruction createKick() {
		return new RobotInstruction(RobotInstructions.KICK, (byte)0, (byte)0, (byte)0);
	}
	
	public static RobotInstruction createSetSpeed(byte speedPercentage) {
		return new RobotInstruction(RobotInstructions.SET_TRAVEL_SPEED, speedPercentage, (byte)0, (byte)0);
	}
	
	public RobotInstruction(byte instructionType, byte param1, byte param2, byte param3) {
		this.instruction = new byte[RobotInstructions.LENGTH];
		this.instruction[0] = instructionType;
		this.instruction[1] = param1;
		this.instruction[2] = param2;
		this.instruction[3] = param3;
	}
	
	public byte getType() {
		return this.instruction[0];
	}

	public byte[] getInstruction() {
		return instruction;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < this.instruction.length; i++) {
		   result.append(this.instruction[i]);
		}
		
		return result.toString();
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof RobotInstruction) {
			RobotInstruction i2 = (RobotInstruction)other;
			return Arrays.equals(i2.getInstruction(), this.getInstruction());
		}
		
		return false;
	}
}
