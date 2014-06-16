package api;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Location;
import models.Story;
import models.dto.LocationDto;
import play.libs.Json;
import play.mvc.Controller;

/**
 * @author Árni
 */
public class Locations extends Controller {

    // will be used when updating Story with new locations
    public ObjectNode createLocationFromDto(LocationDto locationDto) {
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
}
