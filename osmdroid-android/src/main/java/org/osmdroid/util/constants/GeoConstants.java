// Created by plusminus on 17:41:55 - 16.10.2008
package org.osmdroid.util.constants;

public interface GeoConstants {
	// ===========================================================
	// Final Fields
	// ===========================================================

	int RADIUS_EARTH_METERS = 6378137; // http://en.wikipedia.org/wiki/Earth_radius#Equatorial_radius
	double METERS_PER_STATUTE_MILE = 1609.344; // http://en.wikipedia.org/wiki/Mile
	double METERS_PER_NAUTICAL_MILE = 1852; // http://en.wikipedia.org/wiki/Nautical_mile
	double FEET_PER_METER = 3.2808399; // http://en.wikipedia.org/wiki/Feet_%28unit_of_length%29
	int EQUATORCIRCUMFENCE = (int) (2 * Math.PI * RADIUS_EARTH_METERS);

	// ===========================================================
	// Methods
	// ===========================================================
}
