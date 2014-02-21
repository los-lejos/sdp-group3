package dice.strategy.action.attacker;



import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.BoundedLine;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.state.WorldState.PitchZone;
import dice.strategy.StratMaths;
import dice.strategy.StrategyAction;

/*
 * @author Sam Stern
 * 
 * intercept opponent's pass
 * determine where the ball is headed and move there
 * 
 * Attackers equivalant to SaveAction
 */

public class InterceptAction extends StrategyAction {
	
	Vector2 whereToIntercept;
	BoundedLine ballTrajecory;
	protected double criticalVel; //should be the same as in BlockAction
	
	
	public InterceptAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		Vector2 ballVel = state.getBall().getVelocity();
		PitchZone ballZone = state.getBall().getCurrentZone();
		
		boolean hasLargeNegVel = ballVel.X > -Math.abs(criticalVel);
		boolean canReach = (ballZone == WorldState.PitchZone.OPP_DEFEND_ZONE) || (ballZone == WorldState.PitchZone.OUR_ATTACK_ZONE);
		
		return hasLargeNegVel && canReach;
	
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
