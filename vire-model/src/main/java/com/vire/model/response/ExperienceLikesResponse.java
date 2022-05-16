package com.vire.model.response;

import com.vire.dto.ExperienceLikesDto;
import lombok.Data;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ExperienceLikesResponse {

    private String experienceLikesId;
    
    private String likerProfileId;
    private String experienceId;
    private Long createdTime;
    private Long updatedTime;

    public static ExperienceLikesResponse fromDto(final ExperienceLikesDto dto) {

        var experienceLikes = new ExperienceLikesResponse();

        experienceLikes.setExperienceLikesId(String.valueOf(dto.getExperienceLikesId()));
        experienceLikes.setLikerProfileId(String.valueOf(dto.getLikerProfileId()));
        experienceLikes.setExperienceId(String.valueOf(dto.getExperienceId()));
        experienceLikes.setCreatedTime(dto.getCreatedTime());
        experienceLikes.setUpdatedTime(dto.getUpdatedTime());

        return experienceLikes;
    }
}