package com.siemens.backend.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RoomType {
    SINGLE(1), DOUBLE(2), SUITE(3), MATRIMONIAL(4);

    private final Integer value;

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
