package tools.activist.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.batch.BatchCallback;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.HttpRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.cloud.MonitoredResource;
// import com.google.cloud.logging.LogEntry;
// import com.google.cloud.logging.Logging;
// import com.google.cloud.logging.LoggingOptions;
// import com.google.cloud.logging.Payload.StringPayload;
import com.google.api.services.drive.DriveScopes;
import java.util.ArrayList;
import com.google.api.services.iam.v1.IamScopes;

public class GoogleBatchClient {

  private static GoogleBatchClient instance = null;
  private static final Logger log = Logger.getLogger(GoogleBatchClient.class.getName());
  // private static final Logging log =
  // LoggingOptions.getDefaultInstance().getService();
  private static final String APPLICATION_NAME = "tools.political.googleClient";
  private static HttpTransport httpTransport = transport();
  private static GoogleCredential credential = credential();
  private static BatchRequest batch = new BatchRequest(httpTransport, credential);
  // private static BatchRequest batch;

  protected GoogleBatchClient() {
    // Exists only to defeat instantiation.
  }

  public static GoogleBatchClient getInstance() {
    if (instance == null) {
      try {
        instance = new GoogleBatchClient();
      } catch (Exception e) {
        log.log(Level.SEVERE, "Error initializing GoogleBatchClient");
      }
    }
    return instance;
  }

  public static void execute() {
    try {
      batch.execute();
    } catch (IOException e) {
      log.info("Could not execute batch: " + e.getMessage());
    }
  }

  public static HttpTransport getTransport() {
    return httpTransport;
  }

  public static GoogleCredential getCredential() {
    return credential;
  }

  public static BatchRequest getBatch() {
    return batch;
  }

  public <T, E> BatchRequest queue(HttpRequest httpRequest, Class<T> dataClass, Class<E> errorClass,
      BatchCallback<T, E> callback) {
    try {
      return batch.queue(httpRequest, dataClass, errorClass, callback);
    } catch (IOException i) {
      log.info("gbc --errrored");
      return null;
    }
  }

  private static HttpTransport transport() {
    try {
      return GoogleNetHttpTransport.newTrustedTransport();
    } catch (IOException i) {
      log.info("gbc --errrored");
      return null;
    } catch (GeneralSecurityException g) {
      log.info("gbc --errrored");
      return null;
    }
  }

  private static GoogleCredential credential() {
    try {
      GoogleCredential cr = GoogleCredential.getApplicationDefault(); // IOException
      String le = "Credential: " + cr.getServiceAccountId() + " , user: " + cr.getServiceAccountUser();
      log.log(Level.SEVERE, le);
      if (cr.createScopedRequired()) {
        Collection<String> GAE_SCOPES = new ArrayList<String>();
        GAE_SCOPES.add(com.google.api.services.fusiontables.FusiontablesScopes.FUSIONTABLES);
        GAE_SCOPES.addAll(DriveScopes.all());
        GAE_SCOPES.addAll(IamScopes.all());
        // Collection<String> GAE_SCOPES =
        // Collections.singletonList(com.google.api.services.fusiontables.FusiontablesScopes.FUSIONTABLES);
        cr = cr.createScoped(GAE_SCOPES);
      }
      return cr;
    } catch (IOException i) {
      log.info("gbc --errrored" + i.getMessage());
      return null;
    }
  }
}
