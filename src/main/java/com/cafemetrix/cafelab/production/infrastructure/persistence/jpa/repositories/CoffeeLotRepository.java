package com.cafemetrix.cafelab.production.infrastructure.persistence.jpa.repositories;

import com.cafemetrix.cafelab.production.domain.model.aggregates.CoffeeLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoffeeLotRepository extends JpaRepository<CoffeeLot, Long> {
    List<CoffeeLot> findByUserIdAndIsCurrentTrue(Long userId);

    List<CoffeeLot> findBySupplierIdAndIsCurrentTrue(Long supplierId);

    List<CoffeeLot> findByIsCurrentTrue();

    List<CoffeeLot> findByIsCurrentTrueAndRecordStatus(String recordStatus);

    List<CoffeeLot> findByLotLineageIdOrderByVersionNumberDesc(Long lotLineageId);

    @Query("SELECT COUNT(c) > 0 FROM CoffeeLot c WHERE c.lotName.value = :name AND c.userId = :userId AND c.isCurrent = true AND c.recordStatus = 'activo'")
    boolean existsCurrentByLotNameValueAndUserId(@Param("name") String name, @Param("userId") Long userId);

    @Query("SELECT COUNT(c) > 0 FROM CoffeeLot c WHERE c.lotName.value = :name AND c.userId = :userId AND c.isCurrent = true AND c.recordStatus = 'activo' AND c.lotLineageId <> :lineageId")
    boolean existsCurrentByLotNameValueAndUserIdExcludingLineage(
            @Param("name") String name,
            @Param("userId") Long userId,
            @Param("lineageId") Long lineageId);
}
