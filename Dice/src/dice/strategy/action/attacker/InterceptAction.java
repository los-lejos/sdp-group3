package dice.strategy.action.attacker;



import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.BoundedLine;
import dice.state.UnboundedLine;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.strategy.StratMaths;
import dice.strategy.StrategyAction;

/*
 * @author Sam Stern
 * 
 * intercept opponent's pass
 * determine where the ball is headed and move there
 * 
 * Attackers equivilant to SaveAction
 */

public class InterceptAction extends StrategyAction {
	
	Vector2 whereToIntercept;
	BoundedLine ballTraj;
	protected double criticalVel; //sould be the same as in BlockAction
	
	
	public InterceptAction(RobotType targetRobot) {
		super(targetRobot);
	}
	
	@Override
	public String getActionType(){
		return "InterceptAction";
	}

	@Override
	public boolean isPossible(WorldState state) {
		Vector2 ballVel = state.getBall().getVelocity();
		boolean hasLargeNegVel = (ballVel.X < 0) && ballVel.X > -Math.abs(criticalVel);
		boolean canReach = (getTargetObject(state) == state.getOurAttacker()) && 
				((state.getBallZone() == WorldState.PitchZone.OPP_DEFEND_ZONE) || (state.getBallZone() == WorldState.PitchZone.OUR_ATTACK_ZONE));
		
		if (hasLargeNegVel && canReach) {
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
		return RobotInstruction.CreateMoveTo(
				StratMaths.cartesianToPolarTheta(whereToIntercept),
				StratMaths.cartestanToPolarR(whereToIntercept));
	}

}
