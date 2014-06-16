package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

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
}
