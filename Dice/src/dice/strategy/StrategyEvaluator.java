package dice.strategy;

import java.util.ArrayList;
import java.util.List;

import dice.communication.RobotCommunication;
import dice.state.WorldState;

/*
 * @author Joris S. Urbaitis
 */

/*
 * Keep track of what both robots are doing
 * When new state is received from vision or a robot completes an action,
 * use StrategyEvaluator to get a sorted list of actions by utility
 * 
 * If the robots are not performing actions or are performing actions that are now impossible / useless
 * send them new directives, otherwise leave it
 */

public class StrategyEvaluator {
	
	// Actions currently assigned to the robots
	StrategyAction defenderAction, attackerAction;
	
	private List<StrategyAction> possibleActions = new ArrayList<StrategyAction>();
	
	public StrategyEvaluator() {
		
	}

	/*
	 * Returns possible actions sorted by their utility from best to worst.
	 */
	public void onNewState(WorldState state) {
		possibleActions.clear();
		
		// Take possible moves, ex:
		// check to see if opponent's defending robot will get to the ball before it leaves the zone
		//	if yes, move attacking robot to intercept the ball
		//	if no, move to either middle of zone or inbetween
		
		// add those possible actions to a list together with their utility values
		// choose the best one
		
		StrategyAction bestAttackerAction = null, bestDefenderAction = null;
		
		// Flag that, if set, causes the new action to be sent to the robot
		// regardless of what it is doing right now
		boolean attackerOverride = false, defenderOverride = false;

		if(defenderOverride || defenderAction == null || defenderAction.isCompleted() || !defenderAction.isPossible(state)) {
			RobotCommunication.getInstance().sendInstruction(bestDefenderAction.getInstruction());
		}
	}
}
