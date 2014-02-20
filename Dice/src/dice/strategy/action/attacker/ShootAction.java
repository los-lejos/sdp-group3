package dice.strategy.action.attacker;


import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.Goal;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.strategy.StratMaths;
import dice.strategy.StrategyAction;

/*
 * @author Sam Stern
 */

public class ShootAction extends StrategyAction {
	
	Goal opGoal;

	public ShootAction(RobotType targetRobot) {
		super(targetRobot);
	}
	
	@Override
	public String getActionType(){
		return "ShootAction";
	}
	
	
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
		opGoal = state.getOppGoal();
		Vector2 aimTarget = StratMaths.relativePos(this.getTargetObject(state), opGoal.getGoalCenter());
		return RobotInstruction.CreateShootTo(
				StratMaths.cartesianToPolarTheta(aimTarget));
		}
	}
