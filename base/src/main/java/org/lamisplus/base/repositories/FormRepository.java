package org.lamisplus.base.repositories;

import org.lamisplus.base.domain.entities.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {
    Form findByName(String name);
    List<Form> findAll();
}

