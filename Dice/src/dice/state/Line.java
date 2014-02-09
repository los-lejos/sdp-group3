package dice.state;

public class Line {
    private Vector2 startPoint;
    private Vector2 endPoint;

    public Line(Vector2 startPoint, Vector2 endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    // may return Infinity
    public double getGradient() {
        return (endPoint.Y - startPoint.Y) /
               (endPoint.X - startPoint.X);
    }

    public double getYIntersect() {
        return startPoint.Y - (getGradient() * startPoint.X);
    }

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

            // we've actually only calculated an intersection
            // of infinite length lines here, now we
            // must check that the point is within the bounds
            // of both lines
            Vector2 maybeResult = new Vector2(x, y);
            System.out.println("Intersection found at: ("
                + String.valueOf(x) + ","
                + String.valueOf(y) + ").");

            if (withinBounds(maybeResult)
                    && otherLine.withinBounds(maybeResult))
                result = maybeResult;
        }

        return result;
    }

    // we don't actually have to calculate whether the point
    // is on the line itself, as this function is only
    // used when we know the lines intersect at the point
    public boolean withinBounds(Vector2 point) {
        // is there a better way to do this?

        // return true if the X and Y coordinates of the point
        // are between those of the start and end points
        if ((point.X >= startPoint.X && point.X <= endPoint.X) ||
            (point.X <= startPoint.X && point.X >= endPoint.X)) {
            if ((point.Y >= startPoint.Y && point.Y <= endPoint.Y) ||
                (point.Y <= startPoint.Y && point.Y >= endPoint.Y)) {
                return true;
            }
        }

        return false;
    }

    // answer in radians
    public double angleOfIncidence(Line otherLine) {
        double alpha = Math.atan(getGradient());
        double beta = Math.atan(otherLine.getGradient());

        return beta - alpha;
    }

}
