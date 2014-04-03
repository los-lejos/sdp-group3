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

    public enum Side {
        LEFT,
        RIGHT
    }
    
    public enum Pitch {
    	PITCH0,
    	PITCH1
    }

    // for ball ownership
    private static double OWNERSHIP_DISTANCE = 20; // in px
    private static double OWNERSHIP_THRESH = 20;

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
    private static final double ORIGIN = 0;
    
    public static final double PITCH_HEIGHT = 320;
    public static final double PITCH_WIDTH = 580;
    private static final int GOAL_WIDTH = 150;
    
    private static double FIRST_ADJUSTMENT;
    private static double SECOND_ADJUSTMENT;
    private static double THIRD_ADJUSTMENT;
    
    private static double FIRST_CENTER_OFFSET = -20;
    private static double FOURTH_CENTER_OFFSET = 20;

    private static final double FIRST_DIVISION = PITCH_WIDTH / 4 + ORIGIN + FIRST_ADJUSTMENT;
    private static final double SECOND_DIVISION = PITCH_WIDTH / 4 * 2 + ORIGIN + SECOND_ADJUSTMENT;
    private static final double THIRD_DIVISION = PITCH_WIDTH / 4 * 3 + ORIGIN + THIRD_ADJUSTMENT;
    private static final double END = PITCH_WIDTH + ORIGIN;

    // lines which represent the outer edge
    private Line top = new BoundedLine(new Vector2(0, PITCH_HEIGHT), new Vector2(PITCH_WIDTH, PITCH_HEIGHT));
    private Line bottom = new BoundedLine(new Vector2(0, 0), new Vector2(PITCH_WIDTH, 0));

    private GameObject guySlashGirlWithBall;

    // we are LEFT if our defender is on the left.
    private Side ourSide;
    
    // This is pitch0 for main pitch, pitch1 otherwise
    private Pitch pitch;

    private Goal leftGoal = new Goal(new Vector2(0, PITCH_HEIGHT / 2.0 + GOAL_WIDTH / 2.0),
    								 new Vector2(0, PITCH_HEIGHT / 2.0 - GOAL_WIDTH / 2.0));
    private Goal rightGoal = new Goal(new Vector2(PITCH_WIDTH, PITCH_HEIGHT / 2.0 + GOAL_WIDTH / 2.0),
			 						  new Vector2(PITCH_WIDTH, PITCH_HEIGHT / 2.0 - GOAL_WIDTH / 2.0));
    
    // Utility to create and return a new WorldState
    public static WorldState init() {
    	GameObject opponentDefender = new GameObject();
		GameObject ourAttacker = new GameObject();
		GameObject opponentAttacker = new GameObject();
		GameObject ourDefender = new GameObject();
		GameObject ball = new GameObject();

		WorldState result = new WorldState(opponentDefender, opponentAttacker, ourDefender, ourAttacker, ball, Side.LEFT, Pitch.PITCH0);
        Vector2 start = new Vector2(0,0);
        Vector2 end = new Vector2(1000,0);
        result.setTop(new BoundedLine(start,end));

        return result;
    }
    
    public boolean hasData() {
    	return
    		this.getOurAttacker().hasData() &&
    		this.getOurDefender().hasData() &&
    		this.getOpponentAttacker().hasData() &&
    		this.getOpponentDefender().hasData() &&
    		this.getBall().hasData();
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

        updateBallOwnership();
        this.updateObjectZone(this.ball);
    }
    
    private void updateObjectZone(GameObject object) {
    	Vector2 position = object.getPos();
    	
    	if(position == null) { return; }
    	
        double objectX = position.X;

        if (objectX >= ORIGIN && objectX <= FIRST_DIVISION) {
            object.setCurrentZone(zoneFromNumber(0));
        } else if (objectX <= SECOND_DIVISION) {
        	object.setCurrentZone(zoneFromNumber(1));
        } else if (objectX <= THIRD_DIVISION) {
        	object.setCurrentZone(zoneFromNumber(2));
        } else if (objectX <= END) {
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
    
    public void setPitch(Pitch pitch) {
    	this.pitch = pitch;
    	updateZoneCalibration(this.pitch);
    }
    
    public Pitch getPitch() {
    	return pitch;
    }

    // populate the world. First all robots and the
    // ball must be created
    public WorldState(
    		GameObject a, GameObject b,
    		GameObject c, GameObject d,
    		GameObject ball, Side ourSide,
    		Pitch pitch) {
        
        this.farLeftRobot = a;
        this.middleLeftRobot = b;
        this.middleRightRobot = c;
        this.farRightRobot = d;
        this.ball = ball;

        this.ourSide = ourSide;
        this.pitch = pitch;
        
        updateZoneCalibration(this.pitch);
    }
    
    private void updateZoneCalibration(Pitch pitch) {
        if (this.pitch == Pitch.PITCH1) {
        	FIRST_ADJUSTMENT = -6;
        	SECOND_ADJUSTMENT = 7;
        	THIRD_ADJUSTMENT = 25;
        } else {
        	FIRST_ADJUSTMENT = -18;
            SECOND_ADJUSTMENT = 0;
            THIRD_ADJUSTMENT = 15;
        }
        
        Log.logInfo("Pitch calibrated for " + pitch);
    }

    // 0-3 left to right on vision. This is done because the order is reversed
    // depending on which side we are on
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
        double y = PITCH_HEIGHT / 2.0;
        double x;

        if (ourSide == Side.LEFT) {
            switch (zone) {
                case OUR_DEFEND_ZONE:
                    x = (ORIGIN + FIRST_DIVISION) / 2.0 + FIRST_CENTER_OFFSET;
                    break;
                case OPP_ATTACK_ZONE:
                    x = (FIRST_DIVISION + SECOND_DIVISION) / 2.0;
                    break;
                case OUR_ATTACK_ZONE:
                    x = (SECOND_DIVISION + THIRD_DIVISION) / 2.0;
                    break;
                case OPP_DEFEND_ZONE:
                    x = (THIRD_DIVISION + END) / 2.0 + FOURTH_CENTER_OFFSET;
                    break;
                default:
                    x = -1;
            }
        } else {
            switch (zone) {
                case OPP_DEFEND_ZONE:
                    x = (ORIGIN + FIRST_DIVISION) / 2.0 + FIRST_CENTER_OFFSET;
                    break;
                case OUR_ATTACK_ZONE:
                    x = (FIRST_DIVISION + SECOND_DIVISION) / 2.0;
                    break;
                case OPP_ATTACK_ZONE:
                    x = (SECOND_DIVISION + THIRD_DIVISION) / 2.0;
                    break;
                case OUR_DEFEND_ZONE:
                    x = (THIRD_DIVISION + END) / 2.0 + FOURTH_CENTER_OFFSET;
                    break;
                default:
                    x = -1;
            }
        }

        return new Vector2(x, y);
    }
    
    /** Used to decide who owns the ball, if anyone.
     * @return The game object who owns the ball or null; if nobody does
     */
    public void updateBallOwnership() {
    	// only do this if the ball is visible. This way, the owner cannot
    	// be changed unless the ball is visible
    	// Also don't consider this if our robot has the ball as this is already
    	// confirmed by a sensor on the robot
    	if (!ball.isVisible() ||
    			this.guySlashGirlWithBall == this.getOurAttacker() ||
    			this.guySlashGirlWithBall == this.getOurDefender())
    		return;
    	
		GameObject result = null;
		
    	PitchZone ballZone = ball.getCurrentZone();
    	
    	// get the object who owns the zone the ball is in
    	// only consider the opponent's robots as our robots
    	// let us know when they have the ball
    	GameObject nearestObject = null;
    	if(ballZone == WorldState.PitchZone.OPP_ATTACK_ZONE) {
    		nearestObject = getOpponentAttacker(); 
    	} else if (ballZone == WorldState.PitchZone.OPP_DEFEND_ZONE) {
    		nearestObject = getOpponentDefender();
    	}
    	
    	// as long as the ball is in _a_ zone, check if the ball
    	// is in front of the object
    	if (nearestObject != null && nearestObject.hasData()) {
    		// get a point just in front of the object
    		Vector2 objectPos = nearestObject.getPos();
    		Vector2 ballPos = ball.getPos();
    		double objectRotation = nearestObject.getRotation();
    		
    		double xOffset = Math.sin(objectRotation) * OWNERSHIP_DISTANCE;
    		double yOffset = Math.cos(objectRotation) * OWNERSHIP_DISTANCE;
    		double ownershipXPos = objectPos.X + xOffset;
    		double ownershipYPos = objectPos.Y + yOffset;
    		
    		// calculate the distance from the ball to the centre
    		// of the ownership point (check that it is in the
    		// ownership area)
    		double xDiffSquared = Math.pow(ballPos.X - ownershipXPos, 2);
    		double yDiffSquared = Math.pow(ballPos.Y - ownershipYPos,2);
    		double distance = Math.sqrt(xDiffSquared + yDiffSquared);
    		
    		// if the ball is within the ownership area, then
    		// the object of the current zone is the owner
    		if (distance <= OWNERSHIP_THRESH) {
    			result = nearestObject;
    		}
    	}
    	
    	setObjectWithBall(result);
    	
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

    public GameObject getObjectWithBall() {
    	return this.guySlashGirlWithBall;
    }

    public void setObjectWithBall(GameObject guySlashGirlWithBall) {
        this.guySlashGirlWithBall = guySlashGirlWithBall;
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
