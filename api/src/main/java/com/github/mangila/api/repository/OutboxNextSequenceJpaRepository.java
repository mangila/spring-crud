package com.github.mangila.api.repository;

import com.github.mangila.api.model.outbox.OutboxNextSequenceEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

public interface OutboxNextSequenceJpaRepository extends BaseJpaRepository<OutboxNextSequenceEntity, String> {

}
