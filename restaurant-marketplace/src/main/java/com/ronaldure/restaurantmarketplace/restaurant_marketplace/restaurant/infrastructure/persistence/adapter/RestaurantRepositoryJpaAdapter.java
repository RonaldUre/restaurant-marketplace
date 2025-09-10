package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out.RestaurantRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.Restaurant;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.model.vo.*;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.entity.JpaRestaurantEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.repository.RestaurantJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RestaurantRepositoryJpaAdapter implements RestaurantRepository {

    private final RestaurantJpaRepository jpaRepository;

    public RestaurantRepositoryJpaAdapter(RestaurantJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    // -------- Queries --------
    @Override
    public Optional<Restaurant> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Restaurant> findBySlug(String slug) {
        return jpaRepository.findBySlug(slug).map(this::toDomain);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return jpaRepository.existsBySlug(slug);
    }

    // -------- Save (MODIFICADO) --------
    @Override
    public Restaurant save(Restaurant restaurant) {
        JpaRestaurantEntity jpaEntity;

        if (restaurant.id() == null) {
            // Caso: Nuevo Restaurante (flujo de registro)
            // Creamos una nueva entidad JPA. JPA le asignará ID y Version al persistir.
            jpaEntity = new JpaRestaurantEntity();
            mapDomainToEntity(restaurant, jpaEntity); // Mapeamos los campos del dominio a la nueva entidad
        } else {
            // Caso: Restaurante existente (flujo de actualización, abrir, cerrar, suspender)
            // Cargamos la entidad existente desde la base de datos.
            // Esto asegura que la entidad esté "gestionada" por JPA y tenga su valor de version.
            jpaEntity = jpaRepository.findById(restaurant.id().value())
                    .orElseThrow(() -> new IllegalStateException("Restaurant entity not found for id: " + restaurant.id().value()));

            // Actualizamos los campos de la entidad cargada con los valores del objeto de dominio.
            mapDomainToEntity(restaurant, jpaEntity);
            // JPA automáticamente gestionará el campo 'version' al guardar.
        }

        // Guardar la entidad. Si es nueva, JPA asigna ID y version.
        // Si es existente, JPA actualiza y incrementa la version.
        JpaRestaurantEntity savedEntity = jpaRepository.save(jpaEntity);

        // Volvemos a mapear la entidad guardada al objeto de dominio para devolver
        // el agregado con el ID (si era nuevo) y el estado actualizado.
        return toDomain(savedEntity);
    }

    // -------- Helper para mapear Domain -> JPA (reemplaza el constructor directo para actualizaciones) --------
    private void mapDomainToEntity(Restaurant domainRestaurant, JpaRestaurantEntity jpaEntity) {
        // Id will be set by JPA on initial save, or already exists on loaded entity
        // jpaEntity.setId(domainRestaurant.id() != null ? domainRestaurant.id().value() : null); // No seteamos el ID aquí, JPA lo gestiona

        jpaEntity.setName(domainRestaurant.name().value());
        jpaEntity.setSlug(domainRestaurant.slug().value());
        jpaEntity.setStatus(domainRestaurant.status().name());

        jpaEntity.setEmail(domainRestaurant.email() != null ? domainRestaurant.email().value() : null);
        jpaEntity.setPhone(domainRestaurant.phone() != null ? domainRestaurant.phone().value() : null);

        // Address fields
        if (domainRestaurant.address() != null) {
            jpaEntity.setAddressLine1(domainRestaurant.address().line1());
            jpaEntity.setAddressLine2(domainRestaurant.address().line2());
            jpaEntity.setCity(domainRestaurant.address().city());
            jpaEntity.setCountry(domainRestaurant.address().country());
            jpaEntity.setPostalCode(domainRestaurant.address().postalCode());
        } else {
            // If address is null in domain, ensure address fields are null in entity
            jpaEntity.setAddressLine1(null);
            jpaEntity.setAddressLine2(null);
            jpaEntity.setCity(null);
            jpaEntity.setCountry(null);
            jpaEntity.setPostalCode(null);
        }

        jpaEntity.setOpeningHoursJson(domainRestaurant.openingHours() != null ? domainRestaurant.openingHours().json() : null);
        // El campo 'version' lo gestiona JPA automáticamente.
        // El campo 'createdAt' lo gestiona @CreationTimestamp.
    }


    // -------- Mapping: JPA -> Domain (SIN CAMBIOS) --------
    private Restaurant toDomain(JpaRestaurantEntity e) {
        return Restaurant.rehydrate(
                e.getId() != null ? RestaurantId.of(e.getId()) : null,
                Name.of(e.getName()),
                Slug.of(e.getSlug()),
                e.getEmail() != null ? Email.of(e.getEmail()) : null,
                e.getPhone() != null ? Phone.of(e.getPhone()) : null,
                Address.of(e.getAddressLine1(), e.getAddressLine2(), e.getCity(), e.getCountry(), e.getPostalCode()),
                e.getOpeningHoursJson() != null ? OpeningHours.of(e.getOpeningHoursJson()) : null,
                Status.valueOf(e.getStatus())
        );
    }
}