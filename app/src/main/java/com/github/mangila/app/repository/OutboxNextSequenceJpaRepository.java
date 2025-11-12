package com.github.mangila.app.repository;

import com.github.mangila.app.model.outbox.OutboxNextSequenceEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

public interface OutboxNextSequenceJpaRepository extends BaseJpaRepository<OutboxNextSequenceEntity, String> {

}
