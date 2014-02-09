package dice.state;

/** An abstract class to denote a line. This
 * is extended by the BoundedLine and UnboundedLine
 * classes.
 * @author Craig Wilkinson
 */
public abstract class Line {
    abstract double getGradient();
    abstract double getYIntersect();

    public Vector2 intersect(Line otherLine) {
        Vector2 result = null;

        // avoid parallel lines
        if (this.getGradient() != otherLine.getGradient()) {
            double c1 = this.getYIntersect();
            double c2 = otherLine.getYIntersect();
            double m1 = this.getGradient();
            double m2 = otherLine.getGradient();

            double x = (c2 - c1) / (m1 - m2);
            double y = m1 * x + c1;

            result = new Vector2(x, y);
        }

        return result;
    }

    // answer in radians
    public double angleOfIncidence(Line otherLine) {
        double alpha = Math.atan(this.getGradient());
        double beta = Math.atan(otherLine.getGradient());

        return beta - alpha;
    }


}
