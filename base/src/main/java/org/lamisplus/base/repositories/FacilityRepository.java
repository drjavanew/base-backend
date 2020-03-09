package org.lamisplus.base.repositories;

import org.lamisplus.base.domain.entities.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityRepository extends JpaRepository<Country, Long> {

}
