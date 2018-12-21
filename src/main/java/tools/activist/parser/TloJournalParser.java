package tools.activist.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;
import tools.activist.model.floor.JournalEntry;

public class TloJournalParser {
  private static final Logger log = Logger.getLogger(TloWitnessParser.class.getName());
  static String URL;

  public static List<JournalEntry> getStuff(String chamber, String journalId, String session) {
    URL = "http://" + "www.journals." + chamber + ".state.tx.us" + "/" + chamber.substring(0, 1).toUpperCase() + "JRNL/"
        + session + "/HTML/" + journalId + ".HTM";
    Document doc = null;
    try {
      doc = Jsoup.connect(URL).get();
    } catch (Exception e) {
      log.info(e.getMessage());
    }
    if (doc != null)
      return parseTloJournal(doc);
    else
      return null;
  }

  private static List<JournalEntry> parseTloJournal(Document doc) {
    List<JournalEntry> out = new ArrayList<JournalEntry>();
    List<Element> thisBlock = new ArrayList<Element>();
    List<List<Element>> blocks = new ArrayList<List<Element>>();
    String currentHeaderText = "";
    String currentPortion = "proceedings";
    for (Element line : doc.select(".container>.textpara,.container>.Appendix")) {
      isCalendarAnnouncement(line);
      if (isHeader(line)) {
        if (thisBlock.size() > 0) {
          JournalEntry je = new JournalEntry(currentHeaderText, new Elements(thisBlock), currentPortion,
              blocks.size() + 1, true);
          if (je.canPrint()) {
            out.add(je);
          }
        }
        if (thisBlock.size() > 0)
          blocks.add(thisBlock);
        thisBlock.clear();
        currentHeaderText = line.text();
        // TODO: check multi para
      } else if (line.hasClass("Appendix")) {
        currentPortion = line.text().toLowerCase();
        if (thisBlock.size() > 0)
          blocks.add(thisBlock);
        thisBlock.clear();
      } else {
        thisBlock.add(line);
      }
    }
    if (thisBlock.size() > 0)
      blocks.add(thisBlock);
    log.info("blocks: " + blocks.size());
    return out;
  }

  private static boolean isHeader(Element line) {
    boolean isH = false;
    for (Element c : line.children()) {
      // log.info("isH: " + c.ownText());
      if (c.tagName() == "b" && c.ownText().startsWith("Amendment No."))
        isH = true;
      if (c.tagName() == "u" || (c.ownText().length() > 0 && c.tagName() != "b"))
        return false;
    }
    if (line.attr("style").contains("text-align:center"))
      isH = true;
    return isH;
  }

  private static boolean isCalendarAnnouncement(Element line) {
    boolean isCal = false;
    if (line.children().size() == 1 && line.children().get(0).tagName() == "b"
        && (line.children().get(0).textNodes().get(0).text().endsWith("CALENDAR")
            || line.children().get(0).textNodes().get(0).text().equals("POSTPONED BUSINESS"))) {
      // log.info(line.text());
      isCal = true;
    }
    return isCal;
  }
}
