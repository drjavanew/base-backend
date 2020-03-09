package org.lamisplus.base.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.lamisplus.base.domain.entities.PersonRelative;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public class PatientDTO {
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dateRegistration;
    private String hospitalNumber;
    private Byte archive;
    private String firstName;
    private String lastName;
    private String otherNames;
    private Long maritalStatusId;
    private Long titleId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dob;
    private Boolean dobEstimated;
    private Long genderId;
    private Long educationId;
    private Long occupationId;
    private Long facilityId;
    private String mobilePhoneNumber;
    private String alternatePhoneNumber;
    private String email;
    private String zipCode;
    private String city;
    private String street;
    private String landmark;
    private Long countryId;
    private Long stateId;
    private Long provinceId;
    private List<PersonRelativesDTO> personRelativeDTOList;
}
