package com.vire.controller;

import com.vire.constant.VireConstants;
import com.vire.model.request.UpdateEmailRequest;
import com.vire.model.request.UpdatePasswordRequest;
import com.vire.model.response.PageWiseSearchResponse;
import com.vire.model.response.ProfileResponse;
import com.vire.model.response.UpdateEmailResponse;
import com.vire.model.response.UpdatePasswordResponse;
import com.vire.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping(VireConstants.PROFILE_PATH_API)
@CrossOrigin
public class ProfileController {

    @Autowired
    ProfileService profileService;

    @Autowired
    ProfileThumbsDownService profileThumbsDownService;

    @Autowired
    ProfileThumbsUpService profileThumbsUpService;

    @Autowired
    ProfileFollowersService profileFollowersService;

    @Autowired
    ExperienceLikesService experienceLikesService;

    @Operation(summary = "Retrieve profile by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieve Profile by ID Successful",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfileResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Retrieve Profile by ID Failed",
                    content = @Content)})
    @GetMapping("/{profileid}")
    public ResponseEntity<?> retrieveProfileById(@PathVariable(name = "profileid") Long profileId) {
        var response = profileService.retrieveProfileById(profileId);


        return response
                .stream()
                .map(profileResponse -> {
                    var thumbsDownCount = profileThumbsDownService.getThumbsDownCountOfProfile(profileId);
                    var thumbsUpCount = profileThumbsUpService.getThumbsUpCountOfProfile(profileId);
                    var friendsCount = profileFollowersService.getFriendCountOfProfile(profileId);
                    var starsCount = experienceLikesService.getLikesCountOfProfile(profileId);
                    profileResponse.setThumbsDownCount(thumbsDownCount);
                    profileResponse.setThumbsUpCount(thumbsUpCount);
                    profileResponse.setFriendsCount(friendsCount);
                    profileResponse.setStarsCount(starsCount);
                    return new ResponseEntity<>(profileResponse, HttpStatus.OK);
                })
                .findFirst()
                .orElse(new ResponseEntity(HttpStatus.UNAUTHORIZED));
    }
    @Operation(summary = "Update profile setting by profile ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update profile setting by profile ID Successful",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfileResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Update profile setting by profile ID Failed",
                    content = @Content)})
    @PutMapping("/{profileid}/{isprivate}/{showfriends}")
    public ResponseEntity<?> updateProfileSettingByProfileId(@PathVariable(name = "profileid") Long profileId,
                                                 @PathVariable(name = "isprivate") Boolean isPrivate,
                                                 @PathVariable(name = "showfriends") Boolean showFriends) {
        return new ResponseEntity<>(profileService.updateProfileSettingByProfileId(profileId, isPrivate, showFriends), HttpStatus.CREATED);
    }
    /*@Operation(summary = "Retrieve friends of specific profileID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieve Profile by ID Successful",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfileResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Retrieve of friends by Profile ID Failed",
                    content = @Content)})
    @GetMapping("/{profileid}")
    public ResponseEntity<?> retrieveFriendsofSpecificProfileById(@PathVariable(name = "profileid") Long profileId) {

        profileFollowersService.getFriendsOfProfile(profileId);
        return null;
    }*/



   /* @Operation(summary = "Search Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Seach Profile Successful",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfileResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Search Profile Failed",
                    content = @Content) })
    @GetMapping("search")
    public ResponseEntity<List<ProfileResponse>> search(
            @RequestParam(value = "search") String searchString) {
        return new ResponseEntity<>(profileService.searchProfiles(searchString), HttpStatus.OK);
    }*/


    @Operation(summary = "update password by phone number or email id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated password",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdatePasswordResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Failed update password",
                    content = @Content) })
    @PutMapping("/changePassword")
    public ResponseEntity<UpdatePasswordResponse> updatePassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest){
        var response = profileService.updatePassword(updatePasswordRequest);

        return response
                .stream()
                .map(updatePasswordResponse -> new ResponseEntity<>(updatePasswordResponse, HttpStatus.OK))
                .findFirst()
                .orElse(new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR));

    }

    @Operation(summary = "update to new email by phone number or email id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated email",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateEmailResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Failed update email",
                    content = @Content) })
    @PutMapping("/updateEmail")
    public ResponseEntity<UpdateEmailResponse> updateEmail(@Valid @RequestBody UpdateEmailRequest updateEmailRequest){
        var response = profileService.updateEmail(updateEmailRequest);

        return response
                .stream()
                .map(updateEmailResponse -> new ResponseEntity<>(updateEmailResponse, HttpStatus.OK))
                .findFirst()
                .orElse(new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR));

    }

    @Operation(summary = "Search Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Seach Profile Successful",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfileResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Search Profile Failed",
                    content = @Content) })
    @GetMapping("/search")
    public ResponseEntity<List<ProfileResponse>> searchProfiles(@RequestParam(value = "search") String searchString) {
        return new ResponseEntity<>(profileService.searchAnyProfiles(searchString), HttpStatus.OK);
    }

    @Operation(summary = "Search Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search Profile Successful",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfileResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Search Profile Failed",
                    content = @Content) })
    @GetMapping("/searchPageWise")
    public ResponseEntity<PageWiseSearchResponse<ProfileResponse>> searchProfilesPageWise(@RequestParam(value = "search") String searchString,
                                                                                          @RequestParam(value = "page", defaultValue = "1", required = false) Integer pageNumber,
                                                                                          @RequestParam(value = "size", defaultValue = "3", required = false) Integer pageSize) {
        return new ResponseEntity<>(profileService.searchAnyProfilesPaged(searchString,pageNumber,pageSize), HttpStatus.OK);
    }

}
