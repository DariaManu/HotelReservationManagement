package com.siemens.backend;


public class TestRun {
    public static void main(String[] args){
        double lat1 = 46.764654252624204;
        double lon1 = 23.598674125224626;

        double lat2 = 46.75151704379424;
        double lon2 = 23.547639421949896;

        double latMid = (lat1 + lat2) / 2.0;
        double metersPerDegreeOfLatitude = 111132.954 - 559.822 * Math.cos( 2.0 * latMid ) + 1.175 * Math.cos( 4.0 * latMid);
        double metersPerDegreeOfLongitude = 111412.84 * Math.cos(latMid) - 93.5 * Math.cos(3.0 * latMid) + 0.118 * Math.cos(5 * latMid);

        double lat1Meters = lat1 * metersPerDegreeOfLatitude;
        double lon1Meters = lon1 * metersPerDegreeOfLongitude;
        double lat2Meters = lat2 * metersPerDegreeOfLatitude;
        double lon2meters = lon2 * metersPerDegreeOfLongitude;

        double distanceInMeters = Math.sqrt(Math.pow(lat1Meters - lat2Meters, 2) + Math.pow(lon1Meters - lon2meters, 2));
        double distanceInKilometers = distanceInMeters / 1000.0;
        System.out.println(distanceInKilometers);
    }
}
