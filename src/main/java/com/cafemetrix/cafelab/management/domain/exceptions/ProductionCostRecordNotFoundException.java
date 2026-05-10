package com.cafemetrix.cafelab.management.domain.exceptions;

public class ProductionCostRecordNotFoundException extends RuntimeException {
    public ProductionCostRecordNotFoundException(Long id) {
        super("Registro de costo de producción no encontrado: " + id);
    }
}
