package dice.strategy;

import dice.communication.RobotType;
import dice.state.WorldState;
import dice.strategy.action.defender.PassAction;
import dice.strategy.action.defender.PenaltyCorrectionAction;
import dice.strategy.action.defender.SaveAction;
import dice.strategy.action.shared.CloseKickerAction;
import dice.strategy.action.shared.CorrectionAction;
import dice.strategy.action.shared.OpenKickerAction;
import dice.strategy.action.shared.ToBallAction;

public class DefenderStrategyState extends RobotStrategyState {
	
	private PassAction pass;
	private ToBallAction toBall;
	private CorrectionAction correction;
	private SaveAction save;
	private OpenKickerAction openKicker;
	private CloseKickerAction closeKicker;
	private PenaltyCorrectionAction penaltyCorrect;

	public DefenderStrategyState() {
		super(RobotType.DEFENDER);
		
		this.pass = new PassAction(RobotType.DEFENDER);
		this.toBall = new ToBallAction(RobotType.DEFENDER);
		this.correction = new CorrectionAction(RobotType.DEFENDER, CorrectionAction.Side.OPP);
		this.save = new SaveAction(RobotType.DEFENDER);
		this.openKicker = new OpenKickerAction(RobotType.DEFENDER);
		this.closeKicker = new CloseKickerAction(RobotType.DEFENDER);
		this.penaltyCorrect = new PenaltyCorrectionAction(RobotType.DEFENDER);
	}
	
	@Override
	public StrategyAction getBestPenaltyAction(WorldState state) {
		if(save.isPossible(state)) {
			if(penaltyCorrect.isPossible(state)) {
				return penaltyCorrect;
			}
			
			return save;
		}
		
		return null;
	}

	@Override
	public StrategyAction getBestMatchAction(WorldState state) {
		if(pass.isPossible(state)) {
			if(correction.isPossible(state)) {
				return correction;
			}
			
			return pass;
		}
		
		if(toBall.isPossible(state)) {
			if(!this.isKickerOpen()) {
				return openKicker;
			}
			
			return toBall;
		} else if(this.isKickerOpen() && state.getObjectWithBall() != state.getOurDefender()) {
			return closeKicker;
		}
		
		if(save.isPossible(state)) {
			if(correction.isPossible(state)) {
				return correction;
			}
			
			return save;
		}

		return null;
	}

}
