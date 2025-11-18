package com.github.mangila.api.repository;

import com.github.mangila.api.model.outbox.OutboxProcessedSequenceEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

public interface OutboxProcessedSequenceJpaRepository extends BaseJpaRepository<OutboxProcessedSequenceEntity, String> {

}
