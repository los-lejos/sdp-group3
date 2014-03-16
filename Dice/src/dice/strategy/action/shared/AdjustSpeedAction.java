package dice.strategy.action.shared;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.strategy.StrategyAction;

public class AdjustSpeedAction extends StrategyAction {
	
	private byte currentSpeedPercentage = 100;

	public AdjustSpeedAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		GameObject robot = getTargetObject(state);
		Vector2 robotPos = robot.getPos();
		
		if(robotPos == null) { return false; }
		
		double topYDist = Math.abs(robotPos.Y - state.getTopLine().getYValue(robotPos.X));
		double bottomYDist = Math.abs(robotPos.Y - state.getBottomLine().getYValue(robotPos.X));
		
		double minDist = Math.min(topYDist, bottomYDist);
		
		if(robot == state.getOurDefender()) {
			double xDist = Math.abs(robotPos.X - state.getOurGoal().getBottomPost().X);
			minDist = Math.min(minDist, xDist);
		}
		
		byte speedPercentage = 100;
		if(minDist < 30) {
			speedPercentage = 30;
		}
		
		return speedPercentage != this.currentSpeedPercentage;
	}

	@Override
	protected int calculateUtility(WorldState state) {
		// Always want to be adjusting if we want to set to a new speed
		return 10;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		return RobotInstruction.createSetSpeed(this.currentSpeedPercentage);
	}
}
