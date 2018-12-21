package tools.activist.model.floor;

import java.util.ArrayList;
import java.util.List;

class MainMotion {
  public List<String> data = new ArrayList<String>();
  public List<Amendment> amendments;
  // public List<Action> otherActions;
  public RecordVote recordVote;
  public String outcome;

  public MainMotion() {

  }

  public void addData(String data) {
    this.data.add(data);
  }

  public Amendment addAmendment(String name, String readingNumber) {
    if (amendments == null) amendments = new ArrayList<Amendment>();
    Amendment amendment = new Amendment(name, readingNumber);
    amendments.add(amendment);
    return amendment;
  }

  public void setRecordVote(RecordVote recordVote) {
    this.recordVote = recordVote;
  }

  public void setOutcome(String outcome){
    this.outcome = outcome;
  }

  public RecordVote getRecordVote() {
    return recordVote;
  }

}
