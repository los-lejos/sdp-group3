package dice.strategy;

import dice.communication.RobotCommunicator;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.strategy.action.attacker.RecievePassAction;
import dice.strategy.action.defender.PassAction;

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
	
	private static final int UPDATE_DELAY = 200;
	private long lastUpdateTime;
	
	private long PASS_TIMEOUT = 6000;
	private long passTime = 0;
	private double passY = 0;

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
		final long currentTime = System.currentTimeMillis();
		
		// Don't update very often. The issue with doing that is that the robot
		// will get flooded with messages and this will result in erratic movement
		if(currentTime - lastUpdateTime < UPDATE_DELAY) {
			return;
		}
		
		this.lastUpdateTime = currentTime;

		// Update actions performed by robots
		StrategyAction attackerAction = attacker.getBestAction(state);
		StrategyAction defenderAction = defender.getBestAction(state);
		
		// Check if passing behaviour is being executed and synchronize it
		if(attackerAction != null && defenderAction != null &&
		   (attackerAction instanceof RecievePassAction ||
		   defenderAction instanceof PassAction))
		{
			if(currentTime - this.passTime > PASS_TIMEOUT) {
				// If we have been trying to pass for a while or are just starting to pass
				// get the new optimal pass position
				this.passTime = currentTime;
				this.passY = getPassY(state.getOpponentAttacker());
			} else {
				// If we are trying to pass, try to optimize the pass coordinate, but avoid
				// changing it too much as it may result in oscillation where
				// the robots keep moving from one end of the pitch to the other
				this.passY = updatePassY(state.getOpponentAttacker(), this.passY);
			}

			if(attackerAction instanceof RecievePassAction) {
				RecievePassAction recieve = (RecievePassAction)attackerAction;
				recieve.setPassY(this.passY);
			}
			
			if(defenderAction instanceof PassAction) {
				PassAction pass = (PassAction)defenderAction;
				pass.setPassY(this.passY);
			}
		}
		
		attacker.updateCurrentAction(state, attackerAction);
		defender.updateCurrentAction(state, defenderAction);
	}
	
	public static double getPassY(GameObject enemyAttacker) {
		Vector2 pos = enemyAttacker.getPos();
		
		// Return the side that has a wider opening relative to where the attacker robot is
		if(pos.Y > WorldState.PITCH_HEIGHT / 2.0) {
			return 0.0;
		} else {
			return WorldState.PITCH_HEIGHT;
		}
	}
	
	public static double updatePassY(GameObject enemyAttacker, double oldPassY) {
		if(Math.abs(oldPassY - enemyAttacker.getPos().Y) <= StratMaths.Y_POS_THRESH * 2) {
			return getPassY(enemyAttacker);
		} else {
			return oldPassY;
		}
	}
}
