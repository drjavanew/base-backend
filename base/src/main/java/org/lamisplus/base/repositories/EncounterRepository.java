package org.lamisplus.base.repositories;

import org.lamisplus.base.domain.entities.Encounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository

//EncounterRepository
public interface EncounterRepository extends JpaRepository<Encounter, Long> {
    //Encounter
    Optional<Encounter> findByPatientIdAndServiceNameAndFormNameAndVisitId(Long patientId, String serviceName, String formName, Long visitId);

    Optional<Encounter> findByPatientIdAndServiceNameAndFormNameAndDateEncounter(Long patientId, String serviceName, String formName, LocalDate dateFncounter);

    List<Encounter> findBypatientId(Long PatientId);

    //List<PatientObservation> findByPatientAndFormCodeTitle(Patient patient, Long formCode, String title);
    Optional<Encounter> findFirstByPatientIdAndServiceNameAndFormNameOrderByDateEncounterDesc(Long patientId, String serviceName, String formName);

    //Optional<Encounter> findByMaxDAndDateEncounter(String formName, Long patientId);
    List<Encounter> findAllByPatientIdAndServiceNameAndFormNameOrderByDateEncounterDesc(Long patientId, String serviceName, String formName);

    List<Encounter> findAllByServiceNameAndFormNameAndDateEncounterOrderByDateEncounterDesc(String serviceName, String formName, LocalDate DateEncounter);

    List<Encounter> findAllByServiceNameAndFormNameAndPatientIdOrderByDateEncounterDesc(String serviceName, String formName, Long patientId);




}

