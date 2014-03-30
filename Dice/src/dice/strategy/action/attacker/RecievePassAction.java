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
	
	private double passY = -1;

	public RecievePassAction(RobotType targetRobot) {
		super(targetRobot);
	}
	
	public void setPassY(double passY) {
		this.passY = passY;
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
			
		if(ballHeadingTowardsRobot) {
			// We want to stay where we are if the ball is heading towards the robot
			this.passY = -1;
		}

		return state.getObjectWithBall() == state.getOurDefender() ||
				ball.getCurrentZone() == PitchZone.OUR_DEFEND_ZONE ||
				ballHeadingTowardsRobot;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		GameObject target = this.getTargetObject(state);
		Vector2 targetPos = target.getPos();

		if(this.passY != -1 && Math.abs(targetPos.Y - passY) > StratMaths.Y_POS_THRESH) {
			double dist = StratMaths.getStrafeDist(targetPos.Y, passY, state.getSide());
			return RobotInstruction.createLateralMove(dist);
		} else {
			// Stay where we are and wait for the ball
			return null;
		}
	}
}
