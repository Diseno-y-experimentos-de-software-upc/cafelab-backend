package com.cafemetrix.cafelab.defects.application.internal.commandservices;

import com.cafemetrix.cafelab.defects.domain.model.aggregates.Defect;
import com.cafemetrix.cafelab.defects.domain.model.commands.CreateDefectCommand;
import com.cafemetrix.cafelab.defects.domain.model.commands.DeleteDefectCommand;
import com.cafemetrix.cafelab.defects.domain.model.commands.UpdateDefectCommand;
import com.cafemetrix.cafelab.defects.domain.services.DefectCommandService;
import com.cafemetrix.cafelab.defects.infrastructure.persistence.jpa.repositories.DefectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class DefectCommandServiceImpl implements DefectCommandService {
    private final DefectRepository defectRepository;

    /**
     * Constructor
     *
     * @param defectRepository The {@link DefectRepository} instance
     */
    public DefectCommandServiceImpl(DefectRepository defectRepository) {
        this.defectRepository = defectRepository;
    }

    @Override
    public Optional<Defect> handle(CreateDefectCommand command) {
        var defect = new Defect(command);
        defectRepository.save(defect);
        return Optional.of(defect);
    }

    @Override
    @Transactional
    public Optional<Defect> handle(UpdateDefectCommand command) {
        return defectRepository
                .findByIdAndUserId(command.defectId(), command.userId())
                .map(
                        entity -> {
                            entity.applyUpdate(command);
                            return defectRepository.save(entity);
                        });
    }

    @Override
    @Transactional
    public boolean handle(DeleteDefectCommand command) {
        return defectRepository
                .findByIdAndUserId(command.defectId(), command.userId())
                .map(
                        entity -> {
                            entity.softDelete();
                            defectRepository.save(entity);
                            return true;
                        })
                .orElse(false);
    }
}
