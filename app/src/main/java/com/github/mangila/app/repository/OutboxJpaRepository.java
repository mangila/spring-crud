package com.github.mangila.app.repository;

import com.github.mangila.app.model.outbox.OutboxEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxJpaRepository extends BaseJpaRepository<OutboxEntity, String> {

}
