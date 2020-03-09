package org.lamisplus.base.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "lab_test_group", schema = "public", catalog = "lamisplus")
public class LabTestGroup {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    @Column(name = "category")
    private String category;

    @JsonIgnore
    @OneToMany(mappedBy = "labTestGroupByLabTestCategoryId")
    private Collection<LabTest> labTestsById;
}
