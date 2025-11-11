package com.github.mangila.app.repository;

import com.github.mangila.app.model.outbox.OutboxEntity;
import com.github.mangila.app.model.outbox.OutboxEventStatus;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxJpaRepository extends BaseJpaRepository<OutboxEntity, String> {
    List<OutboxEntity> findAllByStatusAndAuditMetadataDeleted(OutboxEventStatus status, boolean deleted, Sort sort, Limit limit);
}
