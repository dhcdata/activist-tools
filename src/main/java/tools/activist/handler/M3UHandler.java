package tools.activist.handler;

import com.iheartradio.m3u8.Encoding;
import com.iheartradio.m3u8.Format;
import com.iheartradio.m3u8.PlaylistParser;
import com.iheartradio.m3u8.PlaylistWriter;
import com.iheartradio.m3u8.data.Playlist;
import com.iheartradio.m3u8.data.TrackData;
import com.mashape.unirest.http.Unirest;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;

public class M3UHandler {
  private static final Logger log = Logger.getLogger(M3UHandler.class.getName());

  public static void print(HttpServletResponse resp, Playlist playlist) {
    try {
      resp.setContentType("application/x-mpegURL");
      // TODO: this does not maintain duration as float in m3u8
      new PlaylistWriter(resp.getOutputStream(), Format.EXT_M3U, Encoding.UTF_8).write(playlist);
    } catch (Exception e) {
      log.info("failure to write");
    }
  }

  public static Playlist getPlaylist(String url) {
    String resp = "";
    try {
      resp = Unirest.get(url).asString().getBody();
    } catch (Exception e) {
      log.info("error in playlist");
    }
    try {
      return new PlaylistParser(new ByteArrayInputStream(resp.getBytes(StandardCharsets.UTF_8)), Format.EXT_M3U,
          Encoding.UTF_8).parse();

    } catch (Exception e) {
      log.info("error parsing Playlist");
      return null;
    }
  }

  public static String getMasterPlaylistUri(String playlistUrl) {
    return getPlaylist(playlistUrl).getMasterPlaylist().getPlaylists().get(0).getUri();
  }

  public static List<TrackData> getTimeClippedOutTracks(List<TrackData> tracks, String domainPathBase,
      HashMap<String, Float> time) {
    List<TrackData> outTracks = new ArrayList<TrackData>();
    float thisStartTime = 0.0f;
    float thisEndTime;
    for (TrackData track : tracks) {
      thisEndTime = thisStartTime + track.getTrackInfo().duration;
      if ((time.get("start") == null || thisEndTime > time.get("start"))
          && (time.get("end") == null || thisStartTime < time.get("end"))) {
        outTracks.add(track.buildUpon().withUri(domainPathBase + track.getUri()).build());
      }
      thisStartTime = thisEndTime;
    }
    ;
    return outTracks;
  }
}
