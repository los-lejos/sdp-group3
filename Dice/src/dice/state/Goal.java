package dice.state;

public class Goal {
	private Vector2 topPost;
	private Vector2 bottomPost;
	private Line line;

	public Goal(Vector2 top, Vector2 bottom) {
		topPost = top;
		bottomPost = bottom;
		line = new BoundedLine(topPost, bottomPost);
    }
	
	public Line getLine() {
		return line;
	}

    public double getHeight() {
    	return bottomPost.Y - topPost.Y;
    }
    
    public Vector2 getTopPost() {
    	return topPost;
    }

    public Vector2 getBottomPost() {
    	return bottomPost;
    }

    public Vector2 getGoalCenter() {
        double centerY = (getTopPost().Y + getBottomPost().Y) / 2.0;
        return new Vector2(getTopPost().X, centerY);
    }

}
