package com.cafemetrix.cafelab.calibrations.application.internal.commandservices;

import com.cafemetrix.cafelab.calibrations.domain.model.aggregates.GrindCalibration;
import com.cafemetrix.cafelab.calibrations.domain.model.commands.CreateGrindCalibrationCommand;
import com.cafemetrix.cafelab.calibrations.domain.model.commands.DeleteGrindCalibrationCommand;
import com.cafemetrix.cafelab.calibrations.domain.model.commands.UpdateGrindCalibrationCommand;
import com.cafemetrix.cafelab.calibrations.domain.services.GrindCalibrationCommandService;
import com.cafemetrix.cafelab.calibrations.infrastructure.persistence.jpa.repositories.GrindCalibrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class GrindCalibrationCommandServiceImpl implements GrindCalibrationCommandService {
    private final GrindCalibrationRepository repository;

    public GrindCalibrationCommandServiceImpl(GrindCalibrationRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Optional<GrindCalibration> handle(CreateGrindCalibrationCommand command) {
        var entity = new GrindCalibration(command);
        return Optional.of(repository.save(entity));
    }

    @Override
    @Transactional
    public Optional<GrindCalibration> handle(UpdateGrindCalibrationCommand command) {
        return repository
                .findByIdAndUserId(command.calibrationId(), command.userId())
                .map(
                        entity -> {
                            entity.applyUpdate(command);
                            return repository.save(entity);
                        });
    }

    @Override
    @Transactional
    public boolean handle(DeleteGrindCalibrationCommand command) {
        return repository
                .findByIdAndUserId(command.calibrationId(), command.userId())
                .map(
                        entity -> {
                            repository.delete(entity);
                            return true;
                        })
                .orElse(false);
    }
}
