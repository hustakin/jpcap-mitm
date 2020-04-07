
+function () {
    'use strict'

    var Utils = {
        domBoundContains: function (outerPointTopLeft, outerPointBottomRight, innerPointTopLeft, innerPointBottomRight, redundant) {
            if(!redundant)
                redundant = 0;
            return  outerPointTopLeft.x + redundant <= innerPointTopLeft.x && outerPointTopLeft.y + redundant <= innerPointTopLeft.y
                    && outerPointBottomRight.x - redundant >= innerPointBottomRight.x && outerPointBottomRight.y - redundant >= innerPointBottomRight.y;
        },
        // ray-casting algorithm based on
        // http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
        pointInPolygon: function (point, vs) {
            var x = point[0], y = point[1];
            var inside = false;
            for (var i = 0, j = vs.length - 1; i < vs.length; j = i++) {
                var xi = vs[i][0], yi = vs[i][1];
                var xj = vs[j][0], yj = vs[j][1];

                var intersect = ((yi > y) != (yj > y))
                    && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
                if (intersect) inside = !inside;
            }
            return inside;
        },
        // Distance in meters
        // Always positive regardless of direction
        // Calculation based on Haversine Formula http://en.wikipedia.org/wiki/Haversine_formula
        // Another method is @ http://www.movable-type.co.uk/scripts/latlong-vincenty.html but seems way overcomplicated
        calcDistance:  function (coord1, coord2) {
            var deltaLng = this.units.degrees.toRadians(coord1[0] - coord2[0]),
            deltaLat = this.units.degrees.toRadians(coord1[1] - coord2[1]),
            lat1 = this.units.degrees.toRadians(coord1[1]),
            lat2 = this.units.degrees.toRadians(coord2[1]),
            hvsLng = Math.sin(deltaLng / 2),
            hvsLat = Math.sin(deltaLat / 2);

            var a = hvsLat * hvsLat + hvsLng * hvsLng * Math.cos(lat1) * Math.cos(lat2);
            return this.R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        },
        // Distance in meters
        // Always positive regardless of direction
        // Calculation based on Haversine Formula http://en.wikipedia.org/wiki/Haversine_formula
        // Another method is @ http://www.movable-type.co.uk/scripts/latlong-vincenty.html but seems way overcomplicated
        calcPathsDistance:  function (coordArray) {
            var distance = 0, c1, c2;
            for (var i = 0, l = coordArray.length; i < l - 1; i += 1) {
                c1 = coordArray[i];
                c2 = coordArray[i + 1];
                distance = distance + this.calcDistance(c1, c2);
            }
            return distance;
        },
        // Calculates area in square meters
        // Method taken from OpenLayers API, https://github.com/openlayers/openlayers/blob/master/lib/OpenLayers/Geometry/LinearRing.js#L270
        calcArea: function (coordArray) {
            var area = 0, i, l, c1, c2;
            for (i = 0, l = coordArray.length; i < l; i += 1) {
                c1 = coordArray[i];
                c2 = coordArray[(i + 1) % coordArray.length]; // Access next item in array until last item is i, then accesses first (0)
                area = area + this.units.degrees.toRadians(c2[0] - c1[0]) * (2 + Math.sin(this.units.degrees.toRadians(c1[1])) + Math.sin(this.units.degrees.toRadians(c2[1])));
            }
            return Math.abs(area * this.R * this.R / 2);
        },
        calcCenter: function (coordArray) {
            var offset = coordArray[0], twiceArea = 0, x = 0, y = 0, i, l, c1, c2, f;
            if (coordArray.length === 1) {
                return coordArray[0];
            }
            for (i = 0, l = coordArray.length; i < l; i += 1) {
                c1 = coordArray[i];
                c2 = coordArray[(i + 1) % coordArray.length]; // Access next item in array until last item is i, then accesses first (0)
                f = (c1[1] - offset[1]) * (c2[0] - offset[0]) - (c2[1] - offset[1]) * (c1[0] - offset[0]);
                twiceArea = twiceArea + f;
                x = x + ((c1[0] + c2[0] - 2 * offset[0]) * f);
                y = y + ((c1[1] + c2[1] - 2 * offset[1]) * f);
            }
            f = twiceArea * 3;
            return [x / f + offset[0], y / f + offset[1]];
        },
        units: {
            acres: {
                factor: 0.00024711,
                display: 'acres',
                decimals: 2
            },
            feet: {
                factor: 3.2808,
                display: 'feet',
                decimals: 0
            },
            kilometers: {
                factor: 0.001,
                display: 'kilometers',
                decimals: 2
            },
            hectares: {
                factor: 0.0001,
                display: 'hectares',
                decimals: 2
            },
            meters: {
                factor: 1,
                display: 'meters',
                decimals: 0
            },
            miles: {
                factor: 3.2808 / 5280,
                display: 'miles',
                decimals: 2,
                toFeet: function (m) {
                    return m * 3.28084;
                },
                toKilometers: function (m) {
                    return m * 0.001;
                },
                toMiles: function (m) {
                    return m * 0.000621371;
                }
            },
            sqfeet: {
                factor: 10.7639,
                display: 'sqfeet',
                decimals: 0
            },
            sqmeters: {
                factor: 1,
                display: 'sqmeters',
                decimals: 0,
                toSqMiles: function (m) {
                    return m * 0.000000386102;
                },
                toAcres: function (m) {
                    return m * 0.000247105;
                }
            },
            sqmiles: {
                factor: 0.000000386102,
                display: 'sqmiles',
                decimals: 2
            },
            degrees: {
                toRadians: function (d) {
                    return d * Math.PI / 180;
                }
            }
        },
        R: 6371000 // Earth radius in meters
    }

    window.Utils = Utils;
}()