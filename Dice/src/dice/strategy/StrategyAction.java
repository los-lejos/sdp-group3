package dice.strategy;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.WorldState;

/*
 * @author Joris S. Urbaitis
 */

public abstract class StrategyAction  {

	protected RobotType targetRobot;
	
	public StrategyAction(RobotType targetRobot) {
		this.targetRobot = targetRobot;
	}

	public abstract boolean isPossible(WorldState state);
	public abstract RobotInstruction getInstruction(WorldState state);
	
	protected GameObject getTargetObject(WorldState state) {
		if(this.targetRobot == RobotType.ATTACKER) {
			return state.getOurAttacker();
		} else {
			return state.getOurDefender();
		}
	}
}
