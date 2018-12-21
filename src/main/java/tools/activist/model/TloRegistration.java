package tools.activist.model;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;

public class TloRegistration {
  private static final Logger log = Logger.getLogger(TloRegistration.class.getName());
  private TloWitness witness;
  private String committee;
  private String committeeCode;
  private Date date;
  private String bill;
  private String action;
  private String position;

  public TloRegistration(TloWitness witness, String committee, String committeeCode, Date date, String bill,
      String action, String position) {
    this.witness = witness;
    this.committee = committee;
    this.committeeCode = committeeCode;
    this.date = date;
    this.bill = bill;
    this.action = action;
    this.position = position;
  }

  public TloWitness getWitness() {
    return witness;
  }

  public String getCommittee() {
    return committee;
  }

  public String getCommitteeCode() {
    return committeeCode;
  }

  public Date getDate() {
    return date;
  }

  public String getBill() {
    return bill;
  }

  public String getAction() {
    return action;
  }

  public String getPosition() {
    return position;
  }

  public String toString() {
    return "TloRegistration [ witness=" + witness + " committee=" + committee + " committeeCode= " + committeeCode
        + " date=" + date + " bill=" + bill + " action = " + action + " position= " + position + " ]";
  }
}
