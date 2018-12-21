package tools.activist.model.floor;

import java.util.List;

import org.jsoup.nodes.Element;

class HouseCalendar {
  public String calendarName;
  public String chamber; // TODO: bad name, this is either HB or SB, etc
  public String readingNumber;
  public List<Bill> bills;

  public HouseCalendar(String calendarName, String chamber, String readingNumber) {
    this.calendarName = calendarName;
    this.chamber = chamber;
    this.readingNumber = readingNumber;
  }

  public HouseCalendar(Element line) {
    calendarName = line.children().get(0).textNodes().get(0).text();
    chamber = line.children().get(0).textNodes().get(1).text();
    readingNumber = line.children().get(0).textNodes().get(2).text();
  }

  public Bill addBill(String name, String readingNumber) {
    Bill bill = new Bill(name, readingNumber);
    bills.add(bill);
    return bill;
  }

  public List<Bill> getBills() {
    return bills;
  }

  public Bill getBill(String name, String readingNumber) {
    for (Bill bill : bills) {
      if (bill.getName().equals(name) && bill.getReadingNumber().equals(readingNumber))
        return bill;
    }
    return null;
  }

  public RecordVote getRecordVote(String recordNumber) {
    // TODO
    return null;
  }
}
