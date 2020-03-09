package org.lamisplus.base.repositories;

import org.lamisplus.base.domain.entities.Country;
import org.lamisplus.base.domain.entities.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {
    List<State> findBycountryByCountryId(Country country);

}
