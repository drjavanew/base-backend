package org.lamisplus.base.repositories;

import org.lamisplus.base.domain.entities.LabTestGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabTestGroupRepository extends JpaRepository<LabTestGroup, Long> {
}
