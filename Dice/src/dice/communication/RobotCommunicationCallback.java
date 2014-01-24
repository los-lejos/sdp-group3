package dice.communication;

/*
 * @author Joris S. Urbaitis
 */

public interface RobotCommunicationCallback {
	public void onError();
	public void onTimeout();
	public void onDone();
}
