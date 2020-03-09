package org.lamisplus.base.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.lamisplus.base.domain.entities.LabTest;


@Data
public class LabTestGroupDTO {

    private Long id;

    private String category;

}
