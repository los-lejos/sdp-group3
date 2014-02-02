package dice.strategy;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.WorldState;

/*
 * @author Sam Stern
 */

public class ToZoneCenterAction extends StrategyAction {
	
	byte xD,yD,xA,yA; // TODO make -A and -B the zone centers for the attacking and defending robots
	
	public ToZoneCenterAction(RobotType target) {
		super(target);
	}

	@Override
	public boolean isPossible(WorldState state) {
		return true;
	}

	@Override
	protected int calculateUtility(WorldState state) {
		switch( this.getTargetRobot()) {
		case ATTACKER:
			if (state.getOpponentAttacker().getHasBall()||state.getOpponentDefender().getHasBall()) {
				return 1;
			} else {
				return 0;
			}
		case DEFENDER:
			if (state.getOpponentAttacker().getHasBall()|| state)
			
		}
		
//		if (state.getOpponentAttacker().getHasBall() && this.getTargetRobot().equals(state.getOurAttacker())) {
//			return 2;
//		} else if (state.getOpponentAttacker().getHasBall() && this.getTargetRobot.equals(state.getOurDefender())) {
//			return 0;
//		} else if (this.getTargetRobot().equals(state.getOurAttacker())) {
//			return 0;
//		} else {
//			return 1;
//		}
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		if (this.getTargetRobot().equals(other)) {
			
		}
	}

}
