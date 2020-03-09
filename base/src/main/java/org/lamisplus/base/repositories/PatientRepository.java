package org.lamisplus.base.repositories;

import org.lamisplus.base.domain.entities.Encounter;
import org.lamisplus.base.domain.entities.Patient;
import org.lamisplus.base.domain.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    @Query("select p from Person p where lower(p.firstName) like lower(concat('%', :search, '%')) " +
            "or lower(p.lastName) like lower(concat('%', :search, '%'))")
    List<Person> findByFirstNameLastName(String search);
    Optional<Patient> findByHospitalNumber(String number);
    Optional<Patient> findByPersonId(Long PersonId);


}
