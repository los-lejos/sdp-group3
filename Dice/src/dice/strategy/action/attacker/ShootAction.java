package dice.strategy.action.attacker;


import dice.communication.RobotInstruction;
import dice.communication.RobotType;
import dice.state.GameObject;
import dice.state.Goal;
import dice.state.Vector2;
import dice.state.WorldState;
import dice.state.WorldState.Side;
import dice.strategy.StratMaths;
import dice.strategy.StrategyAction;

/*
 * @author Sam Stern
 */

public class ShootAction extends StrategyAction {
	
	private final int TIMEOUT = 10000;
	
	private long startTime = -1;

	public ShootAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		boolean possible = state.getObjectWithBall() == this.getTargetObject(state);
		
		if(!possible) {
			startTime = -1;
		} else if(startTime == -1 && possible) {
			startTime = System.currentTimeMillis();
		}
		
		return possible;
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		GameObject target = this.getTargetObject(state);
		Vector2 targetPos = target.getPos();
		
		GameObject defender = state.getOpponentDefender();
		Vector2 defenderPos = defender.getPos();
		Goal oppGoal = state.getOppGoal();
		
		if(System.currentTimeMillis() - this.startTime > TIMEOUT) {
			double dist = this.getStrafeDist(targetPos, defenderPos, oppGoal, state);
			double xDist = getForwardSprintDist(targetPos, state);
			return RobotInstruction.createStrafeAndMoveAndKick(xDist, dist);
		}

		// If defender is blocking or we are not in front of the goal,
		// strafe to the side of the goal that is larger
		if(oppGoal.getBottomPost().Y + StratMaths.SHOOT_GOAL_Y_THRESH > targetPos.Y ||
			oppGoal.getTopPost().Y - StratMaths.SHOOT_GOAL_Y_THRESH < targetPos.Y ||
			Math.abs(targetPos.Y - defenderPos.Y) <= StratMaths.Y_POS_THRESH) {

			double dist = this.getStrafeDist(targetPos, defenderPos, oppGoal, state);
			return RobotInstruction.createLateralMove(dist);
		}

		double heading = StratMaths.getAngleRelativeToHorizontal(target, state.getSide() == WorldState.Side.RIGHT);
		if(Math.abs(heading) > StratMaths.ROTATION_SHOOT_THRESH) {
			int rotSpeed = StratMaths.speedForRot(heading);
			return RobotInstruction.createRotate(heading, rotSpeed);
		}
		
		double xDist = getForwardSprintDist(targetPos, state);
		return RobotInstruction.createMoveAndKick(xDist);
	}
	
	private double getForwardSprintDist(Vector2 targetPos, WorldState state) {
		double xDist;
		if(state.getSide() == Side.LEFT) {
			xDist = state.getThirdDelimiter() - targetPos.X;
		} else {
			xDist = targetPos.X - state.getFirstDelimiter();
		}
		
		xDist -= StratMaths.BALL_DISTANCE_THRESH;
		return xDist;
	}
	
	private double getStrafeDist(Vector2 targetPos, Vector2 defenderPos, Goal oppGoal, WorldState state) {
		// Find which side of the goal has a larger opening
		double targetY;
		if(defenderPos.Y > WorldState.PITCH_HEIGHT / 2.0) {
			targetY = (defenderPos.Y - oppGoal.getBottomPost().Y) / 2.0 + oppGoal.getBottomPost().Y;
		} else {
			targetY = (oppGoal.getTopPost().Y - defenderPos.Y) / 2.0 + defenderPos.Y;
		}

		return StratMaths.getStrafeDist(targetPos.Y, targetY, state.getSide() == Side.RIGHT);
	}
}
