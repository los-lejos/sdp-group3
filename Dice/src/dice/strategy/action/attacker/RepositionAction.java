package dice.strategy.action.attacker;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.*;
import dice.strategy.StratMaths;
import dice.strategy.StrategyAction;

/**
 * Repositions attacker if it has a ball but shooting it
 * would cause bad things to happen
 * 
 * @author Andrew Johnston
 */

public class RepositionAction extends StrategyAction {

	private static int MAX_ATTEMPTS;
	
	private int attempts;
	
	public RepositionAction(RobotType targetRobot) {
		super(targetRobot);
		
		attempts = 0;
	}

	@Override
	public boolean isPossible(WorldState state) {
		// only if the attacker has the ball
		if (attempts <= MAX_ATTEMPTS) {
			return (state.getObjectWithBall() == state.getOurAttacker());
		} else {
			return false;
		}
    }

	@Override
	protected int calculateUtility(WorldState state) {
		GameObject us = state.getOurAttacker();
		Goal goal = state.getOppGoal();
		GameObject annoyance = state.getOpponentDefender();
				
		if (inTheWay(us,annoyance,goal)) {
			// we can't shoot through things
			// yet
			return 2;
		}

        // TODO: fancy 'not actually pointing at goal' tricks eg bouncing ball off wall
        return 0;
	}
	
	/**
	 * If we shoot something at a goal from where we are, will the ball hit
	 * something before it gets there?
	 * 
	 * @param us Thing which wants a clear path to the goal
	 * @param thing Thing which might be in the way
	 * @param goal A goal we want the ball to be in
	 * @return true if thing in way, else false
	 */
	private boolean inTheWay(GameObject us, GameObject thing, Goal goal) {
		
		if (StratMaths.isInFrontOf(us, thing)) {
				return true;
		}
	
		return false; // shoot now, stop asking questions
	}
	
	/** 
	 * Reset the amount of attempts taken. This is done when another action is called
	 * on the attacker robot. (See the RobotStrategyState class)
	 */
	public void resetRepositionAttempts() {
		attempts = 0;
	}
	
	@Override
	public RobotInstruction getInstruction(WorldState state) {
		attempts++; // there has been another attempts at the reposition

		GameObject us = state.getOurAttacker();
		Goal goal = state.getOppGoal();
		GameObject annoyance = state.getOpponentDefender();

        // default to going nowhere
        double goodPos = us.getPos().Y;

        if (annoyance.getEuclidean(goal.getTopPost()) > annoyance.getEuclidean(goal.getBottomPost())) {
           /*
            * if the annoyance is closer to the bottom post,
            * get between it and the top post
            */
            goodPos = StratMaths.getBetweenY(annoyance, goal.getTopPost());
        } else {
            /*
             * otherwise, get between it and the bottom post
             * (including the very unlikely case where it's an
             *  equal distance from both posts)
             */
            goodPos = StratMaths.getBetweenY(annoyance, goal.getBottomPost());
        }

        return RobotInstruction.createLateralMove(goodPos);
	}
}
