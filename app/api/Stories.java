package api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Location;
import models.Story;
import models.dto.StoryDto;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.util.List;

public class Stories extends Controller {
    public static Result listOfAllStories() {
        return TODO;
    }

    public static Result getStory(Long id) {
        ObjectNode result = Json.newObject();
        Story story = Story.findById(id);
        if (story != null) {
            result.put("success", Json.toJson(story));
            return ok(result);
        } else {
            result.put("error", "Couldn't find story with id: " + id);
            return badRequest(result);
        }
    }

    public static Result getStoriesByLocation(double longitude, double latitude, double distance) {

        ObjectNode result = Json.newObject();
        Location location = new Location();
        location.longitude = longitude;
        location.latitude = latitude;
        List<Story> stories = Story.findByLocation(location, distance);
        if (!stories.isEmpty()) {
            result.put("success", Json.toJson(stories));
            return ok(result);
        } else {
            result.put("error", "No stories found within range of + " + distance + "km.");
            return ok(result);
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result doCreate() {
        Http.Request request = request();
        Form<StoryDto> filledForm = Form.form(StoryDto.class).bind(request.body().asJson());
        ObjectNode result = Json.newObject();

        if (filledForm.hasErrors()) {
            result.put("error", filledForm.errorsAsJson());
            return badRequest(result);
        }
        StoryDto storyDto = filledForm.get();
        ObjectNode story = Story.createStoryFromDto(storyDto);
        if (story.get("error") == null) {
            return ok(story);
        }
        return badRequest(story);
    }
}
