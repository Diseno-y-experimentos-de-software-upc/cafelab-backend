package com.cafemetrix.cafelab.calibrations.domain.model.aggregates;

import com.cafemetrix.cafelab.calibrations.domain.model.commands.CreateGrindCalibrationCommand;
import com.cafemetrix.cafelab.calibrations.domain.model.commands.UpdateGrindCalibrationCommand;
import com.cafemetrix.cafelab.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

/**
 * Registro de calibración de molienda: sin vínculos a lotes ni perfiles de tueste; ámbito por {@code userId}.
 */
@Entity
@SQLRestriction("deleted_at IS NULL")
public class GrindCalibration extends AuditableAbstractAggregateRoot<GrindCalibration> {

    @Getter
    @Column(name = "profile_id", nullable = false)
    private Long userId;

    @Getter
    @Column(nullable = false, length = 255)
    private String name;

    @Getter
    @Column(nullable = false, length = 120)
    private String method;

    @Getter
    @Column(nullable = false, length = 120)
    private String equipment;

    @Getter
    @Column(name = "grind_number", nullable = false, length = 64)
    private String grindNumber;

    @Getter
    @Column(nullable = false)
    private Double aperture;

    @Getter
    @Column(name = "cup_volume", nullable = false)
    private Double cupVolume;

    @Getter
    @Column(name = "final_volume", nullable = false)
    private Double finalVolume;

    @Getter
    @Column(name = "calibration_date", nullable = false)
    private LocalDate calibrationDate;

    @Getter
    @Column(columnDefinition = "TEXT")
    private String comments;

    @Getter
    @Column(columnDefinition = "TEXT")
    private String notes;

    @Getter
    @Lob
    @Column(name = "sample_image", columnDefinition = "LONGTEXT")
    private String sampleImage;

    public GrindCalibration() {}

    public GrindCalibration(CreateGrindCalibrationCommand c) {
        this.userId = c.userId();
        this.name = c.name().trim();
        this.method = c.method().trim();
        this.equipment = c.equipment().trim();
        this.grindNumber = c.grindNumber().trim();
        this.aperture = c.aperture();
        this.cupVolume = c.cupVolume();
        this.finalVolume = c.finalVolume();
        this.calibrationDate = c.calibrationDate();
        this.comments = blankToNull(c.comments());
        this.notes = blankToNull(c.notes());
        this.sampleImage = blankToNull(c.sampleImage());
    }

    public void applyUpdate(UpdateGrindCalibrationCommand c) {
        this.name = c.name().trim();
        this.method = c.method().trim();
        this.equipment = c.equipment().trim();
        this.grindNumber = c.grindNumber().trim();
        this.aperture = c.aperture();
        this.cupVolume = c.cupVolume();
        this.finalVolume = c.finalVolume();
        this.calibrationDate = c.calibrationDate();
        this.comments = blankToNull(c.comments());
        this.notes = blankToNull(c.notes());
        this.sampleImage = blankToNull(c.sampleImage());
    }

    private static String blankToNull(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return s.trim();
    }
}
