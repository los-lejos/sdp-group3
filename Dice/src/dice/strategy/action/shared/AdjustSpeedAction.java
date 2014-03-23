package dice.strategy.action.shared;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Line;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.strategy.StrategyAction;

public class AdjustSpeedAction extends StrategyAction {
	
	private long lastExecution = 0;
	
	private byte currentSpeedPercentage = 100;

	public AdjustSpeedAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		if(System.currentTimeMillis() - lastExecution < 3000) {
			return false;
		}

		GameObject robot = getTargetObject(state);
		Vector2 robotPos = robot.getPos();
		Line top = state.getTopLine();
		Line bottom = state.getBottomLine();

		double topYDist = Math.abs(robotPos.Y - top.getYValue(robotPos.X));
		double bottomYDist = Math.abs(robotPos.Y - bottom.getYValue(robotPos.X));
		
		double minDist = Math.min(topYDist, bottomYDist);
		
		if(robot == state.getOurDefender()) {
			double xDist = Math.abs(robotPos.X - state.getOurGoal().getBottomPost().X);
			minDist = Math.min(minDist, xDist);
		}
		
		byte speedPercentage = 100;
		if(minDist < 60) {
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
		System.out.println("Adjusting speed to " + this.currentSpeedPercentage);
		lastExecution = System.currentTimeMillis();
		return RobotInstruction.createSetSpeed(this.currentSpeedPercentage);
	}
}
