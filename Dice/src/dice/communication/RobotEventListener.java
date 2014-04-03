package dice.communication;

public interface RobotEventListener {
	public void onBallCaught();
	public void onBallReleased();
	
	public void onStrafeStart();
	public void onStrafeEnd();
}
