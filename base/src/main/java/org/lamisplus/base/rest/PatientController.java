package org.lamisplus.base.rest;

import org.lamisplus.base.domain.dto.HeaderUtil;
import org.lamisplus.base.domain.dto.PatientDTO;
import org.lamisplus.base.domain.entities.Person;
import org.lamisplus.base.repositories.*;
import org.lamisplus.base.service.PatientServices;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hibernate.id.IdentifierGenerator.ENTITY_NAME;

@RestController
@Validated
@RequestMapping("/api/patients")
public class PatientController {

    private final PersonRepository personRepository;
    private final PersonContactRepository personContactRepository;
    private final PersonRelativeRepository personRelativeRepository;
    private final PatientRepository patientRepository;
    private final PatientServices patientService;
    private final ServiceEnrollmentRepository patientServiceEnrollmentRepository;

    public PatientController(PersonRepository personRepository, PatientServices patientService, PersonContactRepository personContactRepository, PersonRelativeRepository personRelativeRepository, PatientRepository patientRepository, ServiceEnrollmentRepository patientServiceEnrollmentRepository) {
        this.personRepository = personRepository;
        this.patientService = patientService;
        this.personContactRepository = personContactRepository;
        this.personRelativeRepository = personRelativeRepository;
        this.patientRepository = patientRepository;
        this.patientServiceEnrollmentRepository = patientServiceEnrollmentRepository;
    }

    @InitBinder
    public void allowEmptyDateBinding(WebDataBinder binder) {
        // Custom String Editor. tell spring to set empty values as null instead of empty string.
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));

        //Custom Date Editor

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");
        simpleDateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(simpleDateFormat, false));
    }


    @GetMapping
    public Iterable findAll() {
        return this.patientService.getAllPatient();
    }

    @GetMapping("/{HospitalNo}")
    public PatientDTO findOne(@PathVariable String HospitalNo) {
        return this.patientService.getPatientByHospitalNo(HospitalNo);

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Person> save(@RequestBody PatientDTO patientDTO) throws URISyntaxException {
        Person person = this.patientService.save(patientDTO);
        return ResponseEntity.created(new URI("/api/patient/" + person.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, String.valueOf(person.getId()))).body(person);
    }


    /*@PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Patient save(@RequestBody Patient patient) {
        return patientRepository.save(patient);
    }
    */
    @PutMapping("/{id}")
    public PatientDTO update(@RequestBody PatientDTO patientDTO) throws RecordNotFoundException {
        return this.patientService.updatePatient(patientDTO);
    }

    @DeleteMapping("/{hospitalNumber}")
    public Boolean delete(@PathVariable String hospitalNumber, @RequestParam Byte archive) throws RecordNotFoundException {
        return this.patientService.archivePatient(hospitalNumber, archive);
    }
}
