package com.github.mangila.app.repository;

import com.github.mangila.app.model.outbox.OutboxNextSequenceEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

public interface OutboxNextSequenceRepository extends BaseJpaRepository<OutboxNextSequenceEntity, String> {
}
