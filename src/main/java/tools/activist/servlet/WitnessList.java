package tools.activist.servlet;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.activist.handler.JsonHandler;
import tools.activist.model.TloRegistration;
import tools.activist.parser.TloWitnessParser;

@SuppressWarnings("serial")
@WebServlet(name = "WitnessList", value = "/committee-witness")
public class WitnessList extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
    // TODO: Make session a global property somewhere
    // String session = req.getParameter("session") == null ?
    // req.getParameter("session") : "85R";
    List<TloRegistration> out = TloWitnessParser.getStuff(req.getParameter("meeting"), "85R");
    JsonHandler.writeJsonToResponse(out, resp, out.getClass());
  }

}
