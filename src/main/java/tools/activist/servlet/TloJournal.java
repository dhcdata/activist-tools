package tools.activist.servlet;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.activist.handler.JsonHandler;
import tools.activist.model.floor.JournalEntry;
import tools.activist.parser.TloJournalParser;

@SuppressWarnings("serial")
@WebServlet(name = "TloJournal", value = "/journal")
public class TloJournal extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
    // TODO: Make session a global property somewhere
    String session = req.getParameter("session") == null ? req.getParameter("session") : "85R";
    List<JournalEntry> out = TloJournalParser.getStuff(req.getParameter("chamber"), req.getParameter("journalId"),
        session);
    JsonHandler.writeJsonToResponse(out, resp, out.getClass());
  }

}
