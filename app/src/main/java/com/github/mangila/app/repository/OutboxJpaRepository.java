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

@Repository
public interface OutboxJpaRepository extends BaseJpaRepository<OutboxEntity, String> {
    List<OutboxEntity> findAllByStatusAndAuditMetadataDeleted(OutboxEventStatus status, boolean deleted, Sort sort, Limit limit);

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

    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Query("""
                    UPDATE OutboxEntity o
                    SET o.status = 'PROCESSING'
                    WHERE o.aggregateId = :aggregateId
                    AND o.status <> 'PROCESSING'
            """)
    int optimisticClaim(String aggregateId);
}
