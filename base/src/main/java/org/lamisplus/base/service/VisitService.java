package org.lamisplus.base.service;

import lombok.extern.slf4j.Slf4j;
import org.lamisplus.base.domain.dto.BadRequestAlertException;
import org.lamisplus.base.domain.dto.VisitDTO;
import org.lamisplus.base.domain.entities.Patient;
import org.lamisplus.base.domain.entities.Person;
import org.lamisplus.base.domain.entities.Visit;
import org.lamisplus.base.repositories.ApplicationCodesetRepository;
import org.lamisplus.base.repositories.PatientRepository;
import org.lamisplus.base.repositories.PersonRepository;
import org.lamisplus.base.repositories.VisitRepository;
import org.lamisplus.base.rest.RecordNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
@Transactional
@Slf4j
public class VisitService {

    private static final String ENTITY_NAME = "visit";
    private final ApplicationCodesetRepository AppCodesetRepository;
    private final VisitRepository visitRepository;
    private final PatientRepository patientRepository;
    private final PersonRepository personRepository;

    public VisitService(PatientRepository patientRepository, ApplicationCodesetRepository AppCodesetRepository, VisitRepository visitRepository, PersonRepository personRepository) {

        this.AppCodesetRepository = AppCodesetRepository;
        this.visitRepository = visitRepository;
        this.patientRepository = patientRepository;
        this.personRepository = personRepository;
    }

    private static Object exist(Visit o) {
        throw new BadRequestAlertException("Error - Patient already Checked Id", ENTITY_NAME, "Already Exist");
    }

    public Visit save(Visit visit1) {
        Optional<Visit> visit = this.visitRepository.findByPatientIdAndDateVisitStart(visit1.getPatientId(), visit1.getDateVisitStart());
        visit.map(VisitService::exist);

        Patient patient = patientRepository.getOne(visit1.getPatientId());
        if (patient.getArchive() == 0)
            throw new RecordNotFoundException();

        visit1.setPatient(patient);
        this.visitRepository.save(visit1);
        log.info("SAVING visit... " + visit1);

        return visit1;
    }

    public List<VisitDTO> getVisitByDateStart(LocalDate DateVisitStart) {
        List<Visit> visit = this.visitRepository.findByDateVisitStartOrderByVisitTypeIdDesc(DateVisitStart);
        if (visit.size() < 1)
            throw new RecordNotFoundException();

        List<VisitDTO> visitDTOS = new ArrayList<>();

        //Foreach Loop to retrieve all Visit/check In
        visit.forEach(singleVisit -> {
            Patient patient = patientRepository.getOne(singleVisit.getPatientId());

            //Checking for archived patients and not returning them
            if (patient.getArchive() == 0)
                return;

            VisitDTO visitDTO = new VisitDTO();

            visitDTO.setPatientId(patient.getId());
            visitDTO.setHospitalNumber(patient.getHospitalNumber());
            visitDTO.setDateRegistration(patient.getDateRegistration());
            log.info("Checking Visit patient" + patient.getId());
            log.info("Checking Visit patient Hospital No" + patient.getHospitalNumber());

            Optional<Person> person = this.personRepository.findById(patient.getPersonId());

            Person person1 = person.get();

            //PERSON DETAILS

            visitDTO.setFirstName(person1.getFirstName());
            visitDTO.setLastName(person1.getLastName());
            visitDTO.setDob(person1.getDob());
            visitDTO.setOtherNames(person1.getOtherNames());
            visitDTO.setEducationId(person1.getEducationId());
            visitDTO.setGenderId(person1.getGenderId());
            visitDTO.setOccupationId(person1.getOccupationId());
            visitDTO.setMaritalStatusId(person1.getMaritalStatusId());
            visitDTO.setTitleId(person1.getPersonTitleId());

            visitDTO.setId(singleVisit.getId());
            visitDTO.setDateVisitStart(singleVisit.getDateVisitStart());
            visitDTO.setDateVisitEnd(singleVisit.getDateVisitEnd());

            visitDTOS.add(visitDTO);

        });

        return visitDTOS;

    }

    /*public VisitDTO getSingleVisit(Long id){

        Optional<Visit> visit = visitRepository.findById(id);

        if (!visit.isPresent())
            throw new RecordNotFoundException();

        Visit visit1 = visit.get();

        VisitDTO visitDTO = new VisitDTO();

        visitDTO.setPatientId(visit1.getPatientId());
        visitDTO.setDateVisitStart(visit1.getDateVisitStart());
        visitDTO



    }*/
}
