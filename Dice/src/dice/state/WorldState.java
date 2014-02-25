package dice.state;

import java.security.InvalidParameterException;

import dice.Log;

/** The class that will contain all information about the
 * current state of the game.
 * @author Craig Wilkinson
 */
public class WorldState {

	public enum PitchZone {
		OUR_ATTACK_ZONE,
		OUR_DEFEND_ZONE,
		OPP_ATTACK_ZONE,
		OPP_DEFEND_ZONE
    }

    public enum BallPossession {
    	NONE,
    	OUR_DEFENDER,
    	OUR_ATTACKER,
    	OPP_DEFENDER,
    	OPP_ATTACKER
    }

    public enum Side {
        LEFT,
        RIGHT
    }

    private static int PITCH_HEIGHT = 320;
    private static int PITCH_WIDTH = 580;
    private static int GOAL_WIDTH = 200;

    private GameObject farLeftRobot;
    private GameObject middleLeftRobot;
    private GameObject middleRightRobot;
    private GameObject farRightRobot;
    private GameObject ball;
    
    // pitch geometry
    // These represent only the x values of the lines
    // it may be a better idea to use lines here, since the vision
    // may be skewed, or the table rotated slightly
    // o----f----s----t----e ^
    // |    |    |    |    | |
    // |    |    |    |    | | 320
    // o----f----s----t----e v
    // <--------580-------->
    private static final double origin = 0;
    private static final double width = 580;
    
    private static final double firstDivision = width / 4 + origin;
    private static final double secondDivision = width / 4 * 2 + origin;
    private static final double thirdDivision = width / 4 * 3 + origin;
    private static final double end = width + origin;
    

    // lines which represent the outer edge
    private Line top;
    private Line topRight;
    private Line right;
    private Line bottomRight;
    private Line bottom;
    private Line bottomLeft;
    private Line left;
    private Line topLeft;

    private BallPossession possession;

    // we are LEFT if our defender is on the left.
    private Side ourSide;

    private Goal leftGoal;
    private Goal rightGoal;
    
    // Utility to create and return a new WorldState
    public static WorldState init() {
    	GameObject opponentDefender = new GameObject();
		GameObject ourAttacker = new GameObject();
		GameObject opponentAttacker = new GameObject();
		GameObject ourDefender = new GameObject();
		GameObject ball = new GameObject();

		WorldState result = new WorldState(opponentDefender, opponentAttacker, ourDefender, ourAttacker, ball, Side.LEFT);

        Vector2 start = new Vector2(0,0);
        Vector2 end = new Vector2(1000,0);
        result.setTop(new BoundedLine(start,end));

        return result;
    }

    public static double convertYValue(double y) {
        double result = -1 * y + PITCH_HEIGHT;
        if (y != -1)
        	return result;
        else
        	return y;
    }

    public static Vector2 convertYValue(Vector2 point) {
        double newY = convertYValue(point.Y);
        return new Vector2(point.X, newY);
    }


    public void setTop(Line line) {
        this.top = line;
    }

    // these values come in as seen from left to right on the pitch (except ball)
    public void updateState(Vector2 a, double aAngle, Vector2 b, double bAngle,
    		Vector2 c, double cAngle, Vector2 d, double dAngle, Vector2 ball) {
        farLeftRobot.setPos(convertYValue(a));
        farLeftRobot.setRotation(aAngle);
        this.updateObjectZone(farLeftRobot);
        
        middleLeftRobot.setPos(convertYValue(b));
        middleLeftRobot.setRotation(bAngle);
        this.updateObjectZone(middleLeftRobot);
        
        middleRightRobot.setPos(convertYValue(c));
        middleRightRobot.setRotation(cAngle);
        this.updateObjectZone(middleRightRobot);
        
        farRightRobot.setPos(convertYValue(d));
        farRightRobot.setRotation(dAngle);
        this.updateObjectZone(farRightRobot);

        this.ball.setPos(convertYValue(ball));
        this.updateObjectZone(this.ball);
    }
    
    private void updateObjectZone(GameObject object) {
    	Vector2 position = object.getPos();
    	
    	if(position == null) { return; }
    	
        double objectX = position.X;

        if (objectX >= origin && objectX <= firstDivision) {
            object.setCurrentZone(zoneFromNumber(0));
        } else if (objectX <= secondDivision) {
        	object.setCurrentZone(zoneFromNumber(1));
        } else if (objectX <= thirdDivision) {
        	object.setCurrentZone(zoneFromNumber(2));
        } else if (objectX <= end) {
        	object.setCurrentZone(zoneFromNumber(3));
        } else {
            Log.logError("Cannot update object zone - unexpected x coordinate: " + objectX);
        }
    }

    public void setSide(Side side) {
        this.ourSide = side;
    }
    
    public Side getSide() {
    	return ourSide;
    }

    // populate the world. First all robots and the
    // ball must be created
    public WorldState(
    		GameObject a, GameObject b,
    		GameObject c, GameObject d,
    		GameObject ball, Side ourSide) {
        
        this.farLeftRobot = a;
        this.middleLeftRobot = b;
        this.middleRightRobot = c;
        this.farRightRobot = d;
        this.ball = ball;

        this.ourSide = ourSide;
    }

    // do this once at the beginning, so we have an "accurate"
    // representation of the pitch divisions (the pitch may be nudged
    // slightly).
    public void calibratePitch(BoundedLine top, BoundedLine topRight, BoundedLine right,
                               BoundedLine bottomRight, BoundedLine bottom,
                               BoundedLine bottomLeft, BoundedLine left, BoundedLine topLeft) {
        this.top = top;
        this.topRight = topRight;
        this.right = right;
        this.bottomRight = bottomRight;
        this.bottom = bottom;
        this.bottomLeft = bottomLeft;
        this.left = left;
        this.topLeft = topLeft;
        
        // construct the goals
        double middle = (left.getEndPoint().Y + left.getStartPoint().Y) / 2.0;
        Vector2 goalLeftTop = new Vector2(left.getEndPoint().X, middle+GOAL_WIDTH/2.0);
        Vector2 goalLeftBottom = new Vector2(left.getEndPoint().X, middle-GOAL_WIDTH/2.0);
        leftGoal = new Goal(goalLeftTop, goalLeftBottom);
        
        middle = (right.getEndPoint().Y + right.getStartPoint().Y) / 2.0;
        Vector2 goalRightTop = new Vector2(right.getEndPoint().X, middle+GOAL_WIDTH/2.0);
        Vector2 goalRightBottom = new Vector2(right.getEndPoint().X, middle-GOAL_WIDTH/2.0);
        rightGoal = new Goal(goalRightTop, goalRightBottom);
        
        System.out.println("Calibrated pitch.");
    }

    // 0-3 left to right on vision
    private PitchZone zoneFromNumber(int number)
            throws InvalidParameterException {
        PitchZone result;
        
        if (ourSide == Side.LEFT) {
            switch (number) {
                case 0:
                    result = PitchZone.OUR_DEFEND_ZONE;
                    break;
                case 1:
                    result = PitchZone.OPP_ATTACK_ZONE;
                    break;
                case 2:
                    result = PitchZone.OUR_ATTACK_ZONE;
                    break;
                case 3:
                    result = PitchZone.OPP_DEFEND_ZONE;
                    break;
                default:
                    throw new InvalidParameterException("Number must "
                        + "be between 0 and 3");
            }

        } else {
            switch (number) {
                case 0:
                    result = PitchZone.OPP_DEFEND_ZONE;
                    break;
                case 1:
                    result = PitchZone.OUR_ATTACK_ZONE;
                    break;
                case 2:
                    result = PitchZone.OPP_ATTACK_ZONE;
                    break;
                case 3:
                    result = PitchZone.OUR_DEFEND_ZONE;
                    break;
                default:
                    throw new InvalidParameterException("Number must "
                        + "be between 0 and 3");
            }
        }

        return result;
    }

    // pitch cell centers
    public Vector2 getCellCenter(PitchZone zone) {
        double y = width / 2.0;
        double x;

        if (ourSide == Side.LEFT) {
            switch (zone) {
                case OUR_DEFEND_ZONE:
                    x = origin + firstDivision / 2.0;
                    break;
                case OPP_ATTACK_ZONE:
                    x = firstDivision + secondDivision / 2.0;
                    break;
                case OUR_ATTACK_ZONE:
                    x = secondDivision + thirdDivision / 2.0;
                    break;
                case OPP_DEFEND_ZONE:
                    x = thirdDivision + end / 2.0;
                    break;
                default:
                    x = -1;
            }
        } else {
            switch (zone) {
                case OPP_DEFEND_ZONE:
                    x = origin + firstDivision / 2.0;
                    break;
                case OUR_ATTACK_ZONE:
                    x = firstDivision + secondDivision / 2.0;
                    break;
                case OPP_ATTACK_ZONE:
                    x = secondDivision + thirdDivision / 2.0;
                    break;
                case OUR_DEFEND_ZONE:
                    x = thirdDivision + end / 2.0;
                    break;
                default:
                    x = -1;
            }
        }

        return new Vector2(x, y);
    }
    
    // getters for the various robots and ball
    public GameObject getOpponentDefender() {
    	if (ourSide == Side.LEFT)
    		return farRightRobot;
    	else
    		return farLeftRobot;
    }

    public GameObject getOpponentAttacker() {
    	if (ourSide == Side.LEFT)
    		return middleLeftRobot;
    	else
    		return middleRightRobot;
    }

    public GameObject getOurDefender() {
    	if (ourSide == Side.LEFT)
    		return farLeftRobot;
    	else
    		return farRightRobot;
    }

    public GameObject getOurAttacker() {
    	if (ourSide == Side.LEFT)
    		return middleRightRobot;
    	else
    		return middleLeftRobot;
    }

    public GameObject getBall() {
    	return ball;
    }

    public BallPossession getBallPossession() {
    	return possession;
    }

    public void setBallPossession(BallPossession newPossession) {
        this.possession = newPossession;
    }

    public Goal getOurGoal() {
        if (ourSide == Side.LEFT)
            return leftGoal;
        else
            return rightGoal;
    }

    public Goal getOppGoal() {
        if (ourSide == Side.LEFT)
            return rightGoal;
        else
            return leftGoal;
    }

    public Line getTopLine() {
        return top;
    }

    public Line getBottomLine() {
        return bottom;
    }
}
