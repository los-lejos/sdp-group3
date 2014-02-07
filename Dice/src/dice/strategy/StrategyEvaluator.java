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
 * @author Sam Stern
 */

public class StrategyEvaluator {
	
	public enum StrategyType {
		MATCH,
		SHOOTOUT,
		M3_DEFENDER,
		M3_ATTACKER
	}
	
	private RobotStrategyState attacker, defender;
	private StrategyType type;

	public StrategyEvaluator() {
		attacker = new RobotStrategyState(RobotType.ATTACKER);
		defender = new RobotStrategyState(RobotType.DEFENDER);
	}
	
	public void setType(StrategyType type) {
		this.type = type;
		
		this.resetAttackerActions();
		this.resetDefenderActions();
	}
	
	private void resetAttackerActions() {
		attacker.clearActions();
		
		if(this.type == StrategyType.MATCH) {
			attacker.addAction(new InterceptAction(RobotType.ATTACKER));
			attacker.addAction(new BlockAction(RobotType.ATTACKER));
			attacker.addAction(new ShootAction(RobotType.ATTACKER));
		} else if(this.type == StrategyType.SHOOTOUT) {
			
		} else if(this.type == StrategyType.M3_ATTACKER) {
			
		} else if(this.type == StrategyType.M3_DEFENDER) {
			
		}
	}
	
	private void resetDefenderActions() {
		defender.clearActions();
		
		if(this.type == StrategyType.MATCH) {
			defender.addAction(new InterceptAction(RobotType.DEFENDER));
			defender.addAction(new BlockAction(RobotType.DEFENDER));
		} else if(this.type == StrategyType.SHOOTOUT) {
			
		} else if(this.type == StrategyType.M3_ATTACKER) {
			
		} else if(this.type == StrategyType.M3_DEFENDER) {
			
		}
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
		StrategyAction bestAttackerAction = null;
		StrategyAction bestDefenderAction = null;
		
		if(attacker.actionsAvailable()) {
			bestAttackerAction = attacker.getBestAction(state);
		}
		
		if(defender.actionsAvailable()) {
			bestDefenderAction = defender.getBestAction(state);
		}

		// Flag that, if set, causes the new action to be sent to the robot
		// regardless of what it is doing right now
		boolean attackerOverride = false, defenderOverride = false;
		
		// Action overrides	
		// if defender is passing, attacker needs to receive
//		if (bestDefenderAction != null && bestDefenderAction instanceof PassAction) {
//			attackerOverride = true;
//			bestAttackerAction = new RecievePassAction(RobotType.ATTACKER);
//		}
		
		
		// Check if we should send actions to the robots
		if(bestDefenderAction != null && (defenderOverride || defender.needsNewAction(state))) {
			defender.setCurrentAction(bestDefenderAction, state);
		}
		
		if(bestAttackerAction != null && (attackerOverride || attacker.needsNewAction(state))) {
			attacker.setCurrentAction(bestAttackerAction, state);
		}
	}
}
