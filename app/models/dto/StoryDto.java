package models.dto;

import java.util.List;

/**
 * @author Árni
 */
public class StoryDto {
    public Long id;
    public String excerpt;
    public String storyText;
    public List<LocationDto> locations;
}
