package org.example.data.repositories;

import org.example.data.entitites.PojisteniEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PojisteniRepository extends CrudRepository<PojisteniEntity, Long> {
    @Override
    Optional<PojisteniEntity> findById(Long aLong);
}
