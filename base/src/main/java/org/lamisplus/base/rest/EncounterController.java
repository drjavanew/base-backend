package org.lamisplus.base.rest;

import lombok.extern.slf4j.Slf4j;
import org.lamisplus.base.domain.dto.BadRequestAlertException;
import org.lamisplus.base.domain.dto.EncounterDTO;
import org.lamisplus.base.domain.dto.HeaderUtil;
import org.lamisplus.base.domain.dto.LabTestDTO;
import org.lamisplus.base.domain.entities.ClinicianPatient;
import org.lamisplus.base.domain.entities.Drug;
import org.lamisplus.base.domain.entities.Encounter;
import org.lamisplus.base.domain.entities.LabTestGroup;
import org.lamisplus.base.repositories.EncounterRepository;
import org.lamisplus.base.repositories.PatientRepository;
import org.lamisplus.base.service.EncounterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/encounters")
@Slf4j
public class EncounterController {
    private static String ENTITY_NAME = "encounter";
    private final EncounterRepository encounterRepository;
    private final EncounterService encounterService;
    private final PatientRepository patientRepository;


    public EncounterController(PatientRepository patientRepository, EncounterRepository encounterRepository, EncounterService encounterService) {
        this.patientRepository = patientRepository;
        this.encounterRepository = encounterRepository;
        this.encounterService = encounterService;
    }

    private static Object exist(Encounter encounter) {
        throw new BadRequestAlertException("Patient Encounter Already Exist", ENTITY_NAME, "id Already Exist");
    }

    private static Encounter notExit() {
        throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "id is null");
    }

    //SAVING AN ENCOUNTER (ALL ENCOUNTER - VITALS, CONSULTATION, LAB TEST ORDER)
    @PostMapping
    public ResponseEntity<EncounterDTO> save(@RequestBody EncounterDTO encounterDTO) throws URISyntaxException {
        EncounterDTO encounterDTO1 = encounterService.save(encounterDTO);
        return ResponseEntity.created(new URI("/api/encounter/" + encounterDTO1.getEncounterId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, String.valueOf(encounterDTO1.getEncounterId()))).body(encounterDTO1);
    }

    //ASSIGNING A CLINICIAN
    @PostMapping("/assign-clinician")
    public ResponseEntity<ClinicianPatient> assignClinican(@RequestBody ClinicianPatient clinicianPatient) throws URISyntaxException {
        ClinicianPatient clinicianPatient1 = encounterService.assignClinican(clinicianPatient);
        return ResponseEntity.created(new URI("/api/encounter/" + clinicianPatient1.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, String.valueOf(clinicianPatient1.getId()))).body(clinicianPatient1);
    }

    //FOR SAVING ALL CONSULTATION FORM......
    @PostMapping("/{serviceName}/{formName}/{patientId}")
    public ResponseEntity<EncounterDTO> saveEncounter(@RequestBody EncounterDTO encounterDTO, @PathVariable(required = false) String serviceName, @PathVariable(required = false) String formName, @PathVariable(required = false) Long patientId) throws URISyntaxException {
        serviceName = encounterDTO.getServiceName();
        formName = encounterDTO.getFormName();
        patientId = encounterDTO.getPatientId();

        EncounterDTO encounterDTO1 = encounterService.saveConsultation(encounterDTO);
        return ResponseEntity.created(new URI("/api/encounter/" + encounterDTO1.getEncounterId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, String.valueOf(encounterDTO1.getEncounterId()))).body(encounterDTO1);
    }


    //LAB TEST GROUP.........
    @GetMapping("/laboratory/labtest-group")
    public ResponseEntity<List<LabTestGroup>> getAllLabTestGroup() {
        return ResponseEntity.ok(this.encounterService.getAllLabTestGroup());
    }

    //LAB TEST.........
    @GetMapping("/laboratory/{labtestgroupId}/labtest")
    public ResponseEntity<List<LabTestDTO>> getAllLabTest(@PathVariable Long labtestgroupId) {
        return ResponseEntity.ok(this.encounterService.getAllLabTest(labtestgroupId));
    }


    //GET ALL ENCOUNTER
    @GetMapping
    public ResponseEntity<List<EncounterDTO>> getAllEncounter() {
        return ResponseEntity.ok(this.encounterService.getAll());

    }

    //GETTING LATEST ENCOUNTER(DRUG ORDER, VITALS, LAB TEST, CONSULTATION)
    @GetMapping("/{ServiceName}/{FormName}/{patientId}/{date}")
    public ResponseEntity<EncounterDTO> getSingle(@PathVariable Long patientId, @PathVariable String ServiceName, @PathVariable String FormName, @RequestParam(required = false) String date) throws URISyntaxException {
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        if (date == null || date.isEmpty() || date.equals("")) {
            date = localDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } else {

            // Converting 'dd-MM-yyyy' String format to LocalDate
            localDate = LocalDate.parse(date, formatter);
        }
        log.info("Date is " + date);
        return ResponseEntity.ok(this.encounterService.getByPatientIdServiceNameFormNamelocalDate(patientId, ServiceName, FormName, localDate));
    }

    //GETTING LATEST ENCOUNTER(DRUG ORDER, VITALS, LAB TEST, CONSULTATION for a particular patient
    @GetMapping("/{ServiceName}/{FormName}/{patientId}/last")
    public ResponseEntity<EncounterDTO> getLatestSingle(@PathVariable Long patientId, @PathVariable String ServiceName, @PathVariable String FormName) throws URISyntaxException {
        return ResponseEntity.ok(this.encounterService.getLatestEncounter(patientId, ServiceName, FormName));
    }

    //GETTING LATEST ENCOUNTER(DRUG ORDER, VITALS, LAB TEST, CONSULTATION all patients
    @GetMapping("/{ServiceName}/{FormName}")
    public ResponseEntity<List<EncounterDTO>> getAllLatestEncounter(@PathVariable String ServiceName, @PathVariable String FormName) throws URISyntaxException {
        LocalDate localDate = LocalDate.now();
        String date = localDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        //localDate = LocalDate.parse(date);
        localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        log.info("Date is " + date);

        log.info("All Encounter " + ServiceName + " and " + FormName + " Today is " + localDate);


        return ResponseEntity.ok(this.encounterService.getAllLatestEncounter(localDate, ServiceName, FormName));
    }


    //GETTING ENCOUNTER BY ID
    @GetMapping("/id")
    public ResponseEntity<EncounterDTO> getPatientEncounter(@RequestParam Long encounterId) {
        return ResponseEntity.ok(this.encounterService.getByEncounterId(encounterId));
    }

    //GETTING ENCOUNTER BY PATIENT ID
    @GetMapping("/{patientId}")
    //@ResponseStatus(reason = "Some parameters are invalid")
    public ResponseEntity<List<EncounterDTO>> getByPatientId(@PathVariable Long patientId) throws BadRequestAlertException {
        return ResponseEntity.ok(this.encounterService.getAllByPatientId(patientId));
    }

    //DRUG GROUP.........
    @GetMapping("/pharmacy/drugs")
    public ResponseEntity<List<Drug>> getAllDrugs() {
        return ResponseEntity.ok(this.encounterService.getAllDrug());
    }


    @PutMapping("/{id}")
    public ResponseEntity<Encounter> update(@RequestBody Object formData, @PathVariable Long id) throws URISyntaxException {
        Encounter encounter = this.encounterService.updateEncounter(id, formData);
        return ResponseEntity.created(new URI("/api/encounter/" + encounter.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, String.valueOf(encounter.getId()))).body(encounter);
    }

}
