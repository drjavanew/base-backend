package org.lamisplus.base.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.lamisplus.base.domain.entities.ApplicationCodeset;
import org.lamisplus.base.domain.entities.Patient;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalTime;


@Data
public class VisitDTO {

    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dateVisitEnd;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dateVisitStart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "K:mm a")
    private LocalTime timeVisitEnd;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "K:mm a")
    private LocalTime timeVisitStart;

    private LocalDate dateNextAppointment;

    private Long patientId;

    private Long visitTypeId;

    //PATIENT PERSONAL DETAILS
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dateRegistration;
    private String hospitalNumber;
    private String firstName;
    private String lastName;
    private String otherNames;
    private Long maritalStatusId;
    private Long titleId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dob;
    private Long genderId;
    private Long educationId;
    private Long occupationId;


}
