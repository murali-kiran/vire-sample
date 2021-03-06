package com.vire.service;

import com.vire.model.request.PersonalProfileInterestRequest;
import com.vire.model.request.FeedsRequest;
import com.vire.model.response.*;
import com.vire.repository.FileRepository;
import com.vire.repository.FeedsRepository;
import com.vire.utils.Snowflake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FeedsService {

    @Autowired
    Snowflake snowflake;

    @Autowired
    FeedsRepository feedsRepo;

    @Autowired
    FileRepository feedsImageRepo;

    @Autowired
    CommentService commentService;
    @Autowired
    CommentReplyService commentReplyService;
    @Autowired
    LikesService likesService;
    @Autowired
    ProfileService profileService;
    @Autowired
    FeedsSendToService feedsSendToService;
    public FeedsResponse createFeeds(final FeedsRequest request) {

        var dto = request.toDto();
        dto.setFeedId(snowflake.nextId());

        if (!CollectionUtils.isEmpty(dto.getFeedsSendTo())) {
            for (var sendToDto : dto.getFeedsSendTo()) {
                sendToDto.setFeedsSendToId(snowflake.nextId());
            }
        }

        return FeedsResponse.fromDto(feedsRepo.createFeeds(dto));
    }

    public FeedsResponse updateFeeds(final FeedsRequest request) {

        var dto = request.toDto();

        return FeedsResponse.fromDto(feedsRepo.updateFeeds(dto));
    }

    public FeedsResponse deleteFeedsPost(final Long feedsId) {

        return feedsRepo.deleteFeedsPost(feedsId)
                .map(dto -> FeedsResponse.fromDto(dto))
                .get();
    }

    public List<FeedsResponse> getFeeds() {

        return feedsRepo.getAllFeeds().stream()
                .map(dto -> FeedsResponse.fromDto(dto))
                .collect(Collectors.toList());

    }

    public FeedsResponse retrieveById(Long feedsId) {

        return feedsRepo.retrieveById(feedsId)
                .map(dto -> FeedsResponse.fromDto(dto))
                .get();
    }
    public FeedsResponse retrieveFeedsDetailsById(Long feedsId) {
        FeedsResponse feedsResponse =  feedsRepo.retrieveById(feedsId)
                .map(dto -> FeedsResponse.fromDto(dto))
                .get();
       /* List<CommentResponse> commentsList = commentService.searchComments("feedsId:"+feedsId);
        List<CommentReplyResponse> commentReplyList = commentReplyService.searchReplies("feedsId:"+feedsId);
        List<LikesResponse> likesList = likesService.searchLikes("feedsId:"+feedsId);
        feedsPostResponse.setComments(commentsList);
        feedsPostResponse.setCommentsReply(commentReplyList);
        feedsPostResponse.setLikes(likesList);*/

        return feedsResponse;
    }

    public List<FeedsResponse> search(final String searchString) {

        return feedsRepo.search(searchString).stream()
                .map(dto -> FeedsResponse.fromDto(dto))
                .collect(Collectors.toList());
    }

    public List<FeedsResponse> retrievePostsByProfileId(Long profileId) {

        PersonalResponse personalResponse = profileService.retrievePersonalProfileById(profileId).get();
        StringBuilder searchString = new StringBuilder();
        if(personalResponse != null){
           List<PersonalProfileInterestResponse> personalProfileInterestResponses = personalResponse.getPersonalProfile().getInterests();

            for (PersonalProfileInterestResponse personalProfileInterestResponse : personalProfileInterestResponses) {
                if(searchString.length() != 0) {
                    searchString.append(" OR ");
                }else{
                    searchString.append(" type:")
                            .append("INTERESTS")
                            .append(" AND ( ");
                }
                searchString.append("( value:"+ personalProfileInterestResponse.getInterest()+" )");

            }
            searchString.append(" )");
        }

        String designation = personalResponse.getPersonalProfile().getDesignation();
        String fieldProfessionBusiness = personalResponse.getPersonalProfile().getFieldProfessionBusiness();
        String location = personalResponse.getPersonalProfile().getPresentAddress().getCityTownVillage();
        searchString.append(" AND type:DESIGNATION AND ( value:"+designation+ " )");
        searchString.append(" AND type:FPB AND ( value:"+fieldProfessionBusiness+ " )");
        searchString.append(" AND type:LOCATION AND ( value:"+location+ " )");
        log.info("Search String::::"+searchString.toString());
        List<String> uniqueFeeds = feedsSendToService.searchSent(searchString.toString()).stream()
                .map(FeedsSendToResponse::getFeedId)
                .distinct()
                .collect(Collectors.toList());
        List<FeedsResponse> feedsResponses = new ArrayList<>();
        for (String feedsId : uniqueFeeds) {
            feedsResponses.add(retrieveFeedsDetailsById(Long.valueOf(feedsId)));
        }
        return feedsResponses;
    }
}
