package tests.strategy;

import dice.communication.RobotCommunicator;
import dice.communication.RobotInstruction;
import dice.communication.RobotType;

public class MockRobotCommunicator implements RobotCommunicator {

	private RobotType robotType;
	
	@Override
	public void init(RobotType robot) {
		this.robotType = robot;
	}

	@Override
	public void close() {
	}

	@Override
	public void sendInstruction(RobotInstruction instruction) {
		System.out.println("Sending instruction " + instruction.toString() + " to " + robotType.toString());
	}
	
}
