package org.lamisplus.base.repositories;

import org.lamisplus.base.domain.entities.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    List<Visit> findByPatientId(Long patient_Id);
    Optional<Visit> findByPatientIdAndDateVisitStart(Long Patient_id, LocalDate DateVisitStart);
    List<Visit> findByDateVisitStart(LocalDate DateVisitStart);
    List<Visit> findByDateVisitStartOrderByVisitTypeIdDesc(LocalDate DateVisitStart);

}
