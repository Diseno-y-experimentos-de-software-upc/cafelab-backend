package com.cafemetrix.cafelab.production.domain.model.queries;

public record GetCoffeeLotVersionsByLineageIdQuery(Long lotLineageId) {
    public GetCoffeeLotVersionsByLineageIdQuery {
        if (lotLineageId == null || lotLineageId <= 0) {
            throw new IllegalArgumentException("LotLineageId es requerido y debe ser positivo");
        }
    }
}
