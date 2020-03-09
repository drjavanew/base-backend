package org.lamisplus.base.service;

import lombok.extern.slf4j.Slf4j;
import org.lamisplus.base.domain.dto.BadRequestAlertException;
import org.lamisplus.base.domain.dto.DtoToEntity;
import org.lamisplus.base.domain.dto.PatientDTO;
import org.lamisplus.base.domain.dto.PersonRelativesDTO;
import org.lamisplus.base.domain.entities.Patient;
import org.lamisplus.base.domain.entities.Person;
import org.lamisplus.base.domain.entities.PersonContact;
import org.lamisplus.base.domain.entities.PersonRelative;
import org.lamisplus.base.repositories.*;
import org.lamisplus.base.rest.RecordNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
@Transactional
@Slf4j
public class PatientServices {

    // Declaring variable used in the PatientServices
    private static final String ENTITY_NAME = "patient";
    private final ApplicationCodesetRepository AppCodesetRepository;
    private final PatientRepository patientRepository;
    private final PersonRepository personRepository;
    private final ServicesRepository serviceRepository;
    private final IdentifierTypeRepository identifierTypeRepository;
    private final ServiceEnrollmentRepository serviceEnrollmentRepository;
    private final PersonContactRepository personContactRepository;
    private final PersonRelativeRepository personRelativeRepository;
    private final ModuleRepository moduleRepository;
    private final CountriesRepository countryRepository;
    private final StateRepository stateRepository;
    private final ProvinceRepository provinceRepository;


    //Constructor for initializing variables
    public PatientServices(PatientRepository patientRepository, PersonRepository personRepository, ServicesRepository serviceRepository, IdentifierTypeRepository identifierTypeRepository, ServiceEnrollmentRepository patientServiceEnrollmentRepository, PersonContactRepository personContactsRepository, PersonRelativeRepository personRelativeRepository, ModuleRepository moduleRepository, ApplicationCodesetRepository AppCodesetRepository, CountriesRepository countryRepository, StateRepository stateRepository, ProvinceRepository provinceRepository) {
        this.patientRepository = patientRepository;
        this.personRepository = personRepository;
        this.serviceRepository = serviceRepository;
        this.identifierTypeRepository = identifierTypeRepository;
        this.serviceEnrollmentRepository = patientServiceEnrollmentRepository;
        this.personContactRepository = personContactsRepository;
        this.personRelativeRepository = personRelativeRepository;
        this.moduleRepository = moduleRepository;
        this.AppCodesetRepository = AppCodesetRepository;
        this.countryRepository = countryRepository;
        this.stateRepository = stateRepository;
        this.provinceRepository = provinceRepository;
    }

    private static Object exist(Patient o) {
        throw new BadRequestAlertException("Patient Already Exist", ENTITY_NAME, "id Already Exist");
    }

    private static Patient notExit() {
        throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "id is null");
    }

    //Checking for null Value
    public Object process(Object o) throws NullPointerException {
        if (o == null) {
            throw new NullPointerException(o.toString() + "in empty/invalid");
        } else {
            return o;
        }
    }

    /**
     * Saving a patient
     */
    public Person save(PatientDTO patientDTO) {
        LocalDate localDate = null;
        DateTimeFormatter formatter = null;

        // Converting 'dd-MMM-yyyy' String format to LocalDate
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        localDate = LocalDate.parse(formatter.format(LocalDate.now()), formatter);

        //Creating a patient object
        Optional<Patient> patient1 = this.patientRepository.findByHospitalNumber(patientDTO.getHospitalNumber());

        patient1.map(PatientServices::exist);

        //Creating a person object
        Person person = new Person();

        //Setting a person
        person.setFirstName(patientDTO.getFirstName());
        person.setLastName(patientDTO.getLastName());
        person.setOtherNames(patientDTO.getOtherNames());

        person.setDob(patientDTO.getDob());
        person.setDobEstimated(patientDTO.getDobEstimated());
        person.setPersonTitleId(patientDTO.getTitleId());
        person.setMaritalStatusId(patientDTO.getMaritalStatusId());
        person.setGenderId(patientDTO.getGenderId());
        person.setEducationId(patientDTO.getEducationId());
        person.setOccupationId(patientDTO.getOccupationId());

        //Persisting the Person
        Person person1 = this.personRepository.save(person);

        log.info("SAVING person ... " + person1);


        //creating a patient object
        Patient p = new Patient();
        //Setters for patient
        Byte b = 1;
        p.setPersonByPersonId(person1);
        p.setDateRegistration(patientDTO.getDateRegistration());
        p.setFacilityId(patientDTO.getFacilityId());
        p.setHospitalNumber(patientDTO.getHospitalNumber());
        p.setArchive(b);

        //Persisting the patient
        Patient patientsave = this.patientRepository.save(p);

        log.info("SAVING patient ... " + patientsave);
        PersonContact personContact = new PersonContact();

        personContact.setCountryId(patientDTO.getCountryId());
        personContact.setStateId(patientDTO.getStateId());
        personContact.setProvinceId(patientDTO.getProvinceId());

        personContact.setPersonByPersonId(person1);
        personContact.setPersonId(person1.getId());

        PersonContact personContactSave = this.personContactRepository.save(personContact);
        log.info("SAVING Person Contact ... " + personContactSave);

        //Creating Relatives Objects e.g. Can be more than one relatives
        //and persistent them
        if (patientDTO.getPersonRelativeDTOList() != null) {
            List<PersonRelativesDTO> personRelativeDTOS = patientDTO.getPersonRelativeDTOList();
            personRelativeDTOS.stream().map(DtoToEntity::convertPersonRelativeDTORelative).forEach(personRelative -> {
                personRelative.setPersonByPersonId(person1);
                if (!personRelative.getFirstName().equals("")) {
                    this.personRelativeRepository.save(personRelative);
                    log.info("SAVING Person Relative ... ", personRelative.getFirstName());
                }


            });
        }

        return person;
    }

    public List<PatientDTO> getAllPatient() {
        List<Person> personList = personRepository.findAllByOrderByIdDesc();
        List<PatientDTO> patientDTOS = new ArrayList();

        personList.forEach(personDetails -> {
            PatientDTO pDTO = new PatientDTO();
            //PATIENT DETAILS

            if (!patientRepository.findByPersonId(personDetails.getId()).isPresent())
                return;

            Patient patient = patientRepository.findByPersonId(personDetails.getId()).get();

            /*if(patient.getArchive() == 0)
                return;*/

            pDTO.setId(patient.getId());
            pDTO.setHospitalNumber(patient.getHospitalNumber());
            pDTO.setDateRegistration(patient.getDateRegistration());
            pDTO.setFacilityId(patient.getFacilityId());
            log.info("Checking patient" + patient.getId());
            log.info("Checking patient facility" + patient.getFacilityId());
            log.info("Checking patient Hospital No" + patient.getHospitalNumber());


            //PERSON DETAILS
            pDTO.setFirstName(personDetails.getFirstName());
            pDTO.setLastName(personDetails.getLastName());
            pDTO.setDobEstimated(personDetails.getDobEstimated());
            pDTO.setDob(personDetails.getDob());
            pDTO.setOtherNames(personDetails.getOtherNames());
            pDTO.setEducationId(personDetails.getEducationId());
            pDTO.setGenderId(personDetails.getGenderId());
            pDTO.setOccupationId(personDetails.getOccupationId());
            pDTO.setMaritalStatusId(personDetails.getMaritalStatusId());
            pDTO.setTitleId(personDetails.getPersonTitleId());

            PersonContact personContact = this.personContactRepository.findAllByPersonId(personDetails.getId());
            pDTO.setMobilePhoneNumber(personContact.getMobilePhoneNumber());

            //CHECK IF A PERSON HAS RELATIVES
            List<PersonRelative> personRelativeList = personRelativeRepository.findByPersonId(personDetails.getId());
            if (personRelativeList.size() > 0) {
                PersonRelativesDTO personRelativesDTO2 = new PersonRelativesDTO();
                List<PersonRelativesDTO> personRelativesDTOList = new ArrayList();
                personRelativeList.forEach(personRelativeDetails -> {
                    personRelativesDTO2.setFirstName(personRelativeDetails.getFirstName());
                    personRelativesDTO2.setLastName(personRelativeDetails.getLastName());
                    personRelativesDTO2.setRelationshipTypeId(personRelativeDetails.getRelationshipTypeId());
                    personRelativesDTO2.setAddress(personRelativeDetails.getAddress());
                    personRelativesDTO2.setEmail(personRelativeDetails.getEmail());
                    personRelativesDTO2.setMobilePhoneNumber(personRelativeDetails.getMobilePhoneNumber());
                    personRelativesDTO2.setAlternatePhoneNumber(personRelativeDetails.getAlternatePhoneNumber());

                });
                pDTO.setPersonRelativeDTOList(personRelativesDTOList);
            }
            patientDTOS.add(pDTO);

        });
        return patientDTOS;

    }

    public PatientDTO getPatientByHospitalNo(String Hid) {
        Optional<Patient> patient1 = this.patientRepository.findByHospitalNumber(Hid);

        //Creating a person object
        Person person = new Person();

        PatientDTO pDTO = new PatientDTO();
        //PATIENT DETAILS

        Patient patient = patient1.get();
        pDTO.setId(patient.getId());
        pDTO.setHospitalNumber(patient.getHospitalNumber());
        pDTO.setDateRegistration(patient.getDateRegistration());
        pDTO.setFacilityId(patient.getFacilityId());
        log.info("Checking patient" + patient.getId());
        log.info("Checking patient Hospital No" + patient.getHospitalNumber());

        //PERSON DETAILS
        person = this.personRepository.getOne(patient.getPersonId());
        pDTO.setFirstName(person.getFirstName());
        pDTO.setLastName(person.getLastName());
        pDTO.setDobEstimated(person.getDobEstimated());
        pDTO.setDob(person.getDob());
        pDTO.setOtherNames(person.getOtherNames());
        pDTO.setEducationId(person.getEducationId());
        pDTO.setGenderId(person.getGenderId());
        pDTO.setOccupationId(person.getOccupationId());
        pDTO.setMaritalStatusId(person.getMaritalStatusId());
        pDTO.setTitleId(person.getPersonTitleId());


        //CHECK IF A PERSON HAS RELATIVES
        List<PersonRelative> personRelativeList = personRelativeRepository.findByPersonId(person.getId());
        if (personRelativeList.size() > 0) {
            PersonRelativesDTO personRelativesDTO2 = new PersonRelativesDTO();
            List<PersonRelativesDTO> personRelativesDTOList = new ArrayList();
            personRelativeList.forEach(personRelativeDetails -> {
                personRelativesDTO2.setFirstName(personRelativeDetails.getFirstName());
                personRelativesDTO2.setLastName(personRelativeDetails.getLastName());
                personRelativesDTO2.setRelationshipTypeId(personRelativeDetails.getRelationshipTypeId());
                personRelativesDTO2.setAddress(personRelativeDetails.getAddress());
                personRelativesDTO2.setEmail(personRelativeDetails.getEmail());
                personRelativesDTO2.setMobilePhoneNumber(personRelativeDetails.getMobilePhoneNumber());
                personRelativesDTO2.setAlternatePhoneNumber(personRelativeDetails.getAlternatePhoneNumber());


            });
            pDTO.setPersonRelativeDTOList(personRelativesDTOList);
        }

        return pDTO;

    }

    public Boolean archivePatient(String HospitalNumber, Byte archive) {
        Optional<Patient> patient1 = this.patientRepository.findByHospitalNumber(HospitalNumber);

        Patient patient = patient1.get();

        if (patient.getArchive() != 1)
            throw new RecordNotFoundException();

        patient.setArchive(archive);

        this.patientRepository.save(patient);
        return archive != patient.getArchive();


    }


    public PatientDTO updatePatient(PatientDTO patientDTO) {
        LocalDate localDate = null;
        DateTimeFormatter formatter = null;

        // Converting 'dd-MMM-yyyy' String format to LocalDate
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        localDate = LocalDate.parse(formatter.format(LocalDate.now()), formatter);
        //Creating a patient object
        Optional<Patient> patient1 = this.patientRepository.findById(patientDTO.getId());
        if (patient1.get().getArchive() == 0)
            throw new RecordNotFoundException();

        Patient p = patient1.get();

        //Creating a person object
        Person person = this.personRepository.getOne(p.getPersonId());
        //Setting a person
        person.setFirstName(patientDTO.getFirstName());
        person.setLastName(patientDTO.getLastName());
        person.setOtherNames(patientDTO.getOtherNames());

        person.setDob(patientDTO.getDob());
        person.setDobEstimated(patientDTO.getDobEstimated());
        person.setPersonTitleId(patientDTO.getTitleId());
        person.setMaritalStatusId(patientDTO.getMaritalStatusId());
        person.setGenderId(patientDTO.getGenderId());
        person.setEducationId(patientDTO.getEducationId());
        person.setOccupationId(patientDTO.getOccupationId());

        //Persisting the Person
        Person person1 = this.personRepository.save(person);

        log.info("Updating person ... " + person1);

        //Setters for patient
        p.setPersonByPersonId(person1);
        p.setDateRegistration(patientDTO.getDateRegistration());
        p.setFacilityId(patientDTO.getFacilityId());
        p.setHospitalNumber(patientDTO.getHospitalNumber());

        //Persisting the patient
        Patient patientUpdate = this.patientRepository.save(p);

        log.info("Updating patient ... " + patientUpdate);
        PersonContact personContact = this.personContactRepository.getOne(person1.getId());

        personContact.setCountryId(patientDTO.getCountryId());
        personContact.setStateId(patientDTO.getStateId());
        personContact.setProvinceId(patientDTO.getProvinceId());
        personContact.setMobilePhoneNumber(patientDTO.getMobilePhoneNumber());
        personContact.setAlternatePhoneNumber(patientDTO.getAlternatePhoneNumber());
        personContact.setEmail(patientDTO.getEmail());
        personContact.setZipCode(patientDTO.getZipCode());
        personContact.setCity(patientDTO.getCity());
        personContact.setStreet(patientDTO.getStreet());
        personContact.setLandmark(patientDTO.getLandmark());
        personContact.setPersonId(person1.getId());

        PersonContact personContactUpdate = this.personContactRepository.save(personContact);
        log.info("Updating Person Contact ... " + personContactUpdate);

        //Creating Relatives Objects e.g. Can be more than one relatives
        //and persistent them
        if (patientDTO.getPersonRelativeDTOList() != null) {
            List<PersonRelativesDTO> personRelativeDTOS = patientDTO.getPersonRelativeDTOList();
            personRelativeDTOS.stream().map(DtoToEntity::convertPersonRelativeDTORelative).forEach(personRelative -> {
                personRelative.setPersonByPersonId(person1);
                if (!personRelative.getFirstName().equals("")) {
                    this.personRelativeRepository.save(personRelative);
                    log.info("Updating Person Relative ... ", personRelative.getFirstName());
                }


            });
        }

        return patientDTO;
    }


}
