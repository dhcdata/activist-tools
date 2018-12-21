package tools.activist.model.floor;

class Amendment extends MainMotion {
  public String name;
  public String readingNumber;
  // TODO: maybe include bill reference

  public Amendment(String name, String readingNumber) {
    this.name = name;
    this.readingNumber = readingNumber;
  }

  public String getName() {
    return name;
  }

  public String getReadingNumber() {
    return readingNumber;
  }
}
