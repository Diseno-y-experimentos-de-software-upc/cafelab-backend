package com.cafemetrix.cafelab.calibrations.domain.services;

import com.cafemetrix.cafelab.calibrations.domain.model.aggregates.GrindCalibration;
import com.cafemetrix.cafelab.calibrations.domain.model.commands.CreateGrindCalibrationCommand;
import com.cafemetrix.cafelab.calibrations.domain.model.commands.DeleteGrindCalibrationCommand;
import com.cafemetrix.cafelab.calibrations.domain.model.commands.UpdateGrindCalibrationCommand;

import java.util.Optional;

public interface GrindCalibrationCommandService {

    Optional<GrindCalibration> handle(CreateGrindCalibrationCommand command);

    Optional<GrindCalibration> handle(UpdateGrindCalibrationCommand command);

    boolean handle(DeleteGrindCalibrationCommand command);
}
