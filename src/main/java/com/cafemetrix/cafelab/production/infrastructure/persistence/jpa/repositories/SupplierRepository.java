package com.cafemetrix.cafelab.production.infrastructure.persistence.jpa.repositories;

import com.cafemetrix.cafelab.production.domain.model.aggregates.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByUserId(Long userId);

    Optional<Supplier> findByIdAndUserId(Long id, Long userId);

    boolean existsByNameValueAndUserId(String name, Long userId);

    @Query("SELECT COUNT(s) > 0 FROM Supplier s WHERE s.name.value = :name AND s.userId = :userId AND s.id != :excludeId")
    boolean existsByNameAndUserIdExcluding(@Param("name") String name, @Param("userId") Long userId, @Param("excludeId") Long excludeId);
}
