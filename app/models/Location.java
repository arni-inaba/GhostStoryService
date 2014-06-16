package models;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.dto.LocationDto;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Location extends Model {

    @Id
    public long id;

    @Constraints.Required
    public double longitude;

    @Constraints.Required
    public double latitude;

    @ManyToOne(optional = false)
    @JsonIgnore
    public Story story;

    @Constraints.MaxLength(255)
    public String name;

    private static Finder<Long, Location> finder = new Finder<Long, Location>(Long.class, Location.class);

    public static Location findById(Long id) {
        return finder.byId(id);
    }

    public static List<Location> allLocations() {
        return finder.all();
    }

    // will be used when updating Story with new locations
    public static ObjectNode createLocationFromDto(LocationDto locationDto) {
        ObjectNode result = Json.newObject();
        Ebean.beginTransaction();

        Location location = new Location();
        location.longitude = locationDto.longitude;
        location.latitude = locationDto.latitude;
        location.name = locationDto.name;
        location.story = Story.findById(locationDto.storyId);

        if (location.story == null) {
            result.put("error", "No story exists with id " + locationDto.storyId);
            return result;
        }
        location.save();
        locationDto.id = location.id;
        result.put("success", Json.toJson(locationDto));
        return result;
    }

    public static ObjectNode doDelete(Long id) {
        ObjectNode result = Json.newObject();
        Location location = findById(id);
        Ebean.beginTransaction();
        try {
            location.delete();
            Ebean.commitTransaction();
        } catch (Exception e) {
            result.put("error", "Could not delete location with id: " + id);
            return result;
        } finally {
            Ebean.endTransaction();
        }
        result.put("success", "Deleted location with id: " + id);
        return result;
    }

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
