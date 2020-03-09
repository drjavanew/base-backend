package org.lamisplus.base.service;

import lombok.extern.slf4j.Slf4j;
import org.lamisplus.base.domain.entities.Country;
import org.lamisplus.base.repositories.CountriesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@Transactional
@Slf4j
public class CountryServices {
    private final CountriesRepository countriesRepository;

    public CountryServices(CountriesRepository countriesRepository) {
        this.countriesRepository = countriesRepository;
    }

    public Country save(Country country) {
        return countriesRepository.save(country);
    }

    public Country update(@RequestBody Country country) {
        Country result = countriesRepository.save(country);
        return countriesRepository.save(result);
    }

}
