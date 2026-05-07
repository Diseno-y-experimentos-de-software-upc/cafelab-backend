package com.cafemetrix.cafelab.management.domain.model.commands;

/**
 * Marca un registro de costo como {@code anulado} sin borrarlo (queda para auditoría).
 * El campo {@code reason} contiene el motivo que el usuario seleccionó (o escribió en «otro»).
 */
public record AnnullProductionCostRecordCommand(Long id, String reason) {}
