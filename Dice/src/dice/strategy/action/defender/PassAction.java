package dice.strategy.action.defender;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.strategy.StratMaths;
import dice.strategy.StrategyAction;

public class PassAction extends StrategyAction {
	
	private double passY = -1;

	public PassAction(RobotType targetRobot) {
		super(targetRobot);
	}
	
	public void setPassY(double passY) {
		this.passY = passY;
	}

	@Override
	public boolean isPossible(WorldState state) {
		return state.getObjectWithBall() == state.getOurDefender();
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		GameObject defender = state.getOurDefender();
		Vector2 defenderPos = defender.getPos();
		
		double yDist = Math.abs(defenderPos.Y - passY); 
		System.out.println(yDist);
		
		if(yDist > StratMaths.Y_POS_THRESH) {
			double dist = StratMaths.getStrafeDist(defenderPos.Y, passY, state.getSide());
			return RobotInstruction.createLateralMove(dist);
		}
		
		return RobotInstruction.createKick();
	}
}
