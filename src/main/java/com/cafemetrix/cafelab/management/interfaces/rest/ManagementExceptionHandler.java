package com.cafemetrix.cafelab.management.interfaces.rest;

import com.cafemetrix.cafelab.management.domain.exceptions.InsufficientCoffeeLotStockException;
import com.cafemetrix.cafelab.management.domain.exceptions.InventoryAccessDeniedException;
import com.cafemetrix.cafelab.management.domain.exceptions.InventoryEntryNotFoundException;
import com.cafemetrix.cafelab.management.domain.exceptions.ProductionCostRecordNotFoundException;
import com.cafemetrix.cafelab.production.domain.exceptions.CoffeeLotNotFoundException;
import com.cafemetrix.cafelab.production.domain.exceptions.CoffeeLotOwnershipException;
import com.cafemetrix.cafelab.shared.interfaces.rest.resources.MessageResource;
import com.cafemetrix.cafelab.shared.interfaces.rest.support.CafeLabScopedExceptionHandlerSupport;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.cafemetrix.cafelab.management.interfaces.rest")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ManagementExceptionHandler extends CafeLabScopedExceptionHandlerSupport {

    @ExceptionHandler(InventoryEntryNotFoundException.class)
    public ResponseEntity<MessageResource> handleInventoryEntryNotFound(InventoryEntryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResource(ex.getMessage()));
    }

    @ExceptionHandler(ProductionCostRecordNotFoundException.class)
    public ResponseEntity<MessageResource> handleProductionCostRecordNotFound(
            ProductionCostRecordNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResource(ex.getMessage()));
    }

    @ExceptionHandler(InventoryAccessDeniedException.class)
    public ResponseEntity<MessageResource> handleInventoryAccessDenied(InventoryAccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResource(ex.getMessage()));
    }

    @ExceptionHandler(InsufficientCoffeeLotStockException.class)
    public ResponseEntity<MessageResource> handleInsufficientStock(InsufficientCoffeeLotStockException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResource(ex.getMessage()));
    }

    @ExceptionHandler(CoffeeLotNotFoundException.class)
    public ResponseEntity<MessageResource> handleCoffeeLotNotFound(CoffeeLotNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResource(ex.getMessage()));
    }

    @ExceptionHandler(CoffeeLotOwnershipException.class)
    public ResponseEntity<MessageResource> handleCoffeeLotOwnership(CoffeeLotOwnershipException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResource(ex.getMessage()));
    }
}
