package com.cafemetrix.cafelab.production.interfaces.rest.resources;

import java.util.List;

public record CoffeeLotResource(
    Long id,
    Long userId,
    Long supplierId,
    String supplierName,
    Long lotLineageId,
    Integer versionNumber,
    Boolean isCurrent,
    Long supersedesId,
    String recordStatus,
    String annulmentReason,
    String lotName,
    String coffeeType,
    String processingMethod,
    Integer altitude,
    Double weight,
    Double originalWeight,
    String origin,
    String status,
    List<String> certifications
) {}
