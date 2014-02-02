package dice.strategy;

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
 * @author Sam Stern
 */

public class StrategyEvaluator {
	
	private RobotStrategyState attacker, defender;

	public StrategyEvaluator() {
		// Populate attacker actions
		attacker = new RobotStrategyState();
		attacker.addAction(new InterceptAction(RobotType.ATTACKER));
		attacker.addAction(new ShootAction(RobotType.ATTACKER));
		attacker.addAction(new BlockAction(RobotType.ATTACKER));
		attacker.addAction(new ToZoneCenterAction(RobotType.ATTACKER));
		attacker.addAction(new RecievePassAction(RobotType.ATTACKER));
		
		// Populate defender actions
		defender = new RobotStrategyState();
		defender.addAction(new InterceptAction(RobotType.DEFENDER));
		defender.addAction(new ToGoalAction(RobotType.DEFENDER));
		defender.addAction(new PassAction(RobotType.DEFENDER));
		defender.addAction(new BlockAction(RobotType.DEFENDER));
		defender.addAction(new GetBallAction(RobotType.DEFENDER));
		defender.addAction(new ToZoneCenterAction(RobotType.DEFENDER));
	}

	/*
	 * Make decisions based on new world state.
	 */
	public void onNewState(WorldState state) {
		StrategyAction bestAttackerAction = attacker.getBestAction(state);
		StrategyAction bestDefenderAction = defender.getBestAction(state);

		// Flag that, if set, causes the new action to be sent to the robot
		// regardless of what it is doing right now
		boolean attackerOverride = false, defenderOverride = false;
		
		// Action overrides	
		// if defender is passing, attacker needs to receive
		if (bestDefenderAction instanceof PassAction) {
			attackerOverride = true;
			bestAttackerAction = new RecievePassAction(RobotType.ATTACKER);
		}
		
		
		// Check if we should send actions to the robots
		if(defenderOverride || defender.needsNewAction(state)) {
			defender.setCurrentAction(bestDefenderAction, state);
		}
		
		if(attackerOverride || attacker.needsNewAction(state)) {
			attacker.setCurrentAction(bestAttackerAction, state);
		}
	}
}
