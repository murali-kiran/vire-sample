package com.vire.dto;

import lombok.Data;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;


@Data
public class FeedLikesDto {
    private Long feedLikesId;
    private Long likerProfileId;
    private Long feedId;
    public Long createdTime;
    public Long updatedTime;
}
