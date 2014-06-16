package models;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.dto.LocationDto;
import models.dto.StoryDto;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Story extends Model {

    @Id
    public Long id;

    @Constraints.MaxLength(1024)
    public String excerpt;

    @Constraints.Required
    public String storyText;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    public List<Location> locations;

    private static Finder<Long, Story> finder = new Finder<Long, Story>(Long.class, Story.class);

    static Story findById(Long id) {
        return finder.byId(id);
    }

    static List<Story> findByLocation(Location location, double distance) {

        List<Story> stories = new ArrayList<>();
        List<Location> locationList = Location.allLocations();
        for (Location loc : locationList) {
            if (loc.distanceTo(location) < distance) {
                stories.add(loc.story);
            }
        }
        return stories;
    }

    public static ObjectNode createStoryFromDto(StoryDto storyDto) {
        ObjectNode result = Json.newObject();
        Ebean.beginTransaction();
        Story story = new Story();

        try {
            story.excerpt = storyDto.excerpt;
            story.storyText = storyDto.storyText;
            List<Location> locations = new ArrayList<>();
            for (LocationDto loc : storyDto.locations) {
                Location location = new Location();
                location.longitude = loc.longitude;
                location.latitude = loc.latitude;
                location.name = loc.name;
                location.story = story;
                locations.add(location);
            }
            story.locations = locations;
            story.save();
            Ebean.save(story.locations);

            Ebean.commitTransaction();

            storyDto.id = story.id;
            result.put("success", Json.toJson(storyDto));
            return result;
        } catch (Exception ex) {
            result.put("error", "Could not create story. " + ex.getMessage());
            return result;
        } finally {
            Ebean.endTransaction();
        }
    }
}
