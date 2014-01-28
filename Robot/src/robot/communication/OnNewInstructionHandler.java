package robot.communication;

/*
 * @author Joris S. Urbaitis
 */

public interface OnNewInstructionHandler {
	public void onNewInstruction(IssuedInstruction instruction);
}
