package com.siemens.backend.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetNearbyHotelsRequest implements Serializable {
    private int radius;
    private double userLat;
    private double userLon;
}