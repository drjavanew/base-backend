package org.lamisplus.base.rest;


import lombok.extern.slf4j.Slf4j;
import org.lamisplus.base.domain.dto.BadRequestAlertException;
import org.lamisplus.base.domain.dto.HeaderUtil;
import org.lamisplus.base.domain.entities.Province;
import org.lamisplus.base.domain.entities.State;
import org.lamisplus.base.repositories.ProvinceRepository;
import org.lamisplus.base.repositories.StateRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/province")
@Slf4j
public class ProvinceController {
    private static final String ENTITY_NAME = "province";
    private final ProvinceRepository provinceRepository;
    private final StateRepository stateRepository;

    public ProvinceController(ProvinceRepository provinceRepository, StateRepository stateRepository) {
        this.provinceRepository = provinceRepository;
        this.stateRepository = stateRepository;
    }

    private static Object exist(Province o) {
        throw new BadRequestAlertException("Province Already Exist", ENTITY_NAME, "id Already Exist");
    }

    private static Province notExit() {
        throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "id is null");
    }

    @PostMapping
    public ResponseEntity<Province> save(@RequestBody Province province) throws URISyntaxException {
        provinceRepository.findById(province.getId()).map(ProvinceController::exist);
        Optional<State> state = this.stateRepository.findById(province.getStateByStateId().getId());
        if (state.isPresent()){
            State state1 = state.get();
            province.setStateByStateId(state1);
            Province result = this.provinceRepository.save(province);
            return ResponseEntity.created(new URI("/api/province/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, String.valueOf(result.getId()))).body(result);
        } else throw new BadRequestAlertException("State id did not exist ", ENTITY_NAME, "id is null");

    }

    @PutMapping
    public ResponseEntity<Province> update(@RequestBody Province province) throws URISyntaxException {
        Optional<Province> province11 = this.provinceRepository.findById(province.getId());
        province11.orElseGet(ProvinceController::notExit);
        State state = this.stateRepository.getOne(province.getStateByStateId().getId());
        province.setStateByStateId(state);
        Province result = this.provinceRepository.save(province);
        return ResponseEntity.created(new URI("/api/province/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, String.valueOf(result.getId())))
                .body(result);
    }

    @GetMapping
    public ResponseEntity<List<Province>> getAllProvince() {
        return ResponseEntity.ok(this.provinceRepository.findAll());
    }


   /* @DeleteMapping("/{id}")
    public ResponseEntity<?> archiveProvince(@PathVariable Long id) {
        Optional<Province> state = this.provinceRepository.findById(id);
        if (state.isPresent()){
            Province stateArchive = state.get();
            stateArchive.setArchive(Boolean.TRUE);
        } else throw new BadRequestAlertException("Record not found with id ", ENTITY_NAME, "id is  Null");
        return ResponseEntity.ok().build();
    }*/

    @GetMapping("/state/{id}")
    public ResponseEntity<List<Province>> getAllProvince(@PathVariable Long id) {
        State state = this.stateRepository.getOne(id);
        List<Province> stateSet = this.provinceRepository.findBystateByStateId(state);
        return ResponseEntity.ok(stateSet);
    }


}
