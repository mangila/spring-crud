package com.github.mangila.api.repository;

import com.github.mangila.api.model.outbox.OutboxEntity;
import com.github.mangila.api.model.outbox.OutboxEventStatus;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxJpaRepository extends BaseJpaRepository<OutboxEntity, UUID> {
    Page<OutboxEntity> findAllByAggregateId(String aggregateId, Pageable pageable);

    List<OutboxEntity> findAllByStatus(OutboxEventStatus status, Sort sort, Limit limit);

    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Query("""
            UPDATE OutboxEntity o
            SET o.status = :status
            WHERE o.aggregateId = :aggregateId
            """)
    int changeStatus(OutboxEventStatus status, String aggregateId);
}
