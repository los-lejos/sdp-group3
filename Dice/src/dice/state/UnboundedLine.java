package dice.state;

/** A line class that is represented by a point
 * and a gradient (thus, is unbounded)
 * @author Craig Wilkinson
 */
public class UnboundedLine extends Line {
    private double gradient;
    private Vector2 point;

    public UnboundedLine(Vector2 startPoint, double gradient) {
        this.gradient = gradient;
        this.point = startPoint;
    }

    public double getGradient() {
        return gradient;
    }

    public double getYIntersect() {
        return point.Y - gradient * point.X;
    }

}
