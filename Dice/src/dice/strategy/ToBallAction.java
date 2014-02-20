package dice.strategy;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.state.GameObject;


/*
 * @author Sam Stern
 * 
 * move to current ball position
 */

public class ToBallAction extends StrategyAction {

	public ToBallAction(RobotType targetRobot) {
		super(targetRobot);
	}
	
	@Override
	public String getActionType(){
		return "ToBallAction";
	}

	@Override
	public boolean isPossible(WorldState state) {
		if ((WorldState.PitchZone.OUR_ATTACK_ZONE.equals(state.getBallZone()) && 
				getTargetObject(state).equals(state.getOurAttacker())) ||
			(WorldState.PitchZone.OUR_DEFEND_ZONE.equals(state.getBallZone()) && 
					getTargetObject(state).equals(state.getOurDefender()))) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected int calculateUtility(WorldState state) {
		return 2;
//		if ((state.getBallPossession() == WorldState.BallPossession.OUR_ATTACKER) ||
//				(state.getBallPossession() == WorldState.BallPossession.OUR_ATTACKER)) {
//			return 0;
//		} else {
//			return 1;
//		}
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		Vector2 ballPos = state.getBall().getPos();
        GameObject robot;
        robot = getTargetObject(state);

        System.out.println("Ball pos: " + ballPos.X + "," + ballPos.Y);
        System.out.println("Robot pos: " + robot.getPos().X + "," + robot.getPos().Y);
        System.out.println(Math.toDegrees(robot.getRotationRelativeTo(ballPos)));
		return  RobotInstruction.CreateMoveTo(
				(long) Math.round(Math.toDegrees(robot.getRotationRelativeTo(ballPos))),
				(byte) Math.round(10 * robot.getEuclidean(ballPos)));
	}
}
