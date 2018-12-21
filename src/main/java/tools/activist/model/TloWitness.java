package tools.activist.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TloWitness {
  private static final Logger log = Logger.getLogger(TloWitness.class.getName());
  private String fullName;
  private String firstName;
  private String lastName;
  private String location;
  private String title;
  private List<String> organization;
  private String city;
  private String state;
  private String other;

  public TloWitness(String fullName, String firstName, String lastName, String location, List<String> organization,
      String city, String state, String title) {
    this.fullName = fullName;
    this.firstName = firstName;
    this.lastName = lastName;
    this.location = location;
    this.organization = organization;
    this.city = city;
    this.state = state;
    this.title = title;
  }

  public TloWitness(String fullString) {
    // TODO Null Checks
    String finalString = fullString;
    String[] nameBreak = fullString.split("&nbsp;", 2);
    fullName = nameBreak[0];
    lastName = fullName.split(", ")[0].trim();
    firstName = (fullName.split(", ").length > 1) ? fullName.split(", ")[1].trim() : "";

    String locationLine = nameBreak[1].substring(nameBreak[1].lastIndexOf(")") + 3); // String.join(" ", commaBreak);
    city = locationLine.substring(0, locationLine.lastIndexOf(",")).trim();
    state = locationLine.substring(locationLine.lastIndexOf(",") + 2).trim();
    String orgLine = nameBreak[1].substring(0, nameBreak[1].lastIndexOf(")") + 1); // String.join(" ", commaBreak);
    title = orgLine.split("\\(", 2)[0].replace("&nbsp;", "").trim();

    // TODO: Deal with parentheses inside parentheses
    Pattern pattern = Pattern.compile("\\((.*?)\\)", Pattern.DOTALL);
    Matcher matches = pattern.matcher(orgLine);
    organization = new ArrayList<String>();
    while (matches.find()) {
      List<String> orgs = new ArrayList<String>(Arrays.asList(matches.group(1).split(";")));
      if (matches.group(1) != null) {
        for (String o : orgs) {
          if (!o.trim().equals("Self"))
            organization.add(o.trim());
        }
      }

    }
    other = remainingChars(finalString);
  }

  private String remainingChars(String initialString) {
    // Clear
    TreeMap<Integer, ArrayList<String>> f = new TreeMap<Integer, ArrayList<String>>();
    if (f.get(firstName.length()) == null)
      f.put(firstName.length(), new ArrayList<String>());
    f.get(firstName.length()).add(firstName);
    if (f.get(lastName.length()) == null)
      f.put(lastName.length(), new ArrayList<String>());
    f.get(lastName.length()).add(lastName);
    if (f.get(city.length()) == null)
      f.put(city.length(), new ArrayList<String>());
    f.get(city.length()).add(city);
    if (f.get(state.length()) == null)
      f.put(state.length(), new ArrayList<String>());
    f.get(state.length()).add(state);
    if (f.get(title.length()) == null)
      f.put(title.length(), new ArrayList<String>());
    f.get(title.length()).add(title);

    for (String s : organization) {
      if (f.get(s.length()) == null)
        f.put(s.length(), new ArrayList<String>());
      f.get(s.length()).add(s);
    }
    NavigableMap<Integer, ArrayList<String>> nMap = f.descendingMap();
    for (ArrayList<String> entry : nMap.values()) {
      for (String y : entry)
        initialString = initialString.replace(y, "");
    }
    if (initialString.replace("Self", "").replace("&nbsp;", "").replace(",", "").replace("(", "").replace(")", "")
        .replace(" ", "").replace(";", "").length() == 0)
      initialString = null;
    return initialString;

  }
}
