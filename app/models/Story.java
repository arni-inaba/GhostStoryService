package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Story extends Model {

    @Id
    public Long id;

    public String excerpt;
    public String story;

    private static Finder<Long, Story> finder = new Finder<Long, Story>(Long.class, Story.class);

    static Story findbyId(Long id) {
        return finder.byId(id);
    }
}
