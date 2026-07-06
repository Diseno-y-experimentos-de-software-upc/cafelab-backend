package com.cafemetrix.cafelab.production.interfaces.acl;

import com.cafemetrix.cafelab.production.domain.model.aggregates.CoffeeLot;
import com.cafemetrix.cafelab.production.domain.model.aggregates.RoastProfile;
import com.cafemetrix.cafelab.production.domain.model.aggregates.Supplier;

import java.util.List;
import java.util.Optional;

public interface CoffeeproductionContextFacade {
    Long createSupplier(Long userId, String name, String email, Long phone, String location, List<String> specialties, String contactPerson, String webLink);

    Long updateSupplier(Long supplierId, String name, String email, Long phone, String location, List<String> specialties, String contactPerson, String webLink);

    boolean deleteSupplier(Long supplierId);

    List<Supplier> getAllSuppliers();

    Optional<Supplier> getSupplierById(Long supplierId);

    List<Supplier> getSuppliersByUserId(Long userId);

    Long createCoffeeLot(Long userId, Long supplierId, String lotName, String coffeeType, 
                              String processingMethod, Integer altitude, Double weight, 
                              String origin, String status, List<String> certifications);

    Long createCoffeeLotVersion(Long coffeeLotId, String lotName, String coffeeType,
                                String processingMethod, Integer altitude,
                                String origin, String status, List<String> certifications);

    Long updateCoffeeLotStock(Long coffeeLotId, Double weight);

    Long annullCoffeeLot(Long coffeeLotId, String reason);

    List<CoffeeLot> getAllCoffeeLots();

    List<CoffeeLot> getSelectableCoffeeLots();

    Optional<CoffeeLot> getCoffeeLotById(Long coffeeLotId);

    List<CoffeeLot> getCoffeeLotsByUserId(Long userId);

    List<CoffeeLot> getCoffeeLotsBySupplierId(Long supplierId);

    List<CoffeeLot> getCoffeeLotVersionsByLineageId(Long lotLineageId);

    Long createRoastProfile(Long userId, String name, String type, Integer duration, 
                                 Double tempStart, Double tempEnd, Long coffeeLotId, Boolean isFavorite);

    Long updateRoastProfile(Long roastProfileId, String name, String type, Integer duration, 
                                 Double tempStart, Double tempEnd, Long coffeeLotId, Boolean isFavorite);

    boolean deleteRoastProfile(Long roastProfileId);

    List<RoastProfile> getAllRoastProfiles();

    Optional<RoastProfile> getRoastProfileById(Long roastProfileId);

    List<RoastProfile> getRoastProfilesByUserId(Long userId);

    List<RoastProfile> getRoastProfilesByCoffeeLotId(Long coffeeLotId);
}
