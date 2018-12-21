package tools.activist.model.floor;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

class RecordVote {
  private RollCall rollCall;
  private Motion motion;
  // TODO: Fix Chair assignment
  private String chair;
  private HashMap<String, Integer> count = new HashMap<String, Integer>();

  public class RollCall {
    private List<String> absEx = new ArrayList<String>();
    private List<String> abs = new ArrayList<String>();
    private List<String> yeas = new ArrayList<String>();
    private List<String> nays = new ArrayList<String>();
    private List<String> present = new ArrayList<String>();
    private List<String> pnv = new ArrayList<String>();

    public RollCall() {
    }

    public void addAbsentExcused(String voter) {
      absEx.add(voter);
    }

    public void addAbsent(String voter) {
      abs.add(voter);
    }

    public void addYea(String voter) {
      yeas.add(voter);
    }

    public void addNay(String voter) {
      nays.add(voter);
    }

    public void addPresent(String voter) {
      present.add(voter);
    }

    public void addPnv(String voter) {
      pnv.add(voter);
    }

    public void setAbsentExcused(List<String> votes) {
      absEx = votes;
    }

    public void setAbsent(List<String> votes) {
      abs = votes;
    }

    public void setYea(List<String> votes) {
      yeas = votes;
    }

    public void setNay(List<String> votes) {
      nays = votes;
    }

    public void setPresent(List<String> votes) {
      present = votes;
    }

    public void setPnv(List<String> votes) {
      pnv = votes;
    }

    public Integer getPresent() {
      return present.size();
    }

  }

  public class Motion {
    private String recordNumber;
    private String line;

    public Motion() {
      recordNumber = "";
      line = "";
    }

    public Motion(String rn, String line) {
      recordNumber = rn;
      this.line = line;
    }

    public String getRecordNumber() {
      return recordNumber;
    }

    public String getLine() {
      return line;
    }

    public void setLine(String line) {
      this.line = line;
    }

    public void setRecordVote(String rv) {
      recordNumber = rv;
    }
  }

  public RecordVote() {
    motion = new Motion();
    rollCall = new RollCall();
  }

  public RecordVote(Motion motion) {
    this.motion = motion;
    rollCall = new RollCall();
  }

  public RecordVote(String record, String motion) {
    this.motion = new Motion(record, motion);
    rollCall = new RollCall();
  }

  public void addAbsentExcused(String voter) {
    rollCall.addAbsentExcused(voter);
  }

  public void addAbsent(String voter) {
    rollCall.addAbsent(voter);
  }

  public void addYea(String voter) {
    rollCall.addYea(voter);
  }

  public void addNay(String voter) {
    rollCall.addNay(voter);
  }

  public void addPresent(String voter) {
    rollCall.addPresent(voter);
  }

  public void addPnv(String voter) {
    rollCall.addPnv(voter);
  }

  public void addChair(String voter) {
    chair = voter;
  }

  public void setAbsentExcused(List<String> votes) {
    rollCall.setAbsentExcused(votes);
  }

  public void setAbsent(List<String> votes) {
    rollCall.setAbsent(votes);
  }

  public void setYea(List<String> votes) {
    rollCall.setYea(votes);
  }

  public void setNay(List<String> votes) {
    rollCall.setNay(votes);
  }

  public void setPresent(List<String> votes) {
    rollCall.setPresent(votes);
  }

  public void setPnv(List<String> votes) {
    rollCall.setPnv(votes);
  }

  public void setChair(String chair) {
    this.chair = chair;
  }

  public void setMotion(String rv, String line) {
    this.motion = new Motion(rv, line);
  }

  public void setCount(String position, Integer cnt) {
    count.put(position, cnt);
  }

  public Integer getPresent() {
    return rollCall.getPresent();
  }

  public String getRecordNumber() {
    return motion.getRecordNumber();
  }

  public String getMotionLine() {
    return motion.getLine();
  }

  public String getChair() {
    return this.chair;
  }
}
