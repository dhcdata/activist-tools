package tools.activist.handler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.google.gson.GsonBuilder;
import java.util.logging.Logger;
import java.lang.reflect.Type;

public class JsonHandler {
  private static final Logger log = Logger.getLogger(JsonHandler.class.getName());

  public static void writeJsonToResponse(Object out, HttpServletResponse resp, Type type) {
    String o;
    if (type == null)
      o = new GsonBuilder().setPrettyPrinting().create().toJson(out);
    else
      o = new GsonBuilder().setPrettyPrinting().create().toJson(out, type);
    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    try {
      resp.getWriter().write(o);
    } catch (IOException e) {
      log.info("Could Not write to User");
    }
  }

}
