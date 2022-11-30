package com.vire.service;

import com.vire.dao.view.ProfileViewDao;
import com.vire.dto.ExperienceViewsCountDto;
import com.vire.model.request.ExperienceFilterRequest;
import com.vire.model.request.ExperienceRequest;
import com.vire.model.response.*;
import com.vire.repository.ExperienceRepository;
import com.vire.repository.ExperienceViewsCountRepository;
import com.vire.repository.view.ProfileViewRepositoryJpa;
import com.vire.utils.Snowflake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExperienceService {

  @Autowired
  Snowflake snowflake;

  @Autowired
  ExperienceRepository experienceRepository;
  @Autowired
  ProfileService profileService;
  @Autowired
  ProfileViewRepositoryJpa profileViewRepositoryJpa;
  @Autowired
  MasterService masterService;
  @Autowired
  ExperienceCommentService experienceCommentService;
  @Autowired
  ExperienceCommentReplyService experienceCommentReplyService;
  @Autowired
  ExperienceLikesService experienceLikesService;
  @Autowired
  ExperienceViewsCountRepository experienceViewsCountRepository;


  public ExperienceResponse create(final ExperienceRequest request) {

    var dto = request.toDto(snowflake);

    return ExperienceResponse.fromDto(experienceRepository.create(dto));
  }

  public ExperienceResponse update(final ExperienceRequest request) {

    var dto = request.toDto();

    return ExperienceResponse.fromDto(experienceRepository.update(dto));
  }

  /*public ExperienceResponse delete(final Long experienceId) {

    return experienceRepository.delete(experienceId)
            .map(dto -> ExperienceResponse.fromDto(dto))
            .get();
  }*/
  public ExperienceResponse updateDeletedTime(final Long experienceId) {

    return experienceRepository.delete(experienceId)
            .map(dto -> ExperienceResponse.fromDto(dto))
            .get();
  }
  public List<ExperienceResponse> getAll() {

    return experienceRepository
            .getAll()
            .stream()
            .map(dto -> ExperienceResponse.fromDto(dto))
            .collect(Collectors.toList());
  }
  public List<ExperienceResponse> getAllExceptDelete() {

    return experienceRepository
            .getAllExceptDelete()
            .stream()
            .map(dto -> ExperienceResponse.fromDto(dto))
            .collect(Collectors.toList());
  }
  public ExperienceResponse retrieveById(Long experienceId) {

    return experienceRepository
            .retrieveById(experienceId)
            .map(dto -> ExperienceResponse.fromDto(dto))
            .get();
  }

  public List<ExperienceResponse> search(final String searchString) {

    return experienceRepository
            .search(searchString)
            .stream()
            .map(dto -> ExperienceResponse.fromDto(dto))
            .collect(Collectors.toList());
  }

    public List<ExperienceDetailResponse> retrieveAllByProfile(Long profileId) {
      //Pageable paging = PageRequest.of();
      List<ExperienceDetailResponse> experienceDetailResponseList = experienceRepository
              .getAllExceptDelete()
              .stream()
              .map(dto -> ExperienceDetailResponse.fromDto(dto))
              .collect(Collectors.toList());
      for (ExperienceDetailResponse experienceDetailResponse : experienceDetailResponseList) {
        setExperienceDetails(experienceDetailResponse, false, profileId);
        experienceDetailResponse.setMinimalProfileResponse(profileService.retrieveProfileDtoById(Long.valueOf(experienceDetailResponse.getProfileId())));
      }
      return experienceDetailResponseList;
  /*List<String> categoryFiltersToBeApplied = new ArrayList<>();
  var experienceDetailResponses = experienceRepository
          .getExperienceListByProfile(Long.valueOf(profileId), 1, 50, categoryFiltersToBeApplied)
          .stream()
          .map(dao -> dao.toDto())
          .map(dto -> ExperienceDetailResponse.fromDto(dto))
          .collect(Collectors.toList());
      for (ExperienceDetailResponse experienceDetailResponse : experienceDetailResponses) {
        setExperienceDetails(experienceDetailResponse, false, profileId);
        experienceDetailResponse.setMinimalProfileResponse(profileService.retrieveProfileDtoById(Long.valueOf(experienceDetailResponse.getProfileId())));
      }
      return experienceDetailResponses;*/
    }

  public List<ExperienceDetailResponse> retrieveAllByFilter(ExperienceFilterRequest request) {
    if(request != null && request.getCategoryList() != null) {
      List<ExperienceDetailResponse> experienceDetailResponseList = experienceRepository
              .retreveByFilterList(request.getCategoryList())
              .stream()
              .map(dto -> ExperienceDetailResponse.fromDto(dto))
              .collect(Collectors.toList());
      for (ExperienceDetailResponse experienceDetailResponse : experienceDetailResponseList) {
        setExperienceDetails(experienceDetailResponse, false, Long.valueOf(request.getProfileId()));
        experienceDetailResponse.setMinimalProfileResponse(profileService.retrieveProfileDtoById(Long.valueOf(experienceDetailResponse.getProfileId())));
      }
      return experienceDetailResponseList;
    }else{
      if(request.getProfileId() != null)
        return retrieveAllByProfile(Long.valueOf(request.getProfileId()));
      else
        return new ArrayList<>();
    }

  }
  public List<ExperienceDetailResponse> retrieveAllCreatedByProfile(String profileId) {
    List<ExperienceDetailResponse> experienceDetailResponses = experienceRepository
            .search("profileId:"+profileId)
            .stream()
            .map(dto -> ExperienceDetailResponse.fromDto(dto))
            .collect(Collectors.toList());
    for (ExperienceDetailResponse experienceDetailResponse : experienceDetailResponses) {
      setExperienceDetails(experienceDetailResponse, false, Long.valueOf(profileId));
      experienceDetailResponse.setMinimalProfileResponse(profileService.retrieveProfileDtoById(Long.valueOf(experienceDetailResponse.getProfileId())));
    }
    return experienceDetailResponses;
  }

  public ExperienceDetailResponse retrieveExperienceDetailsById(Long experienceId, Long profileId) {
    log.info("Experience ID###:"+experienceId);
    var experienceDetailDto = experienceRepository.retrieveById(experienceId);

    if(experienceDetailDto != null && experienceDetailDto.get() != null){
      var experienceDetailResponse = ExperienceDetailResponse.fromDto(experienceDetailDto.get());
      setExperienceDetails(experienceDetailResponse, true, profileId);
      return experienceDetailResponse;
    }else {
      throw new RuntimeException("No Experiment found with id:"+experienceId);
    }

  }

  private ExperienceDetailResponse setExperienceDetails(ExperienceDetailResponse experienceDetailResponse, Boolean setViewCount, Long profileId){

    List<ExperienceCommentResponse> experienceCommentsList = experienceCommentService.search("experienceId:" + experienceDetailResponse.getExperienceId());
    List<ExperienceLikesResponse> likesList = experienceLikesService.search("experienceId:" + experienceDetailResponse.getExperienceId());
    if(setViewCount) {
      var experienceDto = experienceViewsCountRepository.retrieveByProfileIdExperienceId(Long.valueOf(experienceDetailResponse.getExperienceId()), profileId);
      if(experienceDto.isEmpty()) {
        ExperienceViewsCountDto experienceViewsCountDto = new ExperienceViewsCountDto();
        experienceViewsCountDto.setExperienceId(Long.valueOf(experienceDetailResponse.getExperienceId()));
        experienceViewsCountDto.setProfileId(profileId);
        experienceViewsCountDto.setViewsCountId(snowflake.nextId());
        experienceViewsCountRepository.create(experienceViewsCountDto);
      }
      experienceDetailResponse.setCommentResponseList(experienceCommentsList);
      experienceDetailResponse.setLikesResponseList(likesList);
    }
    List<String> profileIds = likesList == null ? null : likesList.stream().map(ExperienceLikesResponse::getLikerProfileId).collect(Collectors.toList());
    experienceDetailResponse.setLoginUserLiked((profileIds != null && profileIds.contains(profileId+"")) ? true : false);
    experienceDetailResponse.setLikesCount( likesList == null ? 0 : likesList.size() );
    Long viewsCount = experienceViewsCountRepository.countByExperienceId(Long.valueOf(experienceDetailResponse.getExperienceId()));
    MasterResponse experienceCategoryResponse = masterService.retrieveById(Long.valueOf(experienceDetailResponse.getCategoryId()));
    experienceDetailResponse.setCategoryResponse(experienceCategoryResponse);
    List<ExperienceCommentReplyResponse> replyList = experienceCommentReplyService.search("experienceId:" + experienceDetailResponse.getExperienceId());
    DateFormat sdf2 = new SimpleDateFormat("MMMM dd 'at' HH:mm");
    sdf2.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
    experienceDetailResponse.setCreatedTimeStr(sdf2.format(new Date(experienceDetailResponse.getCreatedTime())));
    MinimalProfileResponse minimalProfileResponse = new MinimalProfileResponse();
    minimalProfileResponse.setProfileId(experienceDetailResponse.getProfileId());
    experienceDetailResponse.setMinimalProfileResponse(minimalProfileResponse);
    experienceDetailResponse.getMinimalProfileResponse().cloneProperties(
            profileService.retrieveProfileDtoById(
                    Long.valueOf(experienceDetailResponse.getProfileId())));
    experienceDetailResponse.setCommentsCount(( experienceCommentsList != null ? experienceCommentsList.size() : 0 ) + (replyList != null ? replyList.size() : 0));
    experienceDetailResponse.setViewsCount(viewsCount);
    return experienceDetailResponse;
  }
  
  

  public PageWiseSearchResponse<ExperienceDetailResponse> getAllPaged(Integer pageNumber, Integer pageSize) {
    var pageWiseExperienceResponse = experienceRepository.getAllPaged(pageNumber,pageSize);

    List<ExperienceDetailResponse> experienceDetailResponses = pageWiseExperienceResponse.getList()
            .parallelStream()
            .map(dto -> ExperienceDetailResponse.fromDto(dto))
            .collect(Collectors.toList());

    for (ExperienceDetailResponse experienceDetailResponse : experienceDetailResponses) {

      Long viewsCount = experienceViewsCountRepository.countByExperienceId(Long.valueOf(experienceDetailResponse.getExperienceId()));
      List<ExperienceCommentResponse> experienceCommentsList = experienceCommentService.search("experienceId:" + experienceDetailResponse.getExperienceId());
      List<ExperienceLikesResponse> likesList = experienceLikesService.search("experienceId:" + experienceDetailResponse.getExperienceId());
      List<ExperienceCommentReplyResponse> replyList = experienceCommentReplyService.search("experienceId:" + experienceDetailResponse.getExperienceId());
      experienceDetailResponse.setCommentsCount(( experienceCommentsList != null ? experienceCommentsList.size() : 0 ) + (replyList != null ? replyList.size() : 0));
      experienceDetailResponse.setViewsCount(viewsCount);
      experienceDetailResponse.setLikesCount( likesList == null ? 0 : likesList.size() );



      Optional<ProfileViewDao> profileViewDao = profileViewRepositoryJpa.findByProfileId(Long.valueOf(experienceDetailResponse.getProfileId()));
      if(profileViewDao.isPresent()){
        experienceDetailResponse.setMinimalProfileResponse(MinimalProfileResponse.fromDto(profileViewDao.get().toDto()));
      }

      experienceDetailResponse.setCategoryResponse(masterService.retrieveById(Long.valueOf(experienceDetailResponse.getCategoryId())));
    }

    PageWiseSearchResponse<ExperienceDetailResponse> response = new PageWiseSearchResponse<>();
    response.setList(experienceDetailResponses);
    response.setPageCount(pageWiseExperienceResponse.getPageCount());
    return response;
  }


}
