package org.lamisplus.base.service;

import lombok.extern.slf4j.Slf4j;
import org.lamisplus.base.domain.dto.BadRequestAlertException;
import org.lamisplus.base.domain.dto.EncounterDTO;
import org.lamisplus.base.domain.dto.LabTestDTO;
import org.lamisplus.base.domain.entities.*;
import org.lamisplus.base.repositories.*;
import org.lamisplus.base.rest.RecordNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
@Transactional
@Slf4j
public class EncounterService {
    private static final String ENTITY_NAME = "encounter";
    private final PatientRepository patientRepository;
    private final PersonRepository personRepository;
    private final ServicesRepository servicesRepository;
    private final EncounterRepository encounterRepository;
    private final VisitRepository visitRepository;
    private final FormRepository formRepository;
    private final EncounterRepository patientEncounterRepository;
    private final UserRepository userRepository;
    private final ClinicianPatientRepository clinicianPatientRepository;
    private final ApplicationCodesetRepository applicationCodesetRepository;
    private final LabTestRepository labTestRepository;
    private final DrugRepository drugRepository;
    private final LabTestGroupRepository labTestGroupRepository;


    public EncounterService(DrugRepository drugRepository, LabTestGroupRepository labTestGroupRepository, LabTestRepository labTestRepository, ApplicationCodesetRepository applicationCodesetRepository, ClinicianPatientRepository clinicianPatientRepository, UserRepository userRepository, PersonRepository personRepository, PatientRepository patientRepository, EncounterRepository encounterRepository, VisitRepository visitRepository, FormRepository formRepository, ServicesRepository servicesRepository, EncounterRepository patientEncounterRepository) {
        this.patientRepository = patientRepository;
        this.encounterRepository = encounterRepository;
        this.visitRepository = visitRepository;
        this.formRepository = formRepository;
        this.servicesRepository = servicesRepository;
        this.patientEncounterRepository = patientEncounterRepository;
        this.personRepository = personRepository;
        this.userRepository = userRepository;
        this.clinicianPatientRepository = clinicianPatientRepository;
        this.applicationCodesetRepository = applicationCodesetRepository;
        this.labTestRepository = labTestRepository;
        this.labTestGroupRepository = labTestGroupRepository;
        this.drugRepository = drugRepository;
    }

    private static Object exist(Encounter o) {
        throw new BadRequestAlertException("Encounter  Already Exist", ENTITY_NAME, "id Already Exist");
    }

    private static Encounter notExit() {
        throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "id is null");
    }

    public EncounterDTO save(EncounterDTO encounterDTO) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalTime today = LocalTime.parse(formatter.format(LocalTime.now()), formatter);

        EncounterDTO encounterDTO1 = encounterDTO;
        Encounter encounter = new Encounter();
        try {
            Patient patient1 = this.patientRepository.getOne(encounterDTO.getPatientId());
            if ((patient1 == null || patient1.getArchive() == 0) || patient1.getArchive() == 0)
                throw new RecordNotFoundException();

            Form form = formRepository.findByName(encounterDTO.getFormName());

            Service service = this.servicesRepository.findByServiceName(encounterDTO.getServiceName());

            //Checking is the patient has been checked in by visit id
            Visit visit = this.visitRepository.getOne(encounterDTO.getVisitId());

            //check if an encounter already exist with
            //findBy PatientId And ServiceName And FormName And VisitId
            Optional<Encounter> patientEncounter = this.encounterRepository.findByPatientIdAndServiceNameAndFormNameAndVisitId(patient1.getId(), service.getServiceName(), encounterDTO.getFormName(), visit.getId());
            patientEncounter.map(EncounterService::exist);
            //log.info("SAVING1... " + encounterDTO);

            log.info("GETTING FROM FRONT END... " + encounterDTO);
            //issues may come up with wrong form name
            encounter.setFormName(form.getName());
            encounter.setFormData(encounterDTO.getFormData());

            encounter.setDateEncounter(encounterDTO.getDateEncounter());
            encounter.setTimeCreated(today);
            encounter.setPatientId(patient1.getId());
            encounter.setVisitId(visit.getId());
            encounter.setServiceName(service.getServiceName());
            encounter = this.encounterRepository.save(encounter);
            log.info("SAVING TO BACKEND 12345... " + encounter);

            encounterDTO1.setEncounterId(encounter.getId());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw ex;
        }
        return encounterDTO1;

    }

    //Getting a single encounter for a patient
    public EncounterDTO getByPatientIdServiceNameFormNamelocalDate(Long PatientId, String ServiceName, String FormName, LocalDate localDate) {

        Optional<Encounter> patientEncounter = this.encounterRepository.findByPatientIdAndServiceNameAndFormNameAndDateEncounter(PatientId, ServiceName, FormName, localDate);
        //patientEncounter.map(EncounterService::exist);
        if (!patientEncounter.isPresent())
            throw new RecordNotFoundException();


        Encounter encounter = patientEncounter.get();
        Patient patient = this.patientRepository.findById(encounter.getPatientId()).get();

        if (patient.getArchive() == 0)
            throw new RecordNotFoundException();

        Person person = this.personRepository.findById(patient.getPersonId()).get();

        EncounterDTO encounterDTO1 = new EncounterDTO();

        encounterDTO1.setDateEncounter(encounter.getDateEncounter());
        encounterDTO1.setFormData(encounter.getFormData());
        encounterDTO1.setPatientId(encounter.getPatientId());
        encounterDTO1.setFormName(encounter.getFormName());
        encounterDTO1.setServiceName(encounter.getServiceName());
        encounterDTO1.setVisitId(encounter.getVisitId());
        encounterDTO1.setTimeCreated(encounter.getTimeCreated());

        encounterDTO1.setFirstName(person.getFirstName());
        encounterDTO1.setLastName(person.getLastName());
        encounterDTO1.setDob(person.getDob());
        encounterDTO1.setOtherNames(person.getOtherNames());


        log.info("GETTING encounter 12... " + encounterDTO1);

        return encounterDTO1;

    }

    public List<EncounterDTO> getAll() {
        List<EncounterDTO> encounterDTOS = new ArrayList();
        encounterRepository.findAll().forEach(SingleEncounter -> {

            Patient patient = this.patientRepository.findById(SingleEncounter.getPatientId()).get();

            if (patient.getArchive() == 0)
                return;

            Person person = this.personRepository.findById(patient.getPersonId()).get();
            EncounterDTO encounterDTO = new EncounterDTO();

            encounterDTO.setEncounterId(SingleEncounter.getId());
            encounterDTO.setPatientId(SingleEncounter.getPatientId());
            encounterDTO.setFormName(SingleEncounter.getFormName());
            encounterDTO.setServiceName(SingleEncounter.getServiceName());
            encounterDTO.setVisitId(SingleEncounter.getVisitId());
            encounterDTO.setDateEncounter(SingleEncounter.getDateEncounter());
            encounterDTO.setFormData(SingleEncounter.getFormData());
            encounterDTO.setTimeCreated(SingleEncounter.getTimeCreated());

            encounterDTO.setFirstName(person.getFirstName());
            encounterDTO.setLastName(person.getLastName());
            encounterDTO.setOtherNames(person.getOtherNames());
            encounterDTO.setDob(person.getDob());
            log.info("GETTING encounter in List 12... " + encounterDTO);

            encounterDTOS.add(encounterDTO);


        });

        return encounterDTOS;

    }

    public List<EncounterDTO> getAllByPatientId(Long patientId) {
        List<EncounterDTO> encounterDTOS = new ArrayList();

        List<Encounter> tempEncounter = encounterRepository.findBypatientId(patientId);

        if (tempEncounter.size() < 1)
            throw new RecordNotFoundException();


        tempEncounter.forEach(OnePatientEncounter -> {
            Patient patient = this.patientRepository.findById(OnePatientEncounter.getPatientId()).get();

            if (patient.getArchive() == 0)
                return;

            Person person = this.personRepository.findById(patient.getPersonId()).get();
            EncounterDTO encounterDTO = new EncounterDTO();

            encounterDTO.setEncounterId(OnePatientEncounter.getId());
            encounterDTO.setPatientId(OnePatientEncounter.getPatientId());
            encounterDTO.setFormName(OnePatientEncounter.getFormName());
            encounterDTO.setServiceName(OnePatientEncounter.getServiceName());
            encounterDTO.setVisitId(OnePatientEncounter.getVisitId());
            encounterDTO.setDateEncounter(OnePatientEncounter.getDateEncounter());
            encounterDTO.setFormData(OnePatientEncounter.getFormData());
            encounterDTO.setTimeCreated(OnePatientEncounter.getTimeCreated());

            encounterDTO.setFirstName(person.getFirstName());
            encounterDTO.setLastName(person.getLastName());
            encounterDTO.setOtherNames(person.getOtherNames());
            encounterDTO.setDob(person.getDob());
            log.info("GETTING encounter in List by Pid 12... " + encounterDTO);

            encounterDTOS.add(encounterDTO);
        });

        return encounterDTOS;
    }

    public EncounterDTO getByEncounterId(Long id) {
        Optional<Encounter> patientEncounter = this.encounterRepository.findById(id);

        if (!patientEncounter.isPresent())
            throw new RecordNotFoundException();


        Encounter encounter = patientEncounter.get();
        EncounterDTO encounterDTO = new EncounterDTO();

        Patient patient = this.patientRepository.findById(encounter.getPatientId()).get();

        if (patient.getArchive() == 0)
            throw new RecordNotFoundException();

        Person person = this.personRepository.findById(patient.getPersonId()).get();

        encounterDTO.setEncounterId(encounter.getId());
        encounterDTO.setPatientId(encounter.getPatientId());
        encounterDTO.setFormName(encounter.getFormName());
        encounterDTO.setServiceName(encounter.getServiceName());
        encounterDTO.setVisitId(encounter.getVisitId());
        encounterDTO.setDateEncounter(encounter.getDateEncounter());
        encounterDTO.setFormData(encounter.getFormData());
        encounterDTO.setTimeCreated(encounter.getTimeCreated());

        encounterDTO.setFirstName(person.getFirstName());
        encounterDTO.setLastName(person.getLastName());
        encounterDTO.setOtherNames(person.getOtherNames());
        encounterDTO.setDob(person.getDob());

        return encounterDTO;
    }

    //UPDATING AN ENCOUNTER
    public Encounter updateEncounter(Long encounterId, Object formData) {
        log.info(encounterId + " EncounterDTO for Updating form Data -" + formData);
        Optional<Encounter> encounter = this.encounterRepository.findById(encounterId);
        encounter.orElseGet(EncounterService::notExit);

        //getting a single encounter
        Encounter encounter1 = encounter.get();

        Patient patient = this.patientRepository.findById(encounter1.getPatientId()).get();

        if (patient.getArchive() == 0)
            throw new RecordNotFoundException();

        //SETTING NEW ENCOUNTERS FOR UPDATE
        encounter1.setFormData(formData);


        //UPDATING THE ENCOUNTER
        this.encounterRepository.save(encounter1);

        return encounter1;
    }

    public ClinicianPatient assignClinican(ClinicianPatient clinicianPatient) {
        log.info("Saving Assigned Clinician to Patient " + clinicianPatient.getPatientId() + "visitid = " + clinicianPatient.getVisitId() + clinicianPatient.getClinicanByUserId() + clinicianPatient.getAppCodesetId());

        ClinicianPatient cp = this.clinicianPatientRepository.save(clinicianPatient);

        log.info("Saving Assigned Clinician to Patient " + cp);

        return clinicianPatient;

    }

    //FOR SAVING ALL CONSULTATION FORM......
    public EncounterDTO saveConsultation(EncounterDTO encounterDTO) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime time = LocalTime.parse(formatter.format(LocalTime.now()), formatter);

        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate today = LocalDate.parse(formatter.format(LocalDate.now()), formatter);

        log.info("Getting Consultation..." + encounterDTO);
        EncounterDTO encounterDTO1 = encounterDTO;
        Encounter encounter = new Encounter();
        try {
            Patient patient1 = this.patientRepository.getOne(encounterDTO.getPatientId());
            if ((patient1 == null || patient1.getArchive() == 0) || patient1.getArchive() == 0)
                throw new RecordNotFoundException();

            Form form = formRepository.findByName(encounterDTO.getFormName());

            Service service = this.servicesRepository.findByServiceName(encounterDTO.getServiceName());

            //Checking is the patient has been checked in by visit id
            Visit visit = this.visitRepository.getOne(encounterDTO.getVisitId());

            //check if an encounter already exist with
            //findBy PatientId And ServiceName And FormName And VisitId
            //Optional<Encounter> patientEncounter = this.encounterRepository.findByPatientIdAndServiceNameAndFormNameAndVisitId(patient1.getId(), service.getServiceName(), encounterDTO.getFormName(), visit.getId());
            //patientEncounter.map(EncounterService::exist);

            log.info("GETTING CONSULTATION FROM FRONT END... " + encounterDTO);
            //issues may come up with wrong form name
            encounter.setFormName(form.getName());
            encounter.setFormData(encounterDTO.getFormData());
            encounter.setDateEncounter(today);
            encounter.setTimeCreated(time);
            encounter.setPatientId(patient1.getId());
            encounter.setVisitId(visit.getId());
            encounter.setServiceName(service.getServiceName());

            encounter = this.encounterRepository.save(encounter);
            log.info("SAVING CONSULTATION TO BACKEND 12345... " + encounter);

            encounterDTO1.setEncounterId(encounter.getId());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw ex;
        }
        return encounterDTO1;

    }


    public List<LabTestGroup> getAllLabTestGroup() {
        return this.labTestGroupRepository.findAll();
    }

    public List<Drug> getAllDrug() {
        return this.drugRepository.findAll();
    }


    //Getting a single encounter for a patient
    //GETTING LATEST ENCOUNTER(DRUG ORDER, VITALS, LAB TEST, CONSULTATION)
    public EncounterDTO getLatestEncounter(Long PatientId, String ServiceName, String FormName) {

        Optional<Encounter> patientEncounter = this.encounterRepository.findFirstByPatientIdAndServiceNameAndFormNameOrderByDateEncounterDesc(PatientId, ServiceName, FormName);
        //patientEncounter.map(EncounterService::exist);
        if (!patientEncounter.isPresent())
            throw new RecordNotFoundException();

        Encounter encounter = patientEncounter.get();
        Patient patient = this.patientRepository.findById(encounter.getPatientId()).get();

        if (patient.getArchive() == 0)
            throw new RecordNotFoundException();

        //Creating a person object
        Person person = this.personRepository.findById(patient.getPersonId()).get();

        EncounterDTO encounterDTO1 = new EncounterDTO();

        encounterDTO1.setDateEncounter(encounter.getDateEncounter());
        encounterDTO1.setFormData(encounter.getFormData());
        encounterDTO1.setPatientId(encounter.getPatientId());
        encounterDTO1.setFormName(encounter.getFormName());
        encounterDTO1.setServiceName(encounter.getServiceName());
        encounterDTO1.setVisitId(encounter.getVisitId());
        encounterDTO1.setTimeCreated(encounter.getTimeCreated());
        encounterDTO1.setFirstName(person.getFirstName());
        encounterDTO1.setLastName(person.getLastName());
        encounterDTO1.setDob(person.getDob());
        encounterDTO1.setOtherNames(person.getOtherNames());

        log.info("GETTING encounter Latest 12... " + encounterDTO1);
        //issues may come up with wrong form name
        return encounterDTO1;

    }

    public List<EncounterDTO> getAllLatestEncounter(LocalDate DateEncounter, String serviceName, String formName) {
        List<EncounterDTO> encounterDTOS = new ArrayList();

        List<Encounter> tempEncounter = encounterRepository.findAllByServiceNameAndFormNameAndDateEncounterOrderByDateEncounterDesc(serviceName, formName, DateEncounter);

        if (tempEncounter.size() < 1)
            throw new RecordNotFoundException();


        tempEncounter.forEach(OnePatientEncounter -> {
            Patient patient = this.patientRepository.findById(OnePatientEncounter.getPatientId()).get();

            if (patient.getArchive() == 0)
                return;

            Person person = this.personRepository.findById(patient.getPersonId()).get();
            EncounterDTO encounterDTO = new EncounterDTO();

            encounterDTO.setEncounterId(OnePatientEncounter.getId());
            encounterDTO.setPatientId(OnePatientEncounter.getPatientId());
            encounterDTO.setFormName(OnePatientEncounter.getFormName());
            encounterDTO.setServiceName(OnePatientEncounter.getServiceName());
            encounterDTO.setVisitId(OnePatientEncounter.getVisitId());
            encounterDTO.setDateEncounter(OnePatientEncounter.getDateEncounter());
            encounterDTO.setFormData(OnePatientEncounter.getFormData());
            encounterDTO.setTimeCreated(OnePatientEncounter.getTimeCreated());

            encounterDTO.setFirstName(person.getFirstName());
            encounterDTO.setLastName(person.getLastName());
            encounterDTO.setOtherNames(person.getOtherNames());
            encounterDTO.setDob(person.getDob());
            log.info("GETTING encounter in List by Pid 12... " + encounterDTO);

            encounterDTOS.add(encounterDTO);
        });

        return encounterDTOS;
    }


    //List of Lab Test
    public List<LabTestDTO> getAllLabTest(Long labTestCategoryId) {
        List<LabTestDTO> labTestDTOS = new ArrayList();
        this.labTestRepository.findAllByLabTestCategoryId(labTestCategoryId).forEach(singleTest -> {
            LabTestDTO labTestDTO = new LabTestDTO();
            labTestDTO.setId(singleTest.getId());
            labTestDTO.setDescription(singleTest.getDescription());
            labTestDTO.setUnitMeasurement(singleTest.getUnitMeasurement());

            labTestDTOS.add(labTestDTO);
            log.info("LabTest Description is " + singleTest.getDescription());

        });
        return labTestDTOS;
    }

}


   /* public List<EncounterDTO> getAllLatestPatientEncounter(Long patientId, String serviceName, String formName) {
        List<EncounterDTO> encounterDTOS = new ArrayList();

        List<Encounter> tempEncounter = encounterRepository.findAllByServiceNameAndFormNameAndPatientIdOrderByDateEncounterDesc(patientId,serviceName, formName);

        if (tempEncounter.size() < 1)
            throw new RecordNotFoundException();


        tempEncounter.forEach(OnePatientEncounter -> {
            Patient patient = this.patientRepository.findById(OnePatientEncounter.getPatientId()).get();

            if (patient.getArchive() == 0)
                return;

            Person person = this.personRepository.findById(patient.getPersonId()).get();
            EncounterDTO encounterDTO = new EncounterDTO();

            encounterDTO.setEncounterId(OnePatientEncounter.getId());
            encounterDTO.setPatientId(OnePatientEncounter.getPatientId());
            encounterDTO.setFormName(OnePatientEncounter.getFormName());
            encounterDTO.setServiceName(OnePatientEncounter.getServiceName());
            encounterDTO.setVisitId(OnePatientEncounter.getVisitId());
            encounterDTO.setDateEncounter(OnePatientEncounter.getDateEncounter());
            encounterDTO.setFormData(OnePatientEncounter.getFormData());
            encounterDTO.setTimeCreated(OnePatientEncounter.getTimeCreated());

            encounterDTO.setFirstName(person.getFirstName());
            encounterDTO.setLastName(person.getLastName());
            encounterDTO.setOtherNames(person.getOtherNames());
            encounterDTO.setDob(person.getDob());
            log.info("GETTING encounter in List by Pid 12... " + encounterDTO);

            encounterDTOS.add(encounterDTO);
        });

        return encounterDTOS;
    }*/
