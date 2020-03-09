package org.lamisplus.base.repositories;

import org.lamisplus.base.domain.entities.IdentifierType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentifierTypeRepository extends JpaRepository<IdentifierType, Long> {
    IdentifierType findByIdentifierTypeName(String  name);
}
