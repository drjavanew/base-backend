package org.lamisplus.base.service;


import org.lamisplus.base.domain.dto.BadRequestAlertException;
import org.lamisplus.base.domain.entities.Country;
import org.lamisplus.base.domain.entities.State;
import org.lamisplus.base.repositories.CountriesRepository;
import org.lamisplus.base.repositories.StateRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@Transactional
public class StateService {
    private final StateRepository stateRepository;
    private final CountriesRepository countryRepository;

    public StateService(StateRepository stateRepository, CountriesRepository countryRepository) {
        this.stateRepository = stateRepository;
        this.countryRepository = countryRepository;
    }

    public State save(@RequestBody State state) {
        Optional<Country> country = this.countryRepository.findById(state.getCountryByCountryId().getId());
        if (country.isPresent()) {
            Country country1 = country.get();
            state.setCountryByCountryId(country1);
            return this.stateRepository.save(state);
        } else throw new BadRequestAlertException("Country id did not exist ", "", "id is null");

    }


    public State update(@RequestBody State state) {
        ModelMapper modelMapper = new ModelMapper();
        State state1 = modelMapper.map(state, State.class);
        Country country = this.countryRepository.getOne(state.getCountryByCountryId().getId());
        state1.setCountryByCountryId(country);
        return this.stateRepository.save(state1);
    }

}
