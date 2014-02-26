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
			return (state.getBallPossession() == WorldState.BallPossession.OUR_ATTACKER);
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
	
	/**
	 * Work out where to move a robot such that shooting a ball from
	 * there will not hit a thing
	 * 
	 * @param us A robot to instruct
	 * @param annoyance Thing which gets in the way
	 * @param goal A goal we want to shoot at
	 * @return An instruction to give to the robot
	 */
	private RobotInstruction getAvoidanceInstruction(GameObject us, 
			GameObject annoyance, Goal goal, WorldState state) {
		
		/*
		 * Currently using the highly janky method of trying
		 * to get the robot to move somewhere there isn't an 
		 * obstacle in front of it. Obviously rotation needs to be 
		 * considered in future.
		 */
		
		Vector2 goodPos = state.getCellCenter(WorldState.PitchZone.OUR_ATTACK_ZONE); // for relative positioning
		
		/*
		 * this code is not intended to actually work
		 * please replace it with a good way of figuring out an optimal position
		 * or things will be bad 
		 */
		if (annoyance.getPos().Y < state.getCellCenter(WorldState.PitchZone.OUR_ATTACK_ZONE).Y) {
			// favour moving +ve
			goodPos.setY(goodPos.Y + 10); // replace this
		} else {
			// favour moving -ve
			goodPos.setY(goodPos.Y - 10); // ...and this
		}
		
		return RobotInstruction.createLateralMoveTo(goodPos.Y);
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

        return RobotInstruction.createLateralMoveTo(goodPos);
	}
}
