package org.lamisplus.base.repositories;


import org.lamisplus.base.domain.entities.ApplicationCodeset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationCodesetRepository extends JpaRepository<ApplicationCodeset, Long> {

    List<ApplicationCodeset> findBycodesetGroup(String codeset_group);
}
