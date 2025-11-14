package com.github.mangila.app.repository;

import com.github.mangila.app.model.outbox.OutboxEntity;
import com.github.mangila.app.model.outbox.OutboxEventStatus;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxJpaRepository extends BaseJpaRepository<OutboxEntity, UUID> {
    List<OutboxEntity> findAllByAggregateId(String aggregateId, Sort sort, Limit limit);
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
    void changeStatus(OutboxEventStatus status, String aggregateId);
}
