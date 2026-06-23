package com.cafemetrix.cafelab.production.infrastructure.persistence.jpa.repositories;

import com.cafemetrix.cafelab.production.domain.model.aggregates.CoffeeLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoffeeLotRepository extends JpaRepository<CoffeeLot, Long> {
    List<CoffeeLot> findByUserId(Long userId);

    List<CoffeeLot> findBySupplierId(Long supplierId);

    List<CoffeeLot> findByUserIdAndSupplierId(Long userId, Long supplierId);

    boolean existsByLotNameValueAndUserId(String lotName, Long userId);

    @Query("SELECT COUNT(c) > 0 FROM CoffeeLot c WHERE c.lotName.value = :name AND c.userId = :userId AND c.id != :excludeId")
    boolean existsByLotNameAndUserIdExcluding(@Param("name") String name, @Param("userId") Long userId, @Param("excludeId") Long excludeId);
}
