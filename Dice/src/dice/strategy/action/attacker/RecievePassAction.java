package dice.strategy.action.attacker;

import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.state.WorldState.PitchZone;
import dice.strategy.StratMaths;
import dice.strategy.StrategyAction;

/*
 * @author Sam Stern
 */
public class RecievePassAction extends StrategyAction {

	public RecievePassAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		GameObject attacker = state.getOurAttacker();
		GameObject ball = state.getBall();
		Vector2 attackerPos = attacker.getPos();
		Vector2 ballPos = ball.getPos();
		Vector2 ballVel = state.getBall().getVelocity();

		double heading = attacker.getRotationRelativeTo(ball);
		double dot = ballVel.dot(attackerPos);
		
		// Check if the attacker is facing the ball
		// and that the ball is moving in the direction of the attacker
		boolean ballHeadingTowardsRobot = 
			ballVel.getLength() > 5 &&
			dot > 0 &&
			heading <= StratMaths.getRotationThreshold(attackerPos, ballPos);

		return state.getObjectWithBall() == state.getOurDefender() ||
				ball.getCurrentZone() == PitchZone.OUR_DEFEND_ZONE ||
				ballHeadingTowardsRobot;
	}

	@Override
	protected int calculateUtility(WorldState state) {
		return 5;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		GameObject attacker = state.getOurAttacker();
		GameObject defender = state.getOurDefender();
		
		double heading = attacker.getRotationRelativeTo(defender);
		
		int rotSpeed = StratMaths.speedForRot(heading);
		return RobotInstruction.createRotate(heading, rotSpeed);
	}
}
