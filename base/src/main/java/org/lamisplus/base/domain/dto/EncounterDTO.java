package org.lamisplus.base.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.lamisplus.base.domain.entities.JsonBEntity;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class EncounterDTO implements Serializable {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Long encounterId;
    @Type(type = "jsonb")
    private Object formData;

    private Long patientId;

    private Long visitId;

    private String formName;

    private String serviceName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dateEncounter;

    private String firstName;
    private String lastName;
    private String otherNames;
    private LocalDate dob;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "K:mm a")
    private LocalTime timeCreated;

    private Long clinicanId;



}
