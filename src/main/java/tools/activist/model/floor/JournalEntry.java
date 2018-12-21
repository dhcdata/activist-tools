package tools.activist.model.floor;

import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import tools.activist.parser.TloJournalParser;
import tools.activist.handler.JsonHandler;
import java.util.List;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import tools.activist.enums.Heading;

public class JournalEntry {
  private static final Logger log = Logger.getLogger(JournalEntry.class.getName());

  private String type;
  private List<String> data;
  private String portion;
  private Integer journalEntryNo;
  private Integer paragraphs;
  private Integer fParagraphs;
  private boolean print;
  private RecordVote recordVote;

  public JournalEntry(String type, String data, String portion, Integer journalEntryNo, Integer paragraphs,
      boolean print) {
    this.type = type;
    // this.data = data;
    this.portion = portion;
    this.journalEntryNo = journalEntryNo;
    this.paragraphs = paragraphs;
    this.print = print;
  }

  public JournalEntry(String type, Elements blocks, String portion, Integer journalEntryNo, boolean print) {
    this.data = new ArrayList<String>();
    this.type = type;
    this.paragraphs = blocks.size();
    recordVote(blocks);
    for (Element block : blocks) {
      this.data.add(block.text());
    }
    // this.data = blocks.text();
    this.portion = portion;
    this.journalEntryNo = journalEntryNo;
    this.print = print;
    processType();
    this.fParagraphs = blocks.size();
  }

  public String getType() {
    return type;
  }

  public List<String> getData() {
    return data;
  }

  public boolean canPrint() {
    return print;
    // return true;
  }

  public String getPortion() {
    return portion;
  }

  private void recordVote(Elements blocks) {

    Pattern votePattern = Pattern
        .compile("(^.*)\\(Record (\\d*)\\): (\\d*) Yeas, (\\d*) Nays, (\\d*) Present, not voting", Pattern.DOTALL);
    Pattern rollPattern = Pattern.compile("(^.*)\\(Record (\\d*)\\).", Pattern.DOTALL);
    Iterator<Element> blockItr = blocks.iterator();
    while (blockItr.hasNext()) {
      Element block = blockItr.next();
      Boolean removeItr = false;
      Matcher matches = votePattern.matcher(block.text().trim().replace("\u00a0", " ").replace(" +", " "));
      while (matches.find()) {
        if (recordVote == null)
          recordVote = new RecordVote(matches.group(2), matches.group(1));
        recordVote.setCount("yeas", Integer.parseInt(matches.group(3)));
        recordVote.setCount("nays", Integer.parseInt(matches.group(4)));
        recordVote.setCount("pnvs", Integer.parseInt(matches.group(5)));
        removeItr = true;
      }
      Matcher rollMatches = rollPattern.matcher(block.text().trim().replace("\u00a0", " ").replace(" +", " "));
      while (rollMatches.find()) {
        if (recordVote == null)
          recordVote = new RecordVote(rollMatches.group(2), rollMatches.group(1));
        removeItr = true;
      }

      String position = null;
      if (block.text().startsWith("Yeas — ")) {
        addVote(block, "yeas");
        removeItr = true;
      }
      if (block.text().startsWith("Nays — ")) {
        addVote(block, "nays");
        removeItr = true;
      }
      if (block.text().startsWith("Present, not voting — ")) {
        addVote(block, "pnv");
        removeItr = true;
      }
      if (block.text().startsWith("Absent, Excused")) {
        addVote(block, "absEx");
        removeItr = true;
      }
      if (block.text().startsWith("Absent — ")) {
        addVote(block, "abs");
        removeItr = true;
      }
      if (block.text().startsWith("Present — ")) {
        addVote(block, "present");
        removeItr = true;
      }
      if (removeItr == true)
        blockItr.remove();
    }
  }

  private void addVote(Element line, String position) {
    for (String x : line.text().substring(0, line.text().length() - 1).split("—")[1].split(";")) {
      if (x.contains("(C)"))
        recordVote.addChair(x.trim().replace("(C)", ""));

      if (position.equals("yeas"))
        recordVote.addYea(x.trim().replace("(C)", ""));
      else if (position.equals("nays"))
        recordVote.addNay(x.trim().replace("(C)", ""));
      else if (position.equals("pnv"))
        recordVote.addPnv(x.trim().replace("(C)", ""));
      else if (position.equals("present"))
        recordVote.addPresent(x.trim().replace("(C)", ""));
      else if (position.equals("abs"))
        recordVote.addAbsent(x.trim().replace("(C)", ""));
      else if (position.equals("absEx"))
        recordVote.addAbsentExcused(x.trim().replace("(C)", ""));
    }
  }

  private void processType() {
    if (Heading.Announcement.contains(type)) {
      log.info("ANNOUNCE: " + type);
      print = false;
    } else if (Heading.Motion.contains(type)) {
      log.info("MOTION: " + type);
      print = false;
    } else if (Heading.Calendar.contains(type)) {
      log.info("Calendar: " + type);
      print = false;
    } else {

    }
    switch (type) {
    // Regular Business
    case "RESOLUTIONS ADOPTED":
      // case "REASON FOR VOTE":
      // case "REASONS FOR VOTE":
      // case "STATEMENT OF VOTE":
      // case "STATEMENTS OF VOTE":
      // case "POSTPONED BUSINESS":
      // case "REGULAR ORDER OF BUSINESS SUSPENDED":
      // case "LOCAL, CONSENT, AND RESOLUTIONS CALENDAR RULES SUSPENDEDE":
      // case "CONSTITUTIONAL RULE SUSPENDED":
      // case "FIVE-DAY POSTING RULE SUSPENDED":
      // case "RULES SUSPENDED":
      // case "REMARKS ORDERED PRINTED":
      // case "PARLIAMENTARY INQUIRY":
      // case "HOUSE AT EASE":
      // case "AFTERNOON SESSION":
      print = false;
      break;
    // Motions
    case "MOTION FOR ONE RECORD VOTE":
    case "BILLS RECOMMITTED":
    case "PROVIDING FOR ADJOURNMENT SINE DIE":
    case "MOTION TO SUSPEND FIVE-DAY POSTING RULE":
    case "PROVIDING FOR A LOCAL, CONSENT, AND RESOLUTIONS RULES SUSPENDED":
    case "PROVIDING FOR A LOCAL, CONSENT, AND RESOLUTIONS CALENDAR RULES SUSPENDED":
    case "PROVIDING FOR A LOCAL, CONSENT, AND RESOLUTIONS CALENDAR":
    case "PROVIDING FOR AN ADDENDUM TO LOCAL, CONSENT, AND RESOLUTIONS CALENDAR":
    case "PROVIDING FOR A CONGRATULATORY AND MEMORIAL CALENDAR":
    case "LOCAL, CONSENT, AND RESOLUTIONS CALENDAR MOTION TO SUSPEND RULES":
    case "PROVIDING FOR RECESS":
    case "PROVIDING FOR ADJOURNMENT":
    case "COMMITTEE GRANTED PERMISSION TO MEET":
    case "COMMITTEES GRANTED PERMISSION TO MEET":
      print = false;
      break;
    // Administrative
    case "NOTICE GIVEN":
    case "SENT TO THE SECRETARY OF THE STATE":
    case "ROLL OF HOUSE":
    case "STANDING COMMITTEE REPORTS":
    case "RECESS":
    case "ADJOURNMENT SINE DIE":
    case "ADJOURNMENT":
    case "CAPITOL PHYSICIAN":
    case "INTERPRETER FOR THE DEAF":
    case "INTRODUCTION OF GUESTS":
    case "LEAVE OF ABSENCE GRANTED":
    case "LEAVES OF ABSENCE GRANTED":
    case "COMMITTEE MEETING ANNOUNCEMENT":
    case "COMMITTEE MEETING ANNOUNCEMENTS":
    case "BILLS AND RESOLUTIONS SIGNED BY THE SPEAKER":
      print = false;
      break;
    // Addendum Items
    case "MESSAGE FROM THE SENATE":
    case "MESSAGES FROM THE SENATE":
    case "SIGNED BY THE SPEAKER":
    case "RECOMMENDATIONS FILED WITH THE SPEAKER":
    case "REFERRED TO COMMITTEES":
    case "RESOLUTIONS REFERRED TO COMMITTEES":
    case "CORRECTIONS IN REFERRAL":
      print = false;
      break;
    // Appendix Items
    case "SIGNED BY THE GOVERNOR":
    case "VETOED BY THE GOVERNOR":
    case "MESSAGE FROM THE GOVERNOR OF THE STATE OF TEXAS":
    case "FILED WITHOUT THE GOVERNOR\'S SIGNATURE":
    case "SENT TO THE GOVERNOR":
    case "ENROLLED":
    case "ENGROSSED":
      print = false;
      break;
    // Calendar
    case "MAJOR STATE CALENDAR HOUSE BILLS SECOND READING":
    case "MAJOR STATE CALENDAR HOUSE BILLS THIRD READING":
    case "MAJOR STATE CALENDAR SENATE BILLS SECOND READING":
    case "MAJOR STATE CALENDAR SENATE BILLS THIRD READING":
    case "HOUSE BILLS THIRD READING":
    case "RESOLUTIONS CALENDAR":
    case "GENERAL STATE CALENDAR SENATE BILLS SECOND READING":
    case "GENERAL STATE CALENDAR SENATE BILLS THIRD READING":
    case "GENERAL STATE CALENDAR HOUSE BILLS SECOND READING":
    case "GENERAL STATE CALENDAR HOUSE BILLS THIRD READING":
    case "LOCAL, CONSENT, AND RESOLUTIONS CALENDAR SENATE BILLS SECOND READING":
    case "LOCAL, CONSENT, AND RESOLUTIONS CALENDAR SENATE BILLS THIRD READING":
    case "LOCAL, CONSENT, AND RESOLUTIONS CALENDAR THIRD READING":
    case "EMERGENCY CALENDAR HOUSE BILLS SECOND READING":
    case "EMERGENCY CALENDAR HOUSE BILLS THIRD READING":
    case "EMERGENCY CALENDAR SENATE BILLS SECOND READING":
    case "EMERGENCY CALENDAR SENATE BILLS THIRD READING":
    case "CONSTITUTIONAL AMENDMENTS CALENDAR HOUSE JOINT RESOLUTIONS SECOND READING":
    case "CONSTITUTIONAL AMENDMENTS CALENDAR HOUSE JOINT RESOLUTIONS THIRD READING":
    case "CONSTITUTIONAL AMENDMENTS CALENDAR SENATE JOINT RESOLUTIONS SECOND READING":
    case "CONSTITUTIONAL AMENDMENTS CALENDAR SENATE JOINT RESOLUTIONS THIRD READING":
    case "CONGRATULATORY AND MEMORIAL CALENDAR":
      print = false;
      break;
    default:
      if (type.contains("ON FIRST READING"))
        print = false;
      else if (type.contains("ON SECOND READING"))
        print = false;
      else if (type.contains("ON THIRD READING"))
        print = false;
      else if (type.contains("PREVIOUSLY ADOPTED"))
        print = false;
      // else if (type.contains("Amendment No.")) print = false; // TODO: Check this
      else if (type.contains("POINT OF ORDER"))
        print = false;
      else if (type.contains("POINTS OF ORDER"))
        print = false;
      else if (type.contains("(consideration continued)"))
        print = false;
      else if (type.contains("ADDRESS BY"))
        print = false;
      else if (type.contains("VOTE RECONSIDERED"))
        print = false;
      else if (type.contains("COMMITTEE ON CALENDARS RULE ADOPTED"))
        print = false;
      else if (type.contains("STATEMENT OF LEGISLATIVE INTENT"))
        print = false;
      else if (type.contains("HOUSE SPONSORS AUTHORIZED"))
        print = false;
      else if (type.contains("HOUSE SPONSOR AUTHORIZED"))
        print = false;
      else if (type.contains("LAID ON THE TABLE SUBJECT TO CALL"))
        print = false;
      else if (type.contains("- REMARKS"))
        print = false;
      else if (type.contains("- ADOPTED"))
        print = false;
      else if (type.contains("- NAMES ADDED"))
        print = false;
      else if (type.contains("MOTION FOR PREVIOUS QUESTION"))
        print = false;
      else if (type.contains("MOTION TO SUSPEND RULES"))
        print = false;
      else if (type.contains("PERMISSION TO INTRODUCE"))
        print = false;
      else if (type.contains("CONFERENCE COMMITTEE INSTRUCTED"))
        print = false;
      else if (type.contains("MOTION TO INSTRUCT CONFEREES"))
        print = false;
      else if (type.contains("MOTION TO RECONSIDER SPREAD ON JOURNAL"))
        print = false;
      else if (type.contains("MOTION TO ADOPT COMMITTEE ON CALENDARS RULE"))
        print = false;
      else if (type.contains("- RECOMMITTED"))
        print = false;
      else if (type.contains("- NOTICE GIVEN"))
        print = false;
      else if (type.contains("HCR"))
        print = false;
      else if (type.contains("SCR"))
        print = false;
      else if (type.contains("MOTION TO SUSPEND CONSTITUTIONAL RULE"))
        print = false;
      else if (type.contains("HOUSE CONCURS IN SENATE AMENDMENTS TEXT OF SENATE AMENDMENTS"))
        print = false;
      else if (type.contains("MOTION TO REFUSE TO CONCUR IN SENATE AMENDMENTS"))
        print = false;
      else if (type.contains("MOTION TO CONCUR IN SENATE AMENDMENTS"))
        print = false;
      else if (type.contains("NOTICE OF INTRODUCTION"))
        print = false;
      else if (type.contains("STATEMENT FOR INCLUSION IN THE JOURNAL"))
        print = false;
      else if (type.contains("RETURNED TO SENATE BY THE SPEAKER"))
        print = false;
      else if (type.contains("WITH SENATE AMENDMENTS"))
        print = false;
      else if (type.contains("HOUSE REFUSES TO CONCUR IN SENATE AMENDMENTS"))
        print = false;
      else if (type.contains("CONFERENCE COMMITTEE REPORT ADOPTED"))
        print = false;
      else if (type.contains("RULES SUSPENDED"))
        print = false;
      else if (type.contains("CONFERENCE COMMITTEE APPOINTED"))
        print = false;
      else if (type.contains("INTRODUCTION OF GUEST"))
        print = false;
      else if (recordVote != null && recordVote.getPresent() > 0)
        print = false;

    }
  }
}
