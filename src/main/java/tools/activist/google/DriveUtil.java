package tools.activist.google;

import java.io.IOException;
import java.util.logging.Logger;

// import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class DriveUtil {
  static final Logger log = Logger.getLogger(DriveUtil.class.getName());

  private static final String APPLICATION_NAME = "tools.political.google.drive";

  /** Global instance of the HTTP transport. */
  // private static HttpTransport httpTransport;

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  public static void move(String fileId, String toFolderId) throws IOException {
    File file;
    log.info("begining move");
    Drive driveService = new Drive.Builder(GoogleBatchClient.getTransport(), JSON_FACTORY,
        GoogleBatchClient.getCredential()).setApplicationName(APPLICATION_NAME).build();
    try {
      FileList files = driveService.files().list().execute();

    } catch (Exception e) {
      log.info("no list: " + e.getMessage());
    }

    try {
      file = driveService.files().get(fileId).setFields("parents").execute();
    } catch (Exception e) {
      log.info("Cant get parents: " + e.getMessage());
    }

    // try{
    // StringBuilder previousParents = new StringBuilder();
    // for (String parent: file.getParents()) {
    // previousParents.append(parent);
    // previousParents.append(",");
    // }
    // log.info("Parents: " + previousParents.toString());
    // file = driveService.files().update(fileId, null)
    // .setAddParents(toFolderId)
    // .setRemoveParents(previousParents.toString())
    // .setFields("id, parents")
    // .execute();
    // } catch (Exception e) {
    // log.info("cant move: " + e.getMessage());
    // }

  }
}
