package org.lamisplus.base.rest;

import org.lamisplus.base.domain.dto.BadRequestAlertException;
import org.lamisplus.base.domain.dto.HeaderUtil;
import org.lamisplus.base.domain.entities.Visit;
import org.lamisplus.base.repositories.VisitRepository;
import org.lamisplus.base.service.VisitService;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@RestController
@RequestMapping("api/visits")
public class VisitController {

    private static final String ENTITY_NAME = "Visit";
    private final VisitRepository visitRepository;
    private final VisitService visitService;
    private final RestExceptionHandler restExceptionHandler;

    public VisitController(RestExceptionHandler restExceptionHandler, VisitRepository visitRepository, VisitService visitService) {
        this.visitRepository = visitRepository;
        this.visitService = visitService;
        this.restExceptionHandler = restExceptionHandler;
    }

    private static Visit notExit() {
        throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "id is null");
    }

    @InitBinder
    public void allowEmptyDateBinding(WebDataBinder binder) {
        // Custom String Editor. tell spring to set empty values as null instead of empty string.
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));

        //Custom Date Editor

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-YYYY");
        simpleDateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(simpleDateFormat, false));
    }

    @GetMapping
    public Iterable findAll() {
        return visitRepository.findAll();
    }

    @GetMapping("/datevisit")
    public Iterable findPatientByDateStart() {
        LocalDate localDate = null;
        DateTimeFormatter formatter = null;

        // Converting 'dd-MMM-yyyy' String format to LocalDate
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        localDate = LocalDate.parse(formatter.format(LocalDate.now()), formatter);

        return this.visitService.getVisitByDateStart(localDate);
    }

    @GetMapping("/{id}")
    public Visit findOne(@PathVariable Long id) {
        return visitRepository.findById(id)
                .orElseThrow(RecordNotFoundException::new);
    }

    @GetMapping("/patient/{PatientId}")
    public Iterable findOnePatientId(@PathVariable Long PatientId) {
        // WORK ON IT
        if (visitRepository.findByPatientId(PatientId).size() < 1)
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Patients Not Found", new RecordNotFoundException(PatientId.toString()));
        else
            return visitRepository.findByPatientId(PatientId);

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Visit> save(@RequestBody Visit visit) throws URISyntaxException {
        Visit visit1 = this.visitService.save(visit);
        return ResponseEntity.created(new URI("/api/visits/" + visit1.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, String.valueOf(visit1.getId()))).body(visit1);
    }


    @PutMapping("/{id}")
    public Visit update(@RequestBody Visit visit, @PathVariable Long id) {
        if (visit.getId() != (id)) {
            throw new RecordIdMismatchException(id);
        }
        visitRepository.findById(id)
                .orElseThrow(RecordNotFoundException::new);
        return visitRepository.save(visit);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        visitRepository.findById(id)
                .orElseThrow(RecordNotFoundException::new);
        visitRepository.deleteById(id);
    }


}
