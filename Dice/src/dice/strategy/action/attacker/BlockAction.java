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

		// Try to navigate towards the center of the zone while blocking the ball, avoiding
		// facing the wall so that we don't get stuck trying to catch the ball
		//Vector2 vertical = zoneCenter;
		Vector2 vertical;
		if(robotPos.Y < zoneCenter.Y) {
			vertical = new Vector2(robotPos.X, robotPos.Y + 1);
		} else {
			vertical = new Vector2(robotPos.X, robotPos.Y - 1);
		}
		
		double heading = robot.getRotationRelativeTo(vertical);
		boolean shouldRotate = Math.abs(heading) > StratMaths.getRotationTreshold(robotPos, vertical);

		if(shouldRotate) {
			return RobotInstruction.createRotate(heading, 100);
		} else {
			double distToBallY = robotPos.Y - ball.getPos().Y;
			if(robotPos.Y < zoneCenter.Y) {
				distToBallY = -distToBallY;
			}
			
			return RobotInstruction.createMove(distToBallY, 100);
		}
	}

}
