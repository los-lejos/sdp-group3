package dice.strategy.action.defender;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.strategy.StratMaths;
import dice.strategy.StrategyAction;

public class PassAction extends StrategyAction {

	public PassAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		return state.getObjectWithBall() == state.getOurDefender();
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		GameObject defender = state.getOurDefender();
		Vector2 defenderPos = defender.getPos();
		
		double passY = StratMaths.getPassY(state.getOpponentAttacker());
		
		if(Math.abs(defenderPos.Y - passY) > StratMaths.Y_POS_THRESH) {
			double dist = StratMaths.getStrafeDist(defenderPos.Y, passY, state.getSide());
			return RobotInstruction.createLateralMove(dist);
		}
		
		return RobotInstruction.createKick();
	}
}
