package tools.activist.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.activist.handler.JsonHandler;
import tools.activist.model.RepresentResponse;
import tools.activist.parser.GoogleFusionParser;

@SuppressWarnings("serial")
@WebServlet(name = "WhoRepresents", value = "/whoRepresents")
public class WhoRepresents extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
    RepresentResponse outString = GoogleFusionParser.getStuff(req.getParameter("address"));
    JsonHandler.writeJsonToResponse(outString, resp, outString.getClass());
  }
}
