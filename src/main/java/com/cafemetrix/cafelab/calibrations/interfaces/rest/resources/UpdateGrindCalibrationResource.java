package com.cafemetrix.cafelab.calibrations.interfaces.rest.resources;

import java.time.LocalDate;

public record UpdateGrindCalibrationResource(
        String name,
        String method,
        String equipment,
        String grindNumber,
        Double aperture,
        Double cupVolume,
        Double finalVolume,
        LocalDate calibrationDate,
        String comments,
        String notes,
        String sampleImage
) {
    public UpdateGrindCalibrationResource {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (method == null || method.isBlank()) {
            throw new IllegalArgumentException("method is required");
        }
        if (equipment == null || equipment.isBlank()) {
            throw new IllegalArgumentException("equipment is required");
        }
        if (grindNumber == null || grindNumber.isBlank()) {
            throw new IllegalArgumentException("grindNumber is required");
        }
        if (aperture == null || aperture < 0) {
            throw new IllegalArgumentException("aperture must be >= 0");
        }
        if (cupVolume == null || cupVolume < 0) {
            throw new IllegalArgumentException("cupVolume must be >= 0");
        }
        if (finalVolume == null || finalVolume < 0) {
            throw new IllegalArgumentException("finalVolume must be >= 0");
        }
        if (calibrationDate == null) {
            throw new IllegalArgumentException("calibrationDate is required");
        }
        if (calibrationDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La fecha de calibración debe ser hoy o una fecha futura.");
        }
    }
}
