package org.lamisplus.base.repositories;

import org.lamisplus.base.domain.entities.ClinicianPatient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClinicianPatientRepository extends JpaRepository<ClinicianPatient, Long> {
    Optional<ClinicianPatient> findByClinicianIdAndPatientIdAndVisitId(Long clinicianId, Long patientId, Long visitId);
    Optional<ClinicianPatient> findByVisitId(Long visitId);
}
