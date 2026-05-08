package com.cafemetrix.cafelab.production.application.internal.commandservices;

import com.cafemetrix.cafelab.production.domain.exceptions.CoffeeLotNotFoundException;
import com.cafemetrix.cafelab.production.domain.exceptions.CoffeeLotOwnershipException;
import com.cafemetrix.cafelab.production.domain.model.aggregates.RoastProfile;
import com.cafemetrix.cafelab.production.domain.model.commands.CreateRoastProfileCommand;
import com.cafemetrix.cafelab.production.domain.model.commands.DeleteRoastProfileCommand;
import com.cafemetrix.cafelab.production.domain.model.commands.UpdateRoastProfileCommand;
import com.cafemetrix.cafelab.production.domain.services.RoastProfileCommandService;
import com.cafemetrix.cafelab.production.infrastructure.persistence.jpa.repositories.CoffeeLotRepository;
import com.cafemetrix.cafelab.production.infrastructure.persistence.jpa.repositories.RoastProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoastProfileCommandServiceImpl implements RoastProfileCommandService {
    private final RoastProfileRepository roastProfileRepository;
    private final CoffeeLotRepository coffeeLotRepository;

    public RoastProfileCommandServiceImpl(
            RoastProfileRepository roastProfileRepository, CoffeeLotRepository coffeeLotRepository) {
        this.roastProfileRepository = roastProfileRepository;
        this.coffeeLotRepository = coffeeLotRepository;
    }

    @Override
    public Optional<RoastProfile> handle(CreateRoastProfileCommand command) {
        if (command.tempStart() != null && command.tempEnd() != null
                && command.tempStart() >= command.tempEnd()) {
            throw new IllegalArgumentException(
                    "La temperatura inicial no puede ser mayor o igual que la temperatura final");
        }

        var coffeeLot = coffeeLotRepository.findById(command.coffeeLotId());
        if (coffeeLot.isEmpty()) {
            throw new CoffeeLotNotFoundException(command.coffeeLotId());
        }

        if (!coffeeLot.get().getUserId().equals(command.userId())) {
            throw new CoffeeLotOwnershipException();
        }

        var roastProfile = new RoastProfile(command);
        var savedRoastProfile = roastProfileRepository.save(roastProfile);
        return Optional.of(savedRoastProfile);
    }

    @Override
    public Optional<RoastProfile> handle(UpdateRoastProfileCommand command) {
        if (command.tempStart() != null && command.tempEnd() != null
                && command.tempStart() >= command.tempEnd()) {
            throw new IllegalArgumentException(
                    "La temperatura inicial no puede ser mayor o igual que la temperatura final");
        }

        var roastProfile = roastProfileRepository.findById(command.roastProfileId());
        if (roastProfile.isPresent()) {
            var coffeeLot = coffeeLotRepository.findById(command.coffeeLotId());
            if (coffeeLot.isEmpty()) {
                throw new CoffeeLotNotFoundException(command.coffeeLotId());
            }

            if (!coffeeLot.get().getUserId().equals(roastProfile.get().getUserId())) {
                throw new CoffeeLotOwnershipException("El lote de café no pertenece al usuario del perfil");
            }

            var updatedRoastProfile = roastProfile.get().update(command);
            var savedRoastProfile = roastProfileRepository.save(updatedRoastProfile);
            return Optional.of(savedRoastProfile);
        }
        return Optional.empty();
    }

    @Override
    public boolean handle(DeleteRoastProfileCommand command) {
        var roastProfile = roastProfileRepository.findById(command.roastProfileId());
        if (roastProfile.isPresent()) {
            roastProfileRepository.deleteById(command.roastProfileId());
            return true;
        }
        return false;
    }
}
