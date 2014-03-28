package dice.strategy;

import dice.communication.RobotCommunicator;
import dice.communication.RobotType;
import dice.state.WorldState;
import dice.strategy.action.attacker.BlockAction;
import dice.strategy.action.attacker.RecievePassAction;
import dice.strategy.action.attacker.ShootAction;
import dice.strategy.action.defender.PassAction;
import dice.strategy.action.defender.SaveAction;
import dice.strategy.action.shared.CorrectionAction;
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
 * @author Sam Stern+
 */

public class StrategyEvaluator {

	private RobotStrategyState attacker, defender;

	public StrategyEvaluator() {
		attacker = new RobotStrategyState(RobotType.ATTACKER);
		defender = new RobotStrategyState(RobotType.DEFENDER);
		
		setAttackerActions();
		setDefenderActions();
	}

	private void setAttackerActions() {
		attacker.addAction(new RecievePassAction(RobotType.ATTACKER));
		attacker.addAction(new ShootAction(RobotType.ATTACKER));
		attacker.addAction(new ToBallAction(RobotType.ATTACKER));
		attacker.addAction(new CorrectionAction(RobotType.ATTACKER));
		attacker.addAction(new BlockAction(RobotType.ATTACKER));
	}
	
	private void setDefenderActions() {
		defender.addAction(new PassAction(RobotType.DEFENDER));
		defender.addAction(new ToBallAction(RobotType.DEFENDER));
		defender.addAction(new CorrectionAction(RobotType.DEFENDER));
		defender.addAction(new SaveAction(RobotType.DEFENDER));
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
		
		// Don't want to check the actions if we don't have data about our attacker
		// so check if the position is not null
		if(attacker.actionsAvailable()) {
			bestAttackerAction = attacker.getBestAction(state);
		}
		
		if(defender.actionsAvailable()) {
			bestDefenderAction = defender.getBestAction(state);
		}

		// Check if we should send actions to the robots
		if(bestDefenderAction != null) {
			defender.setCurrentAction(bestDefenderAction, state);
		}
		
		if(bestAttackerAction != null) {
			attacker.setCurrentAction(bestAttackerAction, state);
		}
	}
}
