package org.lamisplus.base.repositories;

import org.lamisplus.base.domain.entities.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    Module findByName(String name);
}
