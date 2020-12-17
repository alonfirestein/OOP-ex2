package api;

/**
 * This class was created in order to implement a geolocation for objects in a weighted directed graph.
 *
 * @author Alon Firestein
 */
public class GeoLocation implements geo_location{
    double x, y, z;

    public GeoLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public double z() {
        return z;
    }

    //Using the formula of finding the distance between two 3D points:
    // https://www.engineeringtoolbox.com/distance-relationship-between-two-points-d_1854.html
    @Override
    public double distance(geo_location g) {
        double result = Math.sqrt(Math.pow((g.x()-x), 2) + Math.pow((g.y()-y), 2) + Math.pow((g.z()-z), 2));
        return result;    }

}
