package tools.activist.model;

public class FusionData {
  private String table;
  private String fields;
  private String name;

  public FusionData(String name, String table, String fields) {
    this.name = name;
    this.table = table;
    this.fields = fields;
  }

  public String getTable() {
    return this.table;
  }

  public String getFields() {
    return this.fields;
  }

  public String getName() {
    return this.name;
  }

  @Override
  public String toString() {
    return "[table: " + this.table + ", fields: " + this.fields + "]";
  }
}
