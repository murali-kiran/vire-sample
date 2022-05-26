package com.vire.service;

import com.vire.model.request.ProfileThumbsUpRequest;
import com.vire.model.response.ProfileThumbsUpResponse;
import com.vire.repository.ProfileThumbsUpRepository;
import com.vire.utils.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfileThumbsUpService {

  @Autowired
  Snowflake snowflake;

  @Autowired
  ProfileThumbsUpRepository profileThumbsUpRepository;

  public ProfileThumbsUpResponse create(final ProfileThumbsUpRequest request) {

    var dto = request.toDto(snowflake);

    return ProfileThumbsUpResponse.fromDto(profileThumbsUpRepository.create(dto));
  }

  public ProfileThumbsUpResponse update(final ProfileThumbsUpRequest request) {

    var dto = request.toDto();

    return ProfileThumbsUpResponse.fromDto(profileThumbsUpRepository.update(dto));
  }

  public ProfileThumbsUpResponse delete(final Long profileThumbsUpId) {

    return profileThumbsUpRepository.delete(profileThumbsUpId)
            .map(dto -> ProfileThumbsUpResponse.fromDto(dto))
            .get();
  }

  public List<ProfileThumbsUpResponse> getAll() {

    return profileThumbsUpRepository
            .getAll()
            .stream()
            .map(dto -> ProfileThumbsUpResponse.fromDto(dto))
            .collect(Collectors.toList());
  }

  public ProfileThumbsUpResponse retrieveById(Long profileThumbsUpId) {

    return profileThumbsUpRepository
            .retrieveById(profileThumbsUpId)
            .map(dto -> ProfileThumbsUpResponse.fromDto(dto))
            .get();
  }

  public List<ProfileThumbsUpResponse> search(final String searchString) {

    return profileThumbsUpRepository
            .search(searchString)
            .stream()
            .map(dto -> ProfileThumbsUpResponse.fromDto(dto))
            .collect(Collectors.toList());
  }
}