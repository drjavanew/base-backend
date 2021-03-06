package org.lamisplus.base.domain.dto;

import lombok.Data;

@Data
public class PersonRelativesDTO {
    private String firstName;
    private String lastName;
    private String otherNames;
    private String email;
    private String mobilePhoneNumber;
    private String alternatePhoneNumber;
    private Long relationshipTypeId;
    private String address;
}
