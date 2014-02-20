package dice.strategy.action.defender;


import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.Goal;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.state.WorldState.PitchZone;
import dice.strategy.StratMaths;
import dice.strategy.StrategyAction;

/*
 * @author Sam Stern
 */
public class ToGoalCenterAction extends StrategyAction {

	Goal ourGoal; // TODO set our Goal
	Vector2 ourGoalCenter = ourGoal.getGoalCenter();
	
	public ToGoalCenterAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		return true;
	}

	@Override
	protected int calculateUtility(WorldState state) {
		PitchZone ballZone = state.getBall().getCurrentZone();
		
		if ((ballZone == WorldState.PitchZone.OPP_DEFEND_ZONE) || 
			(ballZone == WorldState.PitchZone.OUR_ATTACK_ZONE)) {
			return 2;
		} else {
			return 1;
		}
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		return RobotInstruction.CreateMoveTo(
				StratMaths.cartesianToPolarTheta(ourGoalCenter), 
				StratMaths.cartestanToPolarR(ourGoalCenter));
	}

}
