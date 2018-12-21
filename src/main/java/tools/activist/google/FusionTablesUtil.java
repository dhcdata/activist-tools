package tools.activist.google;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import com.google.api.client.googleapis.batch.BatchCallback;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.json.GoogleJsonErrorContainer;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
// import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.fusiontables.Fusiontables;
import com.google.api.services.fusiontables.model.Sqlresponse;

import tools.activist.model.FusionResponse;

/**
 * @author Chris Howe
 *
 */
public class FusionTablesUtil {
  private static final Logger log = Logger.getLogger(FusionTablesUtil.class.getName());

  /**
   * Be sure to specify the name of your application. If the application name is
   * {@code null} or blank, the application will log a warning. Suggested format
   * is "MyCompany-ProductName/1.0".
   */
  private static final String APPLICATION_NAME = "tools.political.fusiontables";

  /** Global instance of the HTTP transport. */
  // private static HttpTransport httpTransport;

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private static GoogleBatchClient gbc = GoogleBatchClient.getInstance();
  private static Fusiontables fusiontables = new Fusiontables.Builder(GoogleBatchClient.getTransport(), JSON_FACTORY,
      GoogleBatchClient.getCredential()).setApplicationName(APPLICATION_NAME).build();

  private static Drive driveService = new Drive.Builder(GoogleBatchClient.getTransport(), JSON_FACTORY,
      GoogleBatchClient.getCredential()).setApplicationName(APPLICATION_NAME).build();

  public static <T, E> BatchRequest fusionQueue(String sql, HashMap<String, FusionResponse> queueResponseMap,
      String key) {
    return gbc.queue(fusionSqlGet(sql), Sqlresponse.class, GoogleJsonErrorContainer.class,
        fusionCallback(queueResponseMap, key));
  }

  private static HttpRequest fusionSqlGet(String sql) {
    try {
      return fusiontables.query().sqlGet(sql).buildHttpRequest();
    } catch (IOException e) {
      log.info("Could not build HTTP Request: " + e.getMessage());
      return null;
    }

  }

  public static <T, E> BatchRequest fusionPostQueue(String sql, HashMap<String, FusionResponse> queueResponseMap,
      String key) {
    return gbc.queue(fusionSqlPost(sql), Sqlresponse.class, GoogleJsonErrorContainer.class,
        fusionCallback(queueResponseMap, key));
  }

  private static HttpRequest fusionSqlPost(String sql) {
    try {
      return fusiontables.query().sql(sql).buildHttpRequest();
    } catch (IOException e) {
      log.info("Could not build HTTP Request: " + e.getMessage());
      return null;
    }

  }

  private static BatchCallback<Sqlresponse, GoogleJsonErrorContainer> fusionCallback(
      HashMap<String, FusionResponse> queueResponseMap, String key) {
    return new BatchCallback<Sqlresponse, GoogleJsonErrorContainer>() {
      public void onSuccess(Sqlresponse sqlResp, HttpHeaders responseHeaders) {
        queueResponseMap.put(key, new FusionResponse(sqlResp));
      }

      public void onFailure(GoogleJsonErrorContainer e, HttpHeaders responseHeaders) {
        queueResponseMap.put(key, new FusionResponse(e));
      }
    };
  }

  public static void listFiles() {
    try {
      FileList files = driveService.files().list().setFields("files(id, name, permissions, parents)").execute();
      for (File file : files.getFiles()) {
        log.info("has file:" + file.toPrettyString());
      }
    } catch (Exception e) {
      log.info("Files Error: " + e.getMessage());
    }
  }
}
