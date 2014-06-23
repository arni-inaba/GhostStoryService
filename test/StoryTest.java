import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.dto.StoryDto;
import org.junit.*;

import play.api.mvc.Request;
import play.libs.WS;
import play.mvc.*;
import play.test.*;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * @author Árni
 */
public class StoryTest {

    public Long storyId;

    @Before
    public void init() {

    }

    private ObjectNode createLocation(double lon, double lat, String name, Long storyId) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode location = mapper.createObjectNode();
        location.put("longitude", lon);
        location.put("latitude", lat);
        location.put("name", name);
        location.put("storyId", storyId);
        return location;
    }

    @Test
    public void postStory() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode story = mapper.createObjectNode();
        ArrayNode locationsArray = mapper.createArrayNode();

        story.put("excerpt", "Test2 story");
        story.put("storyText", "Twice upon a time there was a test");

        locationsArray.add(createLocation(65.22454, 24.51231, "Breiðholt", null));
        locationsArray.add(createLocation(65.153, 24.15315, "Gleiðholt", null));
        story.put("locations", locationsArray);

        WS.Response response = WS.url("http://localhost:9000/api/v1/stories/").post(story).get(5000);
        JsonNode result = response.asJson();
        String error = result.get("error") != null ? result.get("error").textValue() : "";
        assertEquals(error, Helpers.OK, response.getStatus());

        try {
            StoryDto returnedStory = mapper.readValue(result.get("success").toString(), StoryDto.class);
            storyId = returnedStory.id;
            if (storyId == null) {
                error += " Got no storyId";
            }
            if (returnedStory == null) {
                error += " Story creation failed";
            }

        } catch (IOException e) {
            error = e.getMessage();
        }
        System.out.println(storyId);
        assertTrue(error,error.isEmpty());
    }

    @After
    public void teardown() {
        WS.url("http://localhost:9000/api/v1/stories/" + storyId).delete().get(5000);
    }
}
