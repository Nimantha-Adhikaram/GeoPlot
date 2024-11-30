package com.example.splashscreen;

public class UTMConverter {

    private static final double k0 = 0.9996;
    private static final double e = 0.00669438;
    private static final double e1sq = e / (1 - e);
    private static final double a = 6378137; // Earth's radius

    public static String[] latLonToUTM(double latitude, double longitude) {
        double zoneNumber = Math.floor((longitude + 180) / 6) + 1;
        double lambda0 = ((zoneNumber - 1) * 6 - 180 + 3) * Math.PI / 180.0;

        double phi = latitude * Math.PI / 180.0;
        double lambda = longitude * Math.PI / 180.0;
        double N = a / Math.sqrt(1 - e * Math.sin(phi) * Math.sin(phi));
        double T = Math.tan(phi) * Math.tan(phi);
        double C = e1sq * Math.cos(phi) * Math.cos(phi);
        double A = Math.cos(phi) * (lambda - lambda0);

        double M = a * ((1 - e / 4 - 3 * e * e / 64 - 5 * e * e * e / 256) * phi
                - (3 * e / 8 + 3 * e * e / 32 + 45 * e * e * e / 1024) * Math.sin(2 * phi)
                + (15 * e * e / 256 + 45 * e * e * e / 1024) * Math.sin(4 * phi)
                - (35 * e * e * e / 3072) * Math.sin(6 * phi));

        double easting = k0 * N * (A + (1 - T + C) * A * A * A / 6
                + (5 - 18 * T + T * T + 72 * C - 58 * e1sq) * A * A * A * A * A / 120) + 500000;

        double northing = k0 * (M + N * Math.tan(phi) * (A * A / 2 + (5 - T + 9 * C + 4 * C * C) * A * A * A * A / 24
                + (61 - 58 * T + T * T + 600 * C - 330 * e1sq) * A * A * A * A * A * A / 720));

        if (latitude < 0) {
            northing += 10000000; // 10000000 meter offset for southern hemisphere
        }

        String[] utmCoordinates = new String[2];
        utmCoordinates[0] = String.format("Easting: %.2f", easting);
        utmCoordinates[1] = String.format("Northing: %.2f", northing);

        return utmCoordinates;
    }
}

