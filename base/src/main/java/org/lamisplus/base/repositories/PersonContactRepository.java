package org.lamisplus.base.repositories;

import org.lamisplus.base.domain.entities.Person;
import org.lamisplus.base.domain.entities.PersonContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonContactRepository extends JpaRepository<PersonContact, Long> {
    PersonContact findBypersonByPersonId(Person person);
    PersonContact findAllByPersonId(Long id);
}
