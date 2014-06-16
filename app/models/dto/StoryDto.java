package models.dto;

import java.util.List;

public class StoryDto {
    public Long id;
    public String excerpt;
    public String storyText;
    public String title;
    public List<LocationDto> locations;
}
