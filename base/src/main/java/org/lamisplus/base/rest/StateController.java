package org.lamisplus.base.rest;


import org.lamisplus.base.domain.dto.BadRequestAlertException;
import org.lamisplus.base.domain.dto.HeaderUtil;
import org.lamisplus.base.domain.entities.Country;
import org.lamisplus.base.domain.entities.State;
import org.lamisplus.base.repositories.CountriesRepository;
import org.lamisplus.base.repositories.StateRepository;
import org.lamisplus.base.service.StateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/state")
public class StateController {
    private static final String ENTITY_NAME = "state";
    private final StateRepository stateRepository;
    private final CountriesRepository countryRepository;
    private final StateService stateService;

    public StateController(StateRepository stateRepository, CountriesRepository countryRepository, StateService stateService) {
        this.stateRepository = stateRepository;
        this.countryRepository = countryRepository;
        this.stateService = stateService;
    }

    private static Object exist(State o) {
        throw new BadRequestAlertException("State Already Exist", ENTITY_NAME, "id Already Exist");
    }

    private static State notExit() {
        throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "id is null");
    }


    @PostMapping
    public ResponseEntity<State> save(@RequestBody State state) throws URISyntaxException {
        stateRepository.findById(state.getId()).map(StateController::exist);
        State result = stateService.save(state);
        return ResponseEntity.created(new URI("/api/state/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, String.valueOf(result.getId()))).body(result);
    }

    @PutMapping
    public ResponseEntity<State> update(@RequestBody State state) throws URISyntaxException {
        Optional<State> country1 = this.stateRepository.findById(state.getId());
        country1.orElseGet(StateController::notExit);
        State result = stateService.update(state);
        return ResponseEntity.created(new URI("/api/state/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, String.valueOf(result.getId())))
                .body(result);
    }

    @GetMapping
    public ResponseEntity<List<State>> getAllState() {
        return ResponseEntity.ok(this.stateRepository.findAll());
    }


    @GetMapping("/country/{id}")
    public ResponseEntity<List<State>> getAllCountry(@PathVariable Long id) {
        Optional<Country> country = this.countryRepository.findById(id);
        if (country.isPresent()){
            Country country1 = country.get();
            List<State> stateSet = this.stateRepository.findBycountryByCountryId(country1);
            return ResponseEntity.ok(stateSet);
        } else throw new BadRequestAlertException("Record not found ", ENTITY_NAME, "id is  Null");

    }


}
