package tools.activist.model;

import java.util.List;
import java.util.stream.IntStream;
import java.util.HashMap;
import java.util.ArrayList;
import com.google.api.services.fusiontables.model.Sqlresponse;
import com.google.api.client.googleapis.json.GoogleJsonErrorContainer;
import java.util.logging.Logger;

public class FusionResponse {
  private static final Logger log = Logger.getLogger(FusionResponse.class.getName());
  private List<String> columns;
  private String kind;
  private List<List<Object>> rows;
  private String error;

  public FusionResponse() {
    this.columns = new ArrayList<String>();
    this.kind = "";
    this.rows = new ArrayList<List<Object>>();
  }

  public FusionResponse(Sqlresponse s) {
    this.columns = s.getColumns();
    this.kind = s.getKind();
    this.rows = s.getRows();
  }

  public FusionResponse(List columns, String kind, List<List<Object>> rows) {
    this.columns = columns;
    this.kind = kind;
    this.rows = rows;
  }

  public FusionResponse(GoogleJsonErrorContainer e) {
    this.error = e.getError().getMessage();
    this.columns = new ArrayList<>();
    this.kind = "";
    this.rows = new ArrayList<>();
  }

  public HashMap<String, Object> getEntityResponse() {
    List<HashMap<String, Object>> out = new ArrayList<HashMap<String, Object>>();
    for (List<Object> row : rows) {
      HashMap<String, Object> r = new HashMap<String, Object>();
      IntStream.range(0, columns.size()).forEach(i -> r.put(columns.get(i), row.get(i)));
      out.add(r);
    }
    HashMap<String, Object> o = new HashMap<String, Object>();
    o.put("result", out);
    if (error != null)
      o.put("error", error);
    return o;
  }

  @Override
  public String toString() {
    return this.getEntityResponse().toString();
  }
}
