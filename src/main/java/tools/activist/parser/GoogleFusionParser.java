package tools.activist.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;

import io.github.cdimascio.dotenv.Dotenv;
import tools.activist.google.FusionTablesUtil;
import tools.activist.google.GoogleBatchClient;
import tools.activist.model.FusionData;
import tools.activist.model.FusionResponse;
import tools.activist.model.RepresentResponse;

// TODO: This is not a parser - it's a worker
public class GoogleFusionParser {
  private static final Logger log = Logger.getLogger(GoogleFusionParser.class.getName());
  private static String address;

  public static RepresentResponse getStuff(String addr) {
    final long startTime = System.nanoTime();
    RepresentResponse out = null;
    HashMap<String, FusionResponse> hm = new HashMap<String, FusionResponse>();
    address = addr;

    GeocodingResult[] geocode = getGeocode(address);
    if (geocode != null) {
      getFusionData().forEach(l -> FusionTablesUtil
          .fusionQueue(query(l, geocode[0].geometry.location, distance(geocode[0].geometry)), hm, l.getName()));
      GoogleBatchClient.execute(); // TODO, might not want a single instane - race condition between
                                   // threads?
      out = new RepresentResponse(getReturnedResults(hm), getDebug(geocode, address, startTime));
    }
    return out == null ? null : out;
  }

  public static HashMap<String, Object> getDebug(GeocodingResult[] geocode, String address, long startTime) {
    HashMap<String, Object> debug = new HashMap<String, Object>();
    debug.put("location", geocode);
    debug.put("submittedAddress", address);
    debug.put("performance", ((double) (System.nanoTime() - startTime) / 1000000.0) + " ms");
    return debug;
  }

  private static String query(FusionData data, LatLng latlng, double distance) {
    return "SELECT " + data.getFields() + " FROM " + data.getTable() + " WHERE ST_INTERSECTS(geometry, circle(LATLNG("
        + latlng.lat + "," + latlng.lng + "), " + String.valueOf(distance) + " ))";
  }

  public static double distance(Geometry geometry) {
    return distance(geometry.location.lat, geometry.bounds.southwest.lat, geometry.location.lng,
        geometry.bounds.southwest.lng, 0.0, 0.0);
  }

  public static double distance(double lat1, double lat2, double lon1, double lon2, double el1, double el2) {
    final int R = 6371; // Radius of the earth

    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);
    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1))
        * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double distance = R * c * 1000; // convert to meters

    double height = el1 - el2;
    distance = Math.pow(distance, 2) + Math.pow(height, 2);
    return Math.sqrt(distance);
  }

  public static List<FusionData> getFusionData() {
    return Arrays.asList(new FusionData("zip", "10caU7-OmW1EgI9TVi6yUZQGur44yquptomiOpE8t", "ZCTA5CE10"),
        new FusionData("congress", "16KsL-fyKwrLv26GLL-48fXaF6ez5PLseYBJjRq8O", "District"),
        new FusionData("house", "1nTNAy2eEtYZnXnszDlzKhIn4-7CRULoZ6jQLs8pl", "District"),
        new FusionData("senate", "1-sB7hkJ_Q5_dZ4SAvh1PQTROAuBf5gOpnlACtw8M", "District"));
  }

  private static HashMap<String, Object> getReturnedResults(HashMap<String, FusionResponse> m) {
    log.info(m.toString());
    HashMap<String, Object> map = new HashMap<String, Object>();
    m.keySet().forEach(s -> map.put(s, m.get(s).getEntityResponse()));
    return map;
  }

  public static GeocodingResult[] getGeocode(String address) {
    Dotenv dotenv = Dotenv.load();
    String GEOCODE_API_KEY = dotenv.get("GEOCODE_API_KEY");
    try {
      return GeocodingApi.geocode(new GeoApiContext.Builder().apiKey(GEOCODE_API_KEY).build(), address).await();
    } catch (Exception e) {
      log.info("Error getting Address Geocode JSON: " + e.getMessage());
      return null;
    }
  }
}
