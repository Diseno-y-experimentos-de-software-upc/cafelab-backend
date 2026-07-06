package com.cafemetrix.cafelab.production.application.internal.queryservices;

import com.cafemetrix.cafelab.production.domain.model.aggregates.CoffeeLot;
import com.cafemetrix.cafelab.production.domain.model.queries.GetAllCoffeeLotsQuery;
import com.cafemetrix.cafelab.production.domain.model.queries.GetCoffeeLotByIdQuery;
import com.cafemetrix.cafelab.production.domain.model.queries.GetCoffeeLotVersionsByLineageIdQuery;
import com.cafemetrix.cafelab.production.domain.model.queries.GetCoffeeLotsBySupplierIdQuery;
import com.cafemetrix.cafelab.production.domain.model.queries.GetCoffeeLotsByUserIdQuery;
import com.cafemetrix.cafelab.production.domain.model.queries.GetSelectableCoffeeLotsQuery;
import com.cafemetrix.cafelab.production.domain.services.CoffeeLotQueryService;
import com.cafemetrix.cafelab.production.infrastructure.persistence.jpa.repositories.CoffeeLotRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CoffeeLotQueryServiceImpl implements CoffeeLotQueryService {
    private final CoffeeLotRepository coffeeLotRepository;

    public CoffeeLotQueryServiceImpl(CoffeeLotRepository coffeeLotRepository) {
        this.coffeeLotRepository = coffeeLotRepository;
    }

    @Override
    public List<CoffeeLot> handle(GetAllCoffeeLotsQuery query) {
        return coffeeLotRepository.findByIsCurrentTrue();
    }

    @Override
    public Optional<CoffeeLot> handle(GetCoffeeLotByIdQuery query) {
        return coffeeLotRepository.findById(query.coffeeLotId());
    }

    @Override
    public List<CoffeeLot> handle(GetCoffeeLotsByUserIdQuery query) {
        return coffeeLotRepository.findByUserIdAndIsCurrentTrue(query.userId());
    }

    @Override
    public List<CoffeeLot> handle(GetCoffeeLotsBySupplierIdQuery query) {
        return coffeeLotRepository.findBySupplierIdAndIsCurrentTrue(query.supplierId());
    }

    @Override
    public List<CoffeeLot> handle(GetCoffeeLotVersionsByLineageIdQuery query) {
        return coffeeLotRepository.findByLotLineageIdOrderByVersionNumberDesc(query.lotLineageId());
    }

    @Override
    public List<CoffeeLot> handle(GetSelectableCoffeeLotsQuery query) {
        return coffeeLotRepository.findByIsCurrentTrueAndRecordStatus(CoffeeLot.RECORD_STATUS_ACTIVE);
    }
}
