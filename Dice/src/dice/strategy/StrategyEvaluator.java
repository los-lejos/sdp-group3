package dice.strategy;

import dice.communication.RobotCommunicator;
import dice.communication.RobotType;
import dice.state.WorldState;

/**
 * Keep track of what both robots are doing
 * When new state is received from vision or a robot completes an action,
 * use StrategyEvaluator to get a sorted list of actions by utility
 * 
 * If the robots are not performing actions or are performing actions that are now impossible / useless
 * send them new directives, otherwise leave it
 * 
 * @author Joris S. Urbaitis
 * @author Andrew Johnston
 * @author Sam Stern+
 */

public class StrategyEvaluator {

	private RobotStrategyState attacker, defender;
	
	private static final int UPDATE_DELAY = 600;
	private long lastUpdateTime;

	public StrategyEvaluator() {
		attacker = new AttackerStrategyState();
		defender = new DefenderStrategyState();
	}

	public void setCommunicator(RobotType type, RobotCommunicator comms) {
		if(type == RobotType.ATTACKER) {
			attacker.setCommunicator(comms);
		} else {
			defender.setCommunicator(comms);
		}
	}

	/*
	 * Make decisions based on new world state.
	 */
	public void onNewState(WorldState state) {
		// Don't update very often. The issue with doing that is that the robot
		// will get flooded with messages and this will result in erratic movement
		if(System.currentTimeMillis() - lastUpdateTime < UPDATE_DELAY) {
			return;
		}
		
		lastUpdateTime = System.currentTimeMillis();

		// Update actions performed by robots
		attacker.updateCurrentAction(state);
		defender.updateCurrentAction(state);
	}
}
