package dice.strategy;

import dice.communication.RobotCommunicator;
import dice.communication.RobotType;
import dice.state.WorldState;
import dice.strategy.action.attacker.InterceptAction;
import dice.strategy.action.attacker.RecievePassAction;
import dice.strategy.action.attacker.ShootAction;
import dice.strategy.action.attacker.ToZoneCenterAction;
import dice.strategy.action.defender.PassAction;
import dice.strategy.action.defender.SaveAction;
import dice.strategy.action.defender.ToGoalCenterAction;
import dice.strategy.action.shared.BlockAction;
import dice.strategy.action.shared.FaceBallAction;
import dice.strategy.action.shared.ToBallAction;

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
		M3_ATTACKER,
		NONE
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
			/*attacker.addAction(new InterceptAction(RobotType.ATTACKER));*/
			attacker.addAction(new RecievePassAction(RobotType.ATTACKER));
			/*attacker.addAction(new ShootAction(RobotType.ATTACKER));
			attacker.addAction(new ToZoneCenterAction(RobotType.ATTACKER));
			attacker.addAction(new BlockAction(RobotType.ATTACKER));
			attacker.addAction(new FaceBallAction(RobotType.ATTACKER));*/
			attacker.addAction(new ToBallAction(RobotType.ATTACKER));
		} else if(this.type == StrategyType.SHOOTOUT) {
			
		} else if(this.type == StrategyType.M3_ATTACKER) {
			attacker.addAction(new ToBallAction(RobotType.ATTACKER));
			//attacker.addAction(new ShootAction(RobotType.ATTACKER));
		} else if (this.type == StrategyType.NONE) {
			// do nothing
		}
	}
	
	private void resetDefenderActions() {
		defender.clearActions();
		
		if(this.type == StrategyType.MATCH) {
			defender.addAction(new SaveAction(RobotType.DEFENDER));
			defender.addAction(new ToGoalCenterAction(RobotType.DEFENDER));
			defender.addAction(new PassAction(RobotType.DEFENDER));
			defender.addAction(new BlockAction(RobotType.DEFENDER));
			defender.addAction(new FaceBallAction(RobotType.DEFENDER));
			defender.addAction(new ToBallAction(RobotType.DEFENDER));
		} else if(this.type == StrategyType.SHOOTOUT) {
			
		} else if(this.type == StrategyType.M3_DEFENDER) {
			defender.addAction(new SaveAction(RobotType.DEFENDER));

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
		
		// Check if we should send actions to the robots
		if(bestDefenderAction != null && (defenderOverride || defender.needsNewAction(state))) {
			defender.setCurrentAction(bestDefenderAction, state);
		}
		
		if(bestAttackerAction != null && (attackerOverride || attacker.needsNewAction(state))) {
			attacker.setCurrentAction(bestAttackerAction, state);
		}
	}
}
