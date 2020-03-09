package org.lamisplus.base.domain.dto;

import lombok.Data;

@Data
public class ClinicianPatientDTO {

    private Long id;

    private Long visitId;

    private Long patientId;

    private Long clinicanId;
}
