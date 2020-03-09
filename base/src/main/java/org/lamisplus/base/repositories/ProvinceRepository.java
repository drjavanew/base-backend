package org.lamisplus.base.repositories;

import org.lamisplus.base.domain.entities.Province;
import org.lamisplus.base.domain.entities.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, Long> {
    List<Province> findBystateByStateId(State state);
}
