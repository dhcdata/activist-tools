package tools.activist.google;

import com.google.api.services.iam.v1.Iam;//
import com.google.api.client.http.HttpHeaders;//
import com.google.api.client.http.HttpRequest;//
import com.google.api.client.http.HttpTransport;//
import com.google.api.client.json.JsonFactory;//
import com.google.api.client.json.jackson2.JacksonFactory;//
import java.util.logging.Logger;

public class IdentityUtils {
  private static final Logger log = Logger.getLogger(IdentityUtils.class.getName());

  /**
   * Be sure to specify the name of your application. If the application name is
   * {@code null} or blank, the application will log a warning. Suggested format
   * is "MyCompany-ProductName/1.0".
   */
  private static final String APPLICATION_NAME = "tools.political.google.IdentityUtils";

  /** Global instance of the HTTP transport. */
  private static HttpTransport httpTransport;

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private static GoogleBatchClient gbc = GoogleBatchClient.getInstance();
  private static Iam iamService = new Iam.Builder(gbc.getTransport(), JSON_FACTORY, gbc.getCredential())
      .setApplicationName(APPLICATION_NAME).build();

  public static void getProjects() {
    try {

      log.info(iamService.projects().serviceAccounts().list("projects/deft-bonsai-175811").execute().toPrettyString());
    } catch (Exception e) {
      log.info(e.getMessage());
    }
  }
}
