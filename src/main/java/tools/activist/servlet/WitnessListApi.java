package tools.activist.servlet;

import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import tools.activist.parser.TloWitnessParser;
import tools.activist.handler.JsonHandler;
import tools.activist.model.TloRegistration;
import java.util.List;

@SuppressWarnings("serial")
@WebServlet(name = "WitnessListApi", value = "/witness-list/*")
public class WitnessListApi extends HttpServlet {
  private static final Logger log = Logger.getLogger(WitnessListApi.class.getName());

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
    // TODO: Make session a global property somewhere
    // TODO: API Checks witness-list/session/{{ session }}/meeting/{{ meeting }}
    String[] pathParts = req.getPathInfo().split("/");
    String session = req.getParameter("session") == null ? req.getParameter("session") : "85R";
    List<TloRegistration> out = TloWitnessParser.getStuff(pathParts[4], // meeting
        pathParts[2] // session
    );
    JsonHandler.writeJsonToResponse(out, resp, out.getClass());
  }

}
