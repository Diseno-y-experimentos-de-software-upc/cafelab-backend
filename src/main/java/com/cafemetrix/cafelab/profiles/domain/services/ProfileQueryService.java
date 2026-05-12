package com.cafemetrix.cafelab.profiles.domain.services;

import com.cafemetrix.cafelab.profiles.domain.model.aggregates.Profile;
import com.cafemetrix.cafelab.profiles.domain.model.queries.CheckProfileFieldAvailabilityQuery;
import com.cafemetrix.cafelab.profiles.domain.model.queries.GetAllProfilesQuery;
import com.cafemetrix.cafelab.profiles.domain.model.queries.GetProfileByEmailQuery;
import com.cafemetrix.cafelab.profiles.domain.model.queries.GetProfileByIamUserIdQuery;
import com.cafemetrix.cafelab.profiles.domain.model.queries.GetProfileByIdQuery;
import com.cafemetrix.cafelab.profiles.domain.model.valueobjects.ProfileFieldsAvailability;

import java.util.List;
import java.util.Optional;

public interface ProfileQueryService {
    /**
     * Handle Get Profile By ID Query
     *
     * @param query The {@link GetProfileByIdQuery} Query
     * @return A {@link Profile} instance if the query is valid, otherwise empty
     */
    Optional<Profile> handle(GetProfileByIdQuery query);

    /**
     * Handle Get Profile By Email Query
     *
     * @param query The {@link GetProfileByEmailQuery} Query
     * @return A {@link Profile} instance if the query is valid, otherwise empty
     */
    Optional<Profile> handle(GetProfileByEmailQuery query);

    /**
     * Perfil asociado al usuario IAM (columna {@code profiles.user_id}).
     */
    Optional<Profile> handle(GetProfileByIamUserIdQuery query);

    /**
     * Handle Get All Profiles Query
     *
     * @param query The {@link GetAllProfilesQuery} Query
     * @return A list of {@link Profile} instances
     */
    List<Profile> handle(GetAllProfilesQuery query);

    /**
     * Verifica para los campos no nulos/blancos de la query si ya están siendo usados por otro
     * perfil distinto al identificado por {@code excludingUserId}.
     */
    ProfileFieldsAvailability handle(CheckProfileFieldAvailabilityQuery query);
}
