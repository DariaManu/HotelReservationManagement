package com.siemens.backend.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;

/**
 * Class representing all possible types of rooms.
 */
@AllArgsConstructor
public enum RoomType {
    SINGLE(1), DOUBLE(2), SUITE(3), MATRIMONIAL(4);

    private final Integer value;

    /**
     * Return the correct enum type corresponding to the given enum value.
     * @param value - value of the enum
     * @return - corresponding enum type
     */
    @JsonCreator
    public static RoomType fromValue(Integer value) {
        for (RoomType type: RoomType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException();
    }
}
