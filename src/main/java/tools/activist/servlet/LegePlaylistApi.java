package tools.activist.servlet;

import com.iheartradio.m3u8.data.Playlist;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import tools.activist.handler.M3UHandler;
import tools.activist.parser.GranicusParser;

@SuppressWarnings("serial")
@WebServlet(name = "LegePlaylistApi", value = "/playlist/*")
public class LegePlaylistApi extends HttpServlet {
  private static final Logger log = Logger.getLogger(LegePlaylistApi.class.getName());
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
    String[] pathParts = req.getPathInfo().split("/");
    Playlist outPlaylist = GranicusParser.getOutPlaylist(
                                          pathParts[1], //Chamber
                                          pathParts[3], // view
                                          pathParts[5], // clip
                                          pathParts[7], // start
                                          pathParts[9]  // stop
                                        );
    // TODO: check outPlaylist to make sure we're printing a m3u8
    M3UHandler.print(resp, outPlaylist);

  }

}
