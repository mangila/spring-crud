package com.github.mangila.app.repository;

import com.github.mangila.app.model.outbox.OutboxLatestProcessedSequenceEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

public interface OutboxCurrentSequenceRepository extends BaseJpaRepository<OutboxLatestProcessedSequenceEntity, String> {
}
