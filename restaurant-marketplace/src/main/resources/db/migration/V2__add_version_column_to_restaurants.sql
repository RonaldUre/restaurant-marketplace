-- V2__add_version_column_to_restaurants.sql

ALTER TABLE restaurants
ADD COLUMN version BIGINT NOT NULL;