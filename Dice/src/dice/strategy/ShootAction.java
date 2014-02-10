package dice.strategy;


import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.Goal;
import dice.state.Vector2;
import dice.state.WorldState;

/*
 * @author Sam Stern
 */

public class ShootAction extends StrategyAction {
	
	public ShootAction(RobotType targetRobot) {
		super(targetRobot);
	}

	Goal opGoal; //TODO set opponents goal
	
	@Override
	public boolean isPossible(WorldState state) {
		if (state.getBallPossession() == WorldState.BallPossession.OUR_ATTACKER) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected int calculateUtility(WorldState state) {
		return 2;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		Vector2 aimTarget = StratMaths.relativePos(this.getTargetObject(state), opGoal.getGoalCenter());
		return RobotInstruction.CreateShootTo(
				StratMaths.cartesianToPolarTheta(aimTarget));
		}
	}
