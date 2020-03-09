package org.lamisplus.base.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
@Data
@EqualsAndHashCode
public class Drug {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "brand_name")
    private String brandName;

    @Basic
    @Column(name = "generic_name")
    private String genericName;

    @Basic
    @Column(name = "strength")
    private String strength;

    @Basic
    @Column(name = "pack_size")
    private Integer packSize;

    @Basic
    @Column(name = "dosage_form")
    private String dosageForm;

    @Basic
    @Column(name = "abbrev")
    private String abbrev;

}
