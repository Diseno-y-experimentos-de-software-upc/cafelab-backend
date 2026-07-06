package com.cafemetrix.cafelab.production.domain.services;

import com.cafemetrix.cafelab.production.domain.model.aggregates.CoffeeLot;
import com.cafemetrix.cafelab.production.domain.model.queries.GetAllCoffeeLotsQuery;
import com.cafemetrix.cafelab.production.domain.model.queries.GetCoffeeLotByIdQuery;
import com.cafemetrix.cafelab.production.domain.model.queries.GetCoffeeLotVersionsByLineageIdQuery;
import com.cafemetrix.cafelab.production.domain.model.queries.GetCoffeeLotsBySupplierIdQuery;
import com.cafemetrix.cafelab.production.domain.model.queries.GetCoffeeLotsByUserIdQuery;
import com.cafemetrix.cafelab.production.domain.model.queries.GetSelectableCoffeeLotsQuery;

import java.util.List;
import java.util.Optional;

public interface CoffeeLotQueryService {
    List<CoffeeLot> handle(GetAllCoffeeLotsQuery query);
    Optional<CoffeeLot> handle(GetCoffeeLotByIdQuery query);
    List<CoffeeLot> handle(GetCoffeeLotsByUserIdQuery query);
    List<CoffeeLot> handle(GetCoffeeLotsBySupplierIdQuery query);
    List<CoffeeLot> handle(GetCoffeeLotVersionsByLineageIdQuery query);
    List<CoffeeLot> handle(GetSelectableCoffeeLotsQuery query);
}
