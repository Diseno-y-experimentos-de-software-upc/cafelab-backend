package com.cafemetrix.cafelab.profiles.infrastructure.persistence.jpa.repositories;

import com.cafemetrix.cafelab.profiles.domain.model.aggregates.Profile;
import com.cafemetrix.cafelab.profiles.domain.model.valueobjects.EmailAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    /**
     * Find a Profile by Email Address
     *
     * @param emailAddress The Email Address
     * @return A {@link Profile} instance if the email address is valid, otherwise empty
     */
    Optional<Profile> findByEmailAddress(EmailAddress emailAddress);

    
    @Query("SELECT p FROM Profile p WHERE LOWER(TRIM(p.emailAddress.address)) = :email")
    Optional<Profile> findByNormalizedEmail(@Param("email") String normalizedEmail);

    Optional<Profile> findByIamUserId(Long iamUserId);

    List<Profile> findByIamUserIdIsNull();

    /**
     * Check if a Profile exists by Email Address
     *
     * @param emailAddress The Email Address
     * @return True if the email address exists, otherwise false
     */
    boolean existsByEmailAddress(EmailAddress emailAddress);

    /**
     * ¿Existe otro perfil con el mismo email (case-insensitive, trim) cuyo id sea distinto al provisto?
     * Si {@code excludingId} es {@code null}, considera todos los perfiles.
     */
    @Query("SELECT (COUNT(p) > 0) FROM Profile p "
            + "WHERE LOWER(TRIM(p.emailAddress.address)) = :email "
            + "AND (:excludingId IS NULL OR p.id <> :excludingId)")
    boolean existsByNormalizedEmailExcludingId(
            @Param("email") String normalizedEmail,
            @Param("excludingId") Long excludingId);

    /**
     * ¿Existe otro perfil con el mismo nombre (case-insensitive, trim) cuyo id sea distinto al
     * provisto? Útil para validar el cambio de "name".
     */
    @Query("SELECT (COUNT(p) > 0) FROM Profile p "
            + "WHERE LOWER(TRIM(p.name)) = :name "
            + "AND (:excludingId IS NULL OR p.id <> :excludingId)")
    boolean existsByNormalizedNameExcludingId(
            @Param("name") String normalizedName,
            @Param("excludingId") Long excludingId);

    /**
     * ¿Existe otro perfil con el mismo {@code cafeteriaName} (case-insensitive, trim) cuyo id sea
     * distinto al provisto? Si {@code excludingId} es {@code null}, considera todos los perfiles.
     */
    @Query("SELECT (COUNT(p) > 0) FROM Profile p "
            + "WHERE LOWER(TRIM(p.cafeteriaName)) = :cafeteriaName "
            + "AND (:excludingId IS NULL OR p.id <> :excludingId)")
    boolean existsByNormalizedCafeteriaNameExcludingId(
            @Param("cafeteriaName") String normalizedCafeteriaName,
            @Param("excludingId") Long excludingId);
}
