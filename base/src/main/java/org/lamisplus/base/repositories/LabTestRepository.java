package org.lamisplus.base.repositories;

import org.lamisplus.base.domain.entities.LabTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabTestRepository extends JpaRepository<LabTest, Long> {
    List<LabTest> findAllByLabTestCategoryId(Long labTestCategoryId);
}
