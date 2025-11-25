package com.github.mangila.api.repository;

import com.github.mangila.api.model.outbox.OutboxProcessedSequenceEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxProcessedSequenceJpaRepository extends BaseJpaRepository<OutboxProcessedSequenceEntity, String> {

}
