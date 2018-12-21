package tools.activist.model;

import java.util.HashMap;

public class RepresentResponse {
  private HashMap<String, Object> debug;
  private HashMap<String, Object> results;

  public RepresentResponse(HashMap<String, Object> results, HashMap<String, Object> debug) {
    this.debug = debug;
    this.results = results;
  }

  public HashMap<String, Object> getDebug() {
    return debug;
  }

  public HashMap<String, Object> getResults() {
    return results;
  }
}
