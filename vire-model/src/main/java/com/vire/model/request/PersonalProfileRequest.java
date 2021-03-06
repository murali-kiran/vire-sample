package com.vire.model.request;

import com.vire.dto.PersonalProfileDto;
import com.vire.enumeration.BloodDonateWillingness;
import com.vire.enumeration.WorkStatus;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class PersonalProfileRequest {

    private String personalProfileId;
    private String schoolBoard;
    private String schoolName;
    private String intermediateBoard;
    private String intermediateCollegeName;
    private String graduationBoard;
    private String graduationCollegeName;
    private String postGraduationBoard;
    private String postGraduationCollegeName;
    private WorkStatus workStatus;
    private String fieldProfessionBusiness;
    private String designation;
    private String organizationName;
    private String organizationLocation;
    private String bloodGroup;
    private BloodDonateWillingness bloodDonateWillingness;
    private AddressRequest presentAddress;
    private AddressRequest permanentAddress;
    private List<PersonalProfileInterestRequest> interests;

    public PersonalProfileDto toDto() {
        PersonalProfileDto personalProfileDto = new PersonalProfileDto();
        personalProfileDto.setPersonalProfileId((this.personalProfileId == null  || !StringUtils.isNumeric(this.personalProfileId))? null : Long.valueOf(this.personalProfileId));
        personalProfileDto.setSchoolBoard(this.schoolBoard);
        personalProfileDto.setSchoolName(this.schoolName);
        personalProfileDto.setIntermediateBoard(this.intermediateBoard);
        personalProfileDto.setIntermediateCollegeName(this.intermediateCollegeName);
        personalProfileDto.setGraduationBoard(this.graduationBoard);
        personalProfileDto.setGraduationCollegeName(this.graduationCollegeName);
        personalProfileDto.setPostGraduationBoard(this.postGraduationBoard);
        personalProfileDto.setPostGraduationCollegeName(this.postGraduationCollegeName);
        personalProfileDto.setWorkStatus(this.workStatus);
        personalProfileDto.setFieldProfessionBusiness(this.fieldProfessionBusiness);
        personalProfileDto.setDesignation(this.designation);
        personalProfileDto.setOrganizationName(this.organizationName);
        personalProfileDto.setOrganizationLocation(this.organizationLocation);
        personalProfileDto.setBloodGroup(this.bloodGroup);
        personalProfileDto.setBloodDonateWillingness(this.bloodDonateWillingness);
        personalProfileDto.setPresentAddress(this.presentAddress.toDto());
        personalProfileDto.setPermanentAddress(this.permanentAddress.toDto());

        if(interests!=null) {
            personalProfileDto.setInterests(this.interests.stream().map(interest->interest.toDto()).collect(Collectors.toList()));
        }

        return personalProfileDto;

    }

}
