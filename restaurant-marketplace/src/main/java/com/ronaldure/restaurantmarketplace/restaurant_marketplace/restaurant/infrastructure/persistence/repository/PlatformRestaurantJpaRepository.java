package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.repository;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.entity.JpaRestaurantEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.projection.PlatformRestaurantCardProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.projection.PlatformRestaurantDetailProjection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Consultas de plataforma (sin restricciones de estado).
 * Filtros opcionales:
 * - statuses: si includeAllStatuses=true, ignora el filtro; si false, filtra
 * por IN (:statuses)
 * - city: exact match opcional
 * - q: búsqueda simple por name/slug (ILIKE emulado con lower+like)
 * - createdFrom/createdTo: rango de creación opcional
 */
public interface PlatformRestaurantJpaRepository extends JpaRepository<JpaRestaurantEntity, Long> {

        @Query("""
                        select r.id as id, r.name as name, r.slug as slug, r.status as status,
                               r.city as city, r.createdAt as createdAt
                        from JpaRestaurantEntity r
                        where
                          ( :includeAllStatuses = true or r.status in (:statuses) )
                          and ( :city is null or r.city = :city )
                          and ( :q is null or lower(r.name) like lower(concat('%', :q, '%'))
                                         or lower(r.slug) like lower(concat('%', :q, '%')) )
                          and ( :createdFrom is null or r.createdAt >= :createdFrom )
                          and ( :createdTo   is null or r.createdAt <  :createdTo )
                        """)
        Page<PlatformRestaurantCardProjection> listPlatform(
                        Pageable pageable,
                        boolean includeAllStatuses,
                        List<String> statuses,
                        String city,
                        String q,
                        Instant createdFrom,
                        Instant createdTo);
                        

        @Query("""
                        select r.id as id, r.name as name, r.slug as slug, r.status as status,
                               r.email as email, r.phone as phone,
                               r.addressLine1 as addressLine1, r.addressLine2 as addressLine2,
                               r.city as city, r.country as country, r.postalCode as postalCode,
                               r.openingHoursJson as openingHoursJson
                        from JpaRestaurantEntity r
                        where r.id = :id
                        """)
        Optional<PlatformRestaurantDetailProjection> getDetailById(Long id);
}
