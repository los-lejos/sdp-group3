package dice.strategy.action.attacker;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.strategy.StratMaths;
import dice.strategy.StrategyAction;

/*
 * @author Sam Stern
 * 
 * target robot moves between ball position and goal
 */

public class BlockAction extends StrategyAction {

	public BlockAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		boolean ballInDifferentZone = state.getBall().getCurrentZone() != this.getTargetObject(state).getCurrentZone();
		return state.getBall().getPos() != null && ballInDifferentZone;
	}

	@Override
	protected int calculateUtility(WorldState state) {
		return 1;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		GameObject ball = state.getBall();
		GameObject robot = getTargetObject(state);
		Vector2 robotPos = robot.getPos();
		Vector2 zoneCenter = state.getCellCenter(robot.getCurrentZone());

		// Try to navigate towards the center of the zone while blocking the ball
		Vector2 vertical = new Vector2(zoneCenter.X, robotPos.Y - 1);
		double heading = robot.getRotationRelativeTo(vertical);
		boolean shouldRotate = Math.abs(heading) > StratMaths.getRotationTreshold(robotPos, vertical);
		
		if(shouldRotate) {
			return RobotInstruction.createRotate(heading);
		} else {
			double distToBallY = robotPos.Y - ball.getPos().Y;
			return RobotInstruction.createMove(distToBallY);
		}
	}

}
