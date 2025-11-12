package com.github.mangila.app.repository;

import com.github.mangila.app.model.outbox.OutboxProcessedSequenceEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

public interface OutboxProcessedSequenceJpaRepository extends BaseJpaRepository<OutboxProcessedSequenceEntity, String> {

}
