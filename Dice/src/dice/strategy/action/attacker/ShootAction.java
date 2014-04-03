package dice.strategy.action.attacker;


import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Goal;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.state.WorldState.Side;
import dice.strategy.StratMaths;
import dice.strategy.StrategyAction;

/*
 * @author Sam Stern
 */

public class ShootAction extends StrategyAction {

	public ShootAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		return state.getObjectWithBall() == this.getTargetObject(state);
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		GameObject target = this.getTargetObject(state);
		Vector2 targetPos = target.getPos();
		
		GameObject defender = state.getOpponentDefender();
		Vector2 defenderPos = defender.getPos();
		Goal oppGoal = state.getOppGoal();

		// If defender is blocking or we are not in front of the goal,
		// strafe to the side of the goal that is larger
		if(oppGoal.getBottomPost().Y > targetPos.Y || oppGoal.getTopPost().Y < targetPos.Y ||
			Math.abs(targetPos.Y - defenderPos.Y) <= StratMaths.Y_POS_THRESH) {

			// Find which side of the goal has a larger opening
			double targetY;
			if(defenderPos.Y > WorldState.PITCH_HEIGHT / 2.0) {
				targetY = (defenderPos.Y - oppGoal.getBottomPost().Y) / 2.0 + oppGoal.getBottomPost().Y;
			} else {
				targetY = (oppGoal.getTopPost().Y - defenderPos.Y) / 2.0 + defenderPos.Y;
			}

			double dist = StratMaths.getStrafeDist(targetPos.Y, targetY, state.getSide() == Side.RIGHT);
			System.out.println("Strafe " + dist);
			return RobotInstruction.createLateralMove(dist);
		}
		
		double xDist;
		if(state.getSide() == Side.LEFT) {
			xDist = state.getThirdDelimiter() - targetPos.X;
		} else {
			xDist = targetPos.X - state.getFirstDelimiter();
		}
		
		xDist -= StratMaths.BALL_DISTANCE_THRESH;
		return RobotInstruction.createMoveAndKick(xDist);
	}
}
