package com.cafemetrix.cafelab.production.domain.model.commands;

import java.util.List;

public record CreateSupplierCommand(
    Long userId,
    String name,
    String email,
    Long phone,
    String location,
    List<String> specialties
) {
    public CreateSupplierCommand {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("userId es requerido y debe ser positivo");
        }
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name es requerido");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email es requerido");
        if (phone == null) {
            throw new IllegalArgumentException("Phone es requerido");
        }
        if (phone <= 0) {
            throw new IllegalArgumentException("Phone debe ser positivo");
        }
        var phoneText = String.valueOf(phone);
        if (phoneText.length() < 7 || phoneText.length() > 15) {
            throw new IllegalArgumentException("Phone debe tener entre 7 y 15 dígitos");
        }
        if (location == null || location.isBlank()) throw new IllegalArgumentException("Location es requerido");
        if (specialties != null && specialties.size() > 4) throw new IllegalArgumentException("No se pueden tener más de 4 especialidades");
        if (specialties != null) {
            var normalizedSpecialties = specialties.stream()
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .toList();

            if (normalizedSpecialties.stream().distinct().count() != normalizedSpecialties.size()) {
                throw new IllegalArgumentException("No se pueden repetir especialidades");
            }
        }
    }
}
