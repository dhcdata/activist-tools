package tools.activist.model;

import java.util.logging.Logger;
import java.util.HashMap;

public class RepresentResponse {
  private static final Logger log = Logger.getLogger(RepresentResponse.class.getName());
  private HashMap debug;
  private HashMap<String, Object> results;

  public RepresentResponse(HashMap<String, Object> results, HashMap debug) {
    this.debug = debug;
    this.results = results;
  }

  public HashMap getDebug() {
    return debug;
  }

  public HashMap<String, Object> getResults() {
    return results;
  }
}
