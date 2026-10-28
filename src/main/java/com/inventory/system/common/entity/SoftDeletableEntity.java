package com.inventory.system.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

/**
 * Base class for entities that are never physically removed.
 * Deleting marks the row with {@code deleted = true}; repositories
 * expose {@code ...AndDeletedFalse} finders so deleted rows stay hidden.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class SoftDeletableEntity {

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean deleted = false;
}
