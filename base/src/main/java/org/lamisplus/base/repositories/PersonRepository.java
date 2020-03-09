package org.lamisplus.base.repositories;

import org.lamisplus.base.domain.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
List<Person> findAllByOrderByIdDesc();
}
