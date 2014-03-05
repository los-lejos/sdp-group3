package dice.communication;

public interface RobotCommunicator {
	public void init(RobotType robot, RobotEventListener eventListener);
	public void close();
	public void sendInstruction(RobotInstruction instruction);
	public boolean isConnected();
}
