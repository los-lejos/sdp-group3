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

	private static final double DEFENDER_BLOCK_DIST_THRESH = 35;
	
	public ShootAction(RobotType targetRobot) {
		super(targetRobot);
	}

	@Override
	public boolean isPossible(WorldState state) {
		return true;
		//return state.getObjectWithBall() == this.getTargetObject(state);
	}

	@Override
	public RobotInstruction getInstruction(WorldState state) {
		GameObject target = this.getTargetObject(state);
		Vector2 targetPos = target.getPos();

		double heading = StratMaths.getAngleRelativeToHorizontal(target, state.getSide());
		
		// Rotate towards goal
		if(Math.abs(heading) > StratMaths.CORRECTION_ROT_THRESH) {
			System.out.println("Rotate " + heading);
			int rotSpeed = StratMaths.speedForRot(heading);
			return RobotInstruction.createRotate(heading, rotSpeed);
		}
		
		Vector2 zoneMiddle = state.getCellCenter(target.getCurrentZone());
		double deltaX = zoneMiddle.X - target.getPos().X;

		// Check if we should move towards the center of the zone 
		if(Math.abs(deltaX) > StratMaths.POSITION_FUZZ) {
			double dist = state.getSide() == Side.LEFT ? deltaX : -deltaX;
			int moveSpeed = StratMaths.speedForDist(dist);
			return RobotInstruction.createMove(dist, moveSpeed);
		}
		
		GameObject defender = state.getOpponentDefender();
		Vector2 defenderPos = defender.getPos();
		Goal oppGoal = state.getOppGoal();

		// If defender is blocking or we are not in front of the goal,
		// strafe to the side of the goal that is larger
		if(oppGoal.getBottomPost().Y > targetPos.Y || oppGoal.getTopPost().Y < targetPos.Y ||
			Math.abs(targetPos.Y - defenderPos.Y) <= DEFENDER_BLOCK_DIST_THRESH) {

			// Find which side of the goal has a larger opening
			double targetY;
			if(defenderPos.Y > WorldState.PITCH_HEIGHT / 2.0) {
				targetY = (defenderPos.Y - oppGoal.getBottomPost().Y) / 2.0 + oppGoal.getBottomPost().Y;
			} else {
				targetY = (oppGoal.getTopPost().Y - defenderPos.Y) / 2.0 + defenderPos.Y;
			}

			double dist = getMovementAmount(targetPos.Y, targetY, state.getSide());
			System.out.println("Strafe " + dist);
			return RobotInstruction.createLateralMove(dist);
		}
		
		// Shoot at the goal
		System.out.println("Kick");
		return RobotInstruction.createKick();
	}
	
	private static double getMovementAmount(double ourY, double targetY,
			WorldState.Side side) {
		if (side == WorldState.Side.LEFT) {
			return targetY - ourY;
		} else {
			return ourY - targetY;
		}
	}
}
