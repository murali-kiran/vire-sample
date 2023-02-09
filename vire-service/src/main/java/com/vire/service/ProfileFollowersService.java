package com.vire.service;

import com.vire.dto.NotificationType;
import com.vire.dto.ProfileNotificationType;
import com.vire.model.request.ProfileFollowersRequest;
import com.vire.model.response.ProfileFollowersResponse;
import com.vire.repository.ProfileFollowersRepository;
import com.vire.utils.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfileFollowersService {

  @Autowired
  Snowflake snowflake;

  @Autowired
  ProfileFollowersRepository profileFollowersRepository;

  @Autowired
  ProfileService profileService;
  @Autowired
  NotificationService notificationService;

  @Transactional
  public ProfileFollowersResponse create(final ProfileFollowersRequest request) {
      var dto = request.toDto(snowflake);
      ProfileNotificationType profileNotificationType = ProfileNotificationType.FOLLOW_REQUEST;
      if(request.getStatus().equalsIgnoreCase("accepted")){
        profileNotificationType = ProfileNotificationType.FOLLOW_JOINED;
      }
      notificationService.createProfileNotification(NotificationType.PROFILE, request.getFollowerId(), profileNotificationType, request.getProfileId());
      return ProfileFollowersResponse.fromDto(profileFollowersRepository.create(dto));
  }

  @Transactional
  public ProfileFollowersResponse update(final ProfileFollowersRequest request) {

    var dto = request.toDto();
    if(request.getStatus().equalsIgnoreCase("accepted")){
      notificationService.createProfileNotification(NotificationType.PROFILE, request.getProfileId(), ProfileNotificationType.FOLLOW_ACCEPTED, request.getFollowerId());
    }
    return ProfileFollowersResponse.fromDto(profileFollowersRepository.update(dto));
  }

  @Transactional
  public ProfileFollowersResponse updateFollowStatus(final ProfileFollowersRequest request) {
    var dto = request.toDto();
    if(request.getStatus().equalsIgnoreCase("requested")){
      notificationService.createProfileNotification(NotificationType.PROFILE, request.getFollowerId(), ProfileNotificationType.FOLLOW_REQUEST, request.getProfileId());

    }
    if(request.getStatus().equalsIgnoreCase("accepted")) {
      notificationService.createProfileNotification(NotificationType.PROFILE, request.getProfileId(), ProfileNotificationType.FOLLOW_ACCEPTED, request.getFollowerId());
    }
    return ProfileFollowersResponse.fromDto(profileFollowersRepository.updateFollowStatus(dto));
  }
  public ProfileFollowersResponse delete(final Long profileFollowersId) {

    return profileFollowersRepository.delete(profileFollowersId)
            .map(dto -> ProfileFollowersResponse.fromDto(dto))
            .get();
  }

  public List<ProfileFollowersResponse> getAll() {

    return profileFollowersRepository
            .getAll()
            .stream()
            .map(dto -> ProfileFollowersResponse.fromDto(dto))
            .collect(Collectors.toList());
  }

  public ProfileFollowersResponse retrieveById(Long profileFollowersId) {

    return profileFollowersRepository
            .retrieveById(profileFollowersId)
            .map(dto -> ProfileFollowersResponse.fromDto(dto))
            .get();
  }

  public List<ProfileFollowersResponse> search(final String searchString) {

    return profileFollowersRepository
            .search(searchString)
            .stream()
            .map(dto -> profileLoader(ProfileFollowersResponse.fromDto(dto), null))
            .collect(Collectors.toList());
  }

    public long getFriendCountOfProfile(Long profileId) {
      return profileFollowersRepository.getFriendCountOfProfile(profileId,true);
    }

  private ProfileFollowersResponse profileLoader(ProfileFollowersResponse response, String followType) {
    if (followType == null) {
      if (response.getProfile() != null
              && response.getProfile().getProfileId() != null) {
        response.getProfile().cloneProperties(
                profileService.retrieveProfileDtoById(
                        Long.valueOf(response.getProfile().getProfileId())));
      }
      if (response.getFollower() != null
              && response.getFollower().getProfileId() != null) {
        response.getFollower().cloneProperties(
                profileService.retrieveProfileDtoById(
                        Long.valueOf(response.getFollower().getProfileId())));
      }
    }else{
      if("followers".equals(followType)){
        if (response.getFollower() != null
                && response.getFollower().getProfileId() != null) {
          response.getFollower().cloneProperties(
                  profileService.retrieveProfileDtoById(
                          Long.valueOf(response.getFollower().getProfileId())));

        }
      }
      if("following".equals(followType)){
        if (response.getProfile() != null
                && response.getProfile().getProfileId() != null) {
          response.getProfile().cloneProperties(
                  profileService.retrieveProfileDtoById(
                          Long.valueOf(response.getProfile().getProfileId())));

        }
      }
    }
    return response;
  }

  public List<ProfileFollowersResponse> getFollowers(String profileId, String loginProfileId) {
    var loginProfileFollowers = profileFollowersRepository.search("profileId:"+loginProfileId);
    var loginProfileFollowing = profileFollowersRepository.search("followerId:"+loginProfileId);
    return profileFollowersRepository
            .search("profileId:"+profileId)
            .stream()
            .map(dto -> {
              var response = profileLoader(ProfileFollowersResponse.fromDto(dto), "followers");

             // childDao.setExperience(experience);
              boolean isFollower = loginProfileFollowers.stream().filter(f -> f.getFollowerId().equals(dto.getFollowerId())).findFirst().isPresent();
              if(isFollower) {
                response.setLoginProfileFollowStatus("Follower");
              }else{
                boolean isFollowing = loginProfileFollowing.stream().filter(f -> f.getProfileId().equals(dto.getFollowerId())).findFirst().isPresent();
                if(isFollowing) {
                  response.setLoginProfileFollowStatus("Following");
                }else{
                  response.setLoginProfileFollowStatus("Follow Profile");
                }
              }

              return response;
            })
            .collect(Collectors.toList());
  }
  public List<ProfileFollowersResponse> getFollowing(String profileId, String loginProfileId) {
    var loginProfileFollowers = profileFollowersRepository.search("profileId:"+loginProfileId);
    var loginProfileFollowing = profileFollowersRepository.search("followerId:"+loginProfileId);
    return profileFollowersRepository
            .search("followerId:"+profileId)
            .stream()
            .map(dto -> {
              var response = profileLoader(ProfileFollowersResponse.fromDto(dto),  "following");

              // childDao.setExperience(experience);
              boolean isFollower = loginProfileFollowers.stream().filter(f -> f.getFollowerId().equals(dto.getProfileId())).findFirst().isPresent();
              if(isFollower) {
                response.setLoginProfileFollowStatus("Follower");
              }else{
                boolean isFollowing = loginProfileFollowing.stream().filter(f -> f.getProfileId().equals(dto.getProfileId())).findFirst().isPresent();
                if(isFollowing) {
                  response.setLoginProfileFollowStatus("Following");
                }else{
                  response.setLoginProfileFollowStatus("Follow Profile");
                }
              }

              return response;
            }).collect(Collectors.toList());
  }
  /*public void getFriendsOfProfile(Long profileId) {
    return profileFollowersRepository.getFriendsOfProfile(profileId,true);
  }*/
}
