package com.vire.model.response;

import com.vire.dto.SocialCallRequestDto;
import lombok.Data;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class SocialCallRequestResponse {

    private String socialCallRequestId;
    
    private Long profileId;
    private Long requesterProfileId;
    private String status;
    private Long createdTime;
    private Long updatedTime;

    public static SocialCallRequestResponse fromDto(final SocialCallRequestDto dto) {

        var socialCallRequest = new SocialCallRequestResponse();

        socialCallRequest.setSocialCallRequestId(String.valueOf(dto.getSocialCallRequestId()));
        socialCallRequest.setProfileId(dto.getProfileId());
        socialCallRequest.setRequesterProfileId(dto.getRequesterProfileId());
        socialCallRequest.setStatus(dto.getStatus());
        socialCallRequest.setCreatedTime(dto.getCreatedTime());
        socialCallRequest.setUpdatedTime(dto.getUpdatedTime());

        return socialCallRequest;
    }
}
