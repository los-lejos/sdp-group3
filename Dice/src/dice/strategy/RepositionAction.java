package dice.strategy;

import java.util.Arrays;
import java.util.List;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Goal;
import dice.state.Vector2;
import dice.state.WorldState;

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
	public String getActionType() {
		return "RepositionAction";
	}

	@Override
	public boolean isPossible(WorldState state) {
		// only if the attacker has the ball
		if (state.getBallPossession() == WorldState.BallPossession.OUR_ATTACKER) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected int calculateUtility(WorldState state) {
		GameObject us = state.getOurAttacker();
		Goal goal = state.getOppGoal();
		
		// pesky other team getting in the way all the time		
		List<GameObject> annoyingThings = Arrays.asList(state.getOpponentAttacker(), state.getOpponentDefender());
	
		// we have the ball but are we pointing roughly where the goal is?
		if (!pointingAtGoal(us,goal)) {
			// if not, please strongly consider pointing at the goal
			// it makes the ball more likely to go in
			return 2;
		} else if (inTheWay(us,annoyingThings,goal)) {
			// we can't shoot through things
			// yet
			return 2;
		}
		
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
		
		if (us.getRotationRelativeTo(whereGoalIs) < StratMaths.SHOOT_ANGLE_TOLERANCE) {
			return true;
		}
		
		// TODO: fancy 'not actually pointing at goal' tricks eg bouncing ball off wall
		
		return false;
	}
	
	/**
	 * If we shoot something at a goal from where we are, will the ball hit
	 * something before it gets there?
	 * 
	 * @param us Thing which wants a clear path to the goal
	 * @param things Things which might be in the way
	 * @param goal A goal
	 * @return Whether any of 'things' are in the way
	 */
	private boolean inTheWay(GameObject us,
			List<GameObject> things, Goal goal) {
		
		for(GameObject thing : things) {
			if (StratMaths.isInFrontOf(us, thing)) {
				return true;
			}
		}
	
		return false; // shoot now, stop asking questions
	}
	
	/**
	 * Work out where to move a robot such that shooting a ball from
	 * there will not hit things
	 * 
	 * @param us A robot to instruct
	 * @param annoyingThings List of things that get in the way
	 * @param goal A goal we want to shoot at
	 * @return An instruction to give to the robot
	 */
	private RobotInstruction getAvoidanceInstruction(GameObject us,
			List<GameObject> annoyingThings, Goal goal, WorldState state) {
		
		/*
		 * Currently using the highly janky method of trying
		 * to get the robot to move somewhere there isn't an 
		 * obstacle in front of it. Obviously rotation needs to be 
		 * considered in future.
		 */
		
		Vector2 goodPos = state.getCellCenter(WorldState.PitchZone.OUR_ATTACK_ZONE); // for relative positioning
		
		for (GameObject annoyance : annoyingThings) {
			
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
			
		}
		
		return RobotInstruction.CreateLateralMoveTo((byte) goodPos.Y);
	}
	
	@Override
	public RobotInstruction getInstruction(WorldState state) {
		GameObject us = state.getOurAttacker();
		Goal goal = state.getOppGoal();
		
		List<GameObject> annoyingThings = Arrays.asList(state.getOpponentAttacker(), state.getOpponentDefender());
		
		if (!pointingAtGoal(us,goal)) {
			// point robot at goal
			return RobotInstruction.CreateMoveTo(
					(long) us.getRotationRelativeTo(goal.getGoalCenter()),
					(byte) 0);
		} else if (inTheWay(us, annoyingThings, goal)) {
			// move robot somewhere where they aren't
			return getAvoidanceInstruction(us, annoyingThings, goal, state);
		}
		
		return null;
	}



}
