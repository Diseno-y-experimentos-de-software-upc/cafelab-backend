package com.cafemetrix.cafelab.production.application.internal.commandservices;

import com.cafemetrix.cafelab.production.domain.model.aggregates.CoffeeLot;
import com.cafemetrix.cafelab.production.domain.model.commands.CreateCoffeeLotCommand;
import com.cafemetrix.cafelab.production.domain.model.commands.DeleteCoffeeLotCommand;
import com.cafemetrix.cafelab.production.domain.model.commands.UpdateCoffeeLotCommand;
import com.cafemetrix.cafelab.production.domain.services.CoffeeLotCommandService;
import com.cafemetrix.cafelab.production.infrastructure.persistence.jpa.repositories.CoffeeLotRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CoffeeLotCommandServiceImpl implements CoffeeLotCommandService {
    private final CoffeeLotRepository coffeeLotRepository;

    public CoffeeLotCommandServiceImpl(CoffeeLotRepository coffeeLotRepository) {
        this.coffeeLotRepository = coffeeLotRepository;
    }

    @Override
    public Optional<CoffeeLot> handle(CreateCoffeeLotCommand command) {
        if (coffeeLotRepository.existsByLotNameValueAndUserId(command.lotName(), command.userId())) {
            throw new IllegalArgumentException(
                    "Ya existe un lote con el nombre \"" + command.lotName() + "\". Use un nombre diferente.");
        }
        try {
            var coffeeLot = new CoffeeLot(command);
            return Optional.of(coffeeLotRepository.save(coffeeLot));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<CoffeeLot> handle(UpdateCoffeeLotCommand command) {
        var existingCoffeeLot = coffeeLotRepository.findById(command.coffeeLotId());
        if (existingCoffeeLot.isEmpty()) {
            return Optional.empty();
        }
        Long userId = existingCoffeeLot.get().getUserId();
        if (coffeeLotRepository.existsByLotNameAndUserIdExcluding(command.lotName(), userId, command.coffeeLotId())) {
            throw new IllegalArgumentException(
                    "Ya existe un lote con el nombre \"" + command.lotName() + "\". Use un nombre diferente.");
        }
        try {
            var coffeeLot = existingCoffeeLot.get();
            coffeeLot.update(command);
            return Optional.of(coffeeLotRepository.save(coffeeLot));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean handle(DeleteCoffeeLotCommand command) {
        var coffeeLot = coffeeLotRepository.findById(command.coffeeLotId());
        if (coffeeLot.isEmpty()) {
            return false;
        }
        coffeeLot.get().softDelete();
        coffeeLotRepository.save(coffeeLot.get());
        return true;
    }
}
