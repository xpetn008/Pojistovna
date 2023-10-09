package org.example.data.repositories;

import org.example.data.entitites.PojistenecEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PojistenecRepository extends CrudRepository<PojistenecEntity, Long> {
    Optional<PojistenecEntity> findByRodneCislo(String rodneCislo);
}
