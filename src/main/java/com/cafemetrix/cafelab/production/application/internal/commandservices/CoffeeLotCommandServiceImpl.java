package com.cafemetrix.cafelab.production.application.internal.commandservices;

import com.cafemetrix.cafelab.production.domain.model.aggregates.CoffeeLot;
import com.cafemetrix.cafelab.production.domain.model.commands.AnnullCoffeeLotCommand;
import com.cafemetrix.cafelab.production.domain.model.commands.CreateCoffeeLotCommand;
import com.cafemetrix.cafelab.production.domain.model.commands.CreateCoffeeLotVersionCommand;
import com.cafemetrix.cafelab.production.domain.model.commands.UpdateCoffeeLotStockCommand;
import com.cafemetrix.cafelab.production.domain.services.CoffeeLotCommandService;
import com.cafemetrix.cafelab.production.infrastructure.persistence.jpa.repositories.CoffeeLotRepository;
import com.cafemetrix.cafelab.production.infrastructure.persistence.jpa.repositories.SupplierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CoffeeLotCommandServiceImpl implements CoffeeLotCommandService {
    private static final Logger log = LoggerFactory.getLogger(CoffeeLotCommandServiceImpl.class);

    private final CoffeeLotRepository coffeeLotRepository;
    private final SupplierRepository supplierRepository;

    public CoffeeLotCommandServiceImpl(
            CoffeeLotRepository coffeeLotRepository,
            SupplierRepository supplierRepository) {
        this.coffeeLotRepository = coffeeLotRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    public Optional<CoffeeLot> handle(CreateCoffeeLotCommand command) {
        if (coffeeLotRepository.existsCurrentByLotNameValueAndUserId(command.lotName(), command.userId())) {
            throw new IllegalArgumentException(
                    "Ya existe un lote con el nombre \"" + command.lotName() + "\". Use un nombre diferente.");
        }
        try {
            var supplier = supplierRepository.findById(command.supplierId());
            if (supplier.isEmpty()) {
                throw new IllegalArgumentException("Proveedor no encontrado");
            }
            var coffeeLot = new CoffeeLot(command, supplier.get().getName());
            // Placeholder único hasta obtener el id real; evita choque en uq_coffee_lot_lineage_version.
            coffeeLot.initializeAsFirstVersion(-System.currentTimeMillis());
            var saved = coffeeLotRepository.save(coffeeLot);
            saved.initializeAsFirstVersion(saved.getId());
            return Optional.of(coffeeLotRepository.save(saved));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("No se pudo crear el lote de café para userId={} lotName={}", command.userId(), command.lotName(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<CoffeeLot> handle(CreateCoffeeLotVersionCommand command) {
        var existingCoffeeLot = coffeeLotRepository.findById(command.coffeeLotId());
        if (existingCoffeeLot.isEmpty()) {
            return Optional.empty();
        }
        var current = existingCoffeeLot.get();
        if (!Boolean.TRUE.equals(current.getIsCurrent())) {
            throw new IllegalArgumentException("Solo se puede versionar la version actual del lote");
        }
        if (current.isAnnulled()) {
            throw new IllegalArgumentException("No se puede modificar un lote anulado");
        }

        Long userId = current.getUserId();
        if (coffeeLotRepository.existsCurrentByLotNameValueAndUserIdExcludingLineage(
                command.lotName(), userId, current.getLotLineageId())) {
            throw new IllegalArgumentException(
                    "Ya existe un lote con el nombre \"" + command.lotName() + "\". Use un nombre diferente.");
        }

        try {
            current.markAsHistorical();
            coffeeLotRepository.save(current);
            var nextVersion = current.createSuccessor(command);
            return Optional.of(coffeeLotRepository.save(nextVersion));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<CoffeeLot> handle(UpdateCoffeeLotStockCommand command) {
        var existingCoffeeLot = coffeeLotRepository.findById(command.coffeeLotId());
        if (existingCoffeeLot.isEmpty()) {
            return Optional.empty();
        }
        try {
            var coffeeLot = existingCoffeeLot.get();
            if (!Boolean.TRUE.equals(coffeeLot.getIsCurrent())) {
                throw new IllegalArgumentException("Solo se puede ajustar stock en la version actual del lote");
            }
            if (coffeeLot.isAnnulled()) {
                throw new IllegalArgumentException("No se puede ajustar stock de un lote anulado");
            }
            coffeeLot.adjustCurrentWeight(command.weight());
            return Optional.of(coffeeLotRepository.save(coffeeLot));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<CoffeeLot> handle(AnnullCoffeeLotCommand command) {
        var coffeeLot = coffeeLotRepository.findById(command.coffeeLotId());
        if (coffeeLot.isEmpty()) {
            return Optional.empty();
        }
        var versions = coffeeLotRepository.findByLotLineageIdOrderByVersionNumberDesc(
                coffeeLot.get().getLotLineageId());
        if (versions.isEmpty()) {
            return Optional.empty();
        }
        var current = versions.stream()
                .filter(v -> Boolean.TRUE.equals(v.getIsCurrent()))
                .findFirst()
                .orElse(versions.get(0));
        if (current.isAnnulled()) {
            throw new IllegalArgumentException("El lote ya está anulado");
        }
        try {
            for (CoffeeLot version : versions) {
                version.annull(command.reason());
                coffeeLotRepository.save(version);
            }
            return coffeeLotRepository.findById(current.getId());
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
