package com.cafemetrix.cafelab.production.domain.services;

import com.cafemetrix.cafelab.production.domain.model.aggregates.CoffeeLot;
import com.cafemetrix.cafelab.production.domain.model.commands.AnnullCoffeeLotCommand;
import com.cafemetrix.cafelab.production.domain.model.commands.CreateCoffeeLotCommand;
import com.cafemetrix.cafelab.production.domain.model.commands.CreateCoffeeLotVersionCommand;
import com.cafemetrix.cafelab.production.domain.model.commands.UpdateCoffeeLotStockCommand;

import java.util.Optional;

public interface CoffeeLotCommandService {
    Optional<CoffeeLot> handle(CreateCoffeeLotCommand command);
    Optional<CoffeeLot> handle(CreateCoffeeLotVersionCommand command);
    Optional<CoffeeLot> handle(UpdateCoffeeLotStockCommand command);
    Optional<CoffeeLot> handle(AnnullCoffeeLotCommand command);
}
