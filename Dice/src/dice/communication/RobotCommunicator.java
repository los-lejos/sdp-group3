package dice.communication;

public interface RobotCommunicator {
	public void init(RobotType robot);
	public void close();
	public void sendInstruction(RobotInstruction instruction);
}
