// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/infrastructure/persistence/repository/PublicRestaurantJpaRepository.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.repository;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.entity.JpaRestaurantEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.projection.PublicRestaurantCardProjection;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.projection.PublicRestaurantDetailProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * Read-optimized queries for public endpoints (no aggregate rehydration).
 * Uses interface-based projections to fetch only the needed columns.
 */
public interface PublicRestaurantJpaRepository extends JpaRepository<JpaRestaurantEntity, Long> {

    @Query("""
            select r.id as id, r.name as name, r.slug as slug, r.status as status, r.city as city
            from JpaRestaurantEntity r
            where r.status = 'OPEN'
              and (:city is null or r.city = :city)
            """)
    Page<PublicRestaurantCardProjection> listOpen(Pageable pageable, String city);

    @Query("""
            select r.id as id, r.name as name, r.slug as slug, r.status as status,
                   r.email as email, r.phone as phone,
                   r.addressLine1 as addressLine1, r.addressLine2 as addressLine2,
                   r.city as city, r.country as country, r.postalCode as postalCode,
                   r.openingHoursJson as openingHoursJson
            from JpaRestaurantEntity r
            where r.status in ('OPEN','CLOSED') 
              and r.slug = :slug
            """)
    Optional<PublicRestaurantDetailProjection> getDetailBySlug(String slug);

    @Query("""
            select r.id as id, r.name as name, r.slug as slug, r.status as status,
                   r.email as email, r.phone as phone,
                   r.addressLine1 as addressLine1, r.addressLine2 as addressLine2,
                   r.city as city, r.country as country, r.postalCode as postalCode,
                   r.openingHoursJson as openingHoursJson
            from JpaRestaurantEntity r
            where r.status in ('OPEN','CLOSED')
              and r.id = :id
            """)
    Optional<PublicRestaurantDetailProjection> getDetailById(Long id);

    boolean existsBySlug(String slug);
}
