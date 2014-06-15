package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Location extends Model {

    @Id
    public long id;

    @Constraints.Required
    public double longitude;

    @Constraints.Required
    public double latitude;

    public String name;

    private static Finder<Long, Location> finder = new Finder<Long, Location>(Long.class, Location.class);


    public double distanceTo(Location that) {
        double theta = this.longitude - that.longitude;
        double distance = Math.sin(deg2rad(this.latitude)) * Math.sin(deg2rad(that.latitude))
                + Math.cos(deg2rad(this.latitude)) * Math.cos(deg2rad(that.latitude)) * Math.cos(deg2rad(theta));
        distance = Math.acos(distance);
        distance = rad2deg(distance);
        return distance * 60 * 1.853159616;
    }

    private static double deg2rad(double deg)
    {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad)
    {
        return (rad * 180 / Math.PI);
    }

    @Override
    public String toString() {
        return longitude + "," + latitude;
    }
}
