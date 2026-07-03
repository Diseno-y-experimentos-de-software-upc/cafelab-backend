package com.cafemetrix.cafelab.production.interfaces.rest.resources;

import java.util.List;

/** DTO; {@code userId} corresponde a columna {@code user_id} en BD. */
public record SupplierResource(
    Long id,
    Long userId,
    String name,
    String email,
    Long phone,
    String location,
    List<String> specialties,
    String contactPerson,
    String webLink
) {}
