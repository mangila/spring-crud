package com.github.mangila.app.repository;

import com.github.mangila.app.model.outbox.OutboxSequenceEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

public interface OutboxSequenceRepository extends BaseJpaRepository<OutboxSequenceEntity, String> {
}
