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

	public RepositionAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		// only if the attacker has the ball
		return (state.getBallPossession() == WorldState.BallPossession.OUR_ATTACKER);
    }

	@Override
	protected int calculateUtility(WorldState state) {
		GameObject us = state.getOurAttacker();
		Goal goal = state.getOppGoal();
		GameObject annoyance = state.getOpponentDefender();
				
		// we have the ball but are we pointing roughly where the goal is?
		if (!pointingAtGoal(us,goal)) {
			// if not, please strongly consider pointing at the goal
			// it makes the ball more likely to go in
			return 2;
		} else if (inTheWay(us,annoyance,goal)) {
			// we can't shoot through things
			// yet
			return 2;
		}

        // TODO: fancy 'not actually pointing at goal' tricks eg bouncing ball off wall

        return 0;
	}
	
	/**
	 * Determine whether a thing is pointing in the general direction of a goal
	 * 
	 * @param us Thing that might be pointing at goal
	 * @param goal Goal it might be pointing at
	 * @return true or false
	 */
    private boolean pointingAtGoal(GameObject us, Goal goal) {
		Vector2 whereGoalIs = goal.getGoalCenter();
		
		return (us.getRotationRelativeTo(whereGoalIs) < StratMaths.SHOOT_ANGLE_TOLERANCE);
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
		
		return RobotInstruction.CreateLateralMoveTo(goodPos.Y);
	}
	
	@Override
	public RobotInstruction getInstruction(WorldState state) {
		GameObject us = state.getOurAttacker();
		Goal goal = state.getOppGoal();
		GameObject annoyance = state.getOpponentDefender();
				
		if (!pointingAtGoal(us,goal)) {
			// point robot at goal
			return RobotInstruction.CreateMoveTo(
					us.getRotationRelativeTo(goal.getGoalCenter()),
					0);
		} else  {
			// move robot somewhere where an annoyance isn't
			return getAvoidanceInstruction(us, annoyance, goal, state);
		}
	}
}