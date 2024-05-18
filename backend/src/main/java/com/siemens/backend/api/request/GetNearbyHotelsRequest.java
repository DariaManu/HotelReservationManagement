package com.siemens.backend.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Request for retrieving hotels which are in a specific radius relative to the position of the user.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class GetNearbyHotelsRequest implements Serializable {
    private int radius;
    private double userLat;
    private double userLon;
}
