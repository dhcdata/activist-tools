package tools.activist.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.text.ParseException;
import tools.activist.model.TloWitness;
import tools.activist.model.TloRegistration;
import com.google.gson.GsonBuilder;

public class TloWitnessParser {
  private static final Logger log = Logger.getLogger(TloWitnessParser.class.getName());
  static final String DOMAIN = "www.capitol.state.tx.us";
  static final String PATH = "/tlodocs/";
  static final String DIR = "/witlistmtg/html/";
  private static String currentCommittee;
  private static String currentBill;
  private static String currentAction;
  private static String currentPosition;
  private static String currentWitness;
  private static String currentCommitteeCode;
  private static Date meetingDate;
  private static List<TloRegistration> witnessList = new ArrayList<TloRegistration>();
  private static DateFormat df = new SimpleDateFormat("MMMMM d, yyyy hh:mm aaa");

  public static String getMeetingURL(String meeting, String session) {
    return "http://" + DOMAIN + PATH + session + DIR + meeting + ".HTM";
  }

  public static List<TloRegistration> getStuff(String meeting, String session) {
    currentCommitteeCode = meeting.substring(0, 4);
    meetingDate = null;
    Document doc = null;
    try {
      doc = Jsoup.connect(getMeetingURL(meeting, session)).get();
    } catch (Exception e) {
      log.info(e.getMessage());
    }
    if (doc != null)
      return processTloWitness(doc);
    else
      return null;
  }

  private static boolean hasState(String line) {
    if (line.length() == 2)
      return true;
    else if (line.length() > 4)
      return line.substring(line.length() - 4, line.length() - 2).equals(", ");
    else
      return false;
  }

  private static List<TloRegistration> processTloWitness(Document doc) {
    currentAction = "testify";
    List<String> witnessLine = new ArrayList<String>();
    for (Element line : doc.select("p>span")) {
      String[] a = line.html().split(" ", 2);
      Elements children = line.children();
      int begSpace = a[0].split("&nbsp").length;
      int lineSpace = 0;
      boolean begin = true;
      String lineText = a[1];

      if (begSpace == 12) {
        if (children.first() != null && children.first().tagName() == "u") {
          currentBill = line.children().first().text();
        } else if (lineText.contains(":")) {
          try {
            meetingDate = df.parse(lineText);
          } catch (ParseException e) {
          }
        } else {
          currentCommittee = lineText;
        }
      } else if (begSpace == 33) {
        witnessLine.add(lineText);
        if (hasState(lineText)) {
          witnessList.add(new TloRegistration(new TloWitness(String.join(" ", witnessLine)), currentCommittee,
              currentCommitteeCode, meetingDate, currentBill, currentAction, currentPosition));
          witnessLine.clear();
        }
        // TODO witness
        // log.info("" + begSpace + lineText);
      } else if (begSpace == 18) {
        if (line.text().contains("ON:"))
          currentPosition = "on";
        else if (line.text().contains("FOR:"))
          currentPosition = "for";
        else if (line.text().contains("AGAINST:"))
          currentPosition = "against";
        else if (line.children().first().tagName() == "u") {
          currentAction = line.children().first().text();
        }
      } else if (begSpace == 78) {
        // TODO Witness list title
      } else {
        log.info("New Space: " + begSpace);
      }
    }
    // log.info(witnessList.get(0).toString());
    return witnessList;
  }
}
