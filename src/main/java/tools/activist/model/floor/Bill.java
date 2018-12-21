package tools.activist.model.floor;

class Bill extends MainMotion {
  public String name;
  public String readingNumber;

  public Bill(String name, String readingNumber) {
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
