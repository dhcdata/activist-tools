package tools.activist.servlet;

import com.iheartradio.m3u8.data.Playlist;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import tools.activist.handler.M3UHandler;
import tools.activist.parser.GranicusParser;

@SuppressWarnings("serial")
@WebServlet(name = "LegePlaylist", value = "/legePlaylist.m3u8")
public class LegePlaylist extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
    Playlist outPlaylist = GranicusParser.getOutPlaylist(req.getParameter("chamber"), req.getParameter("viewId"),
        req.getParameter("clipId"), req.getParameter("startTime"), req.getParameter("endTime"));
    M3UHandler.print(resp, outPlaylist);
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
    Playlist outPlaylist = GranicusParser.getOutPlaylist(req.getParameter("chamber"), req.getParameter("viewId"),
        req.getParameter("clipId"), req.getParameter("startTime"), req.getParameter("endTime"));
    M3UHandler.print(resp, outPlaylist);
  }

}
