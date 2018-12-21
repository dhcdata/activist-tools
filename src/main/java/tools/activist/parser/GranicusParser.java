package tools.activist.parser;

import com.iheartradio.m3u8.data.Playlist;
import com.iheartradio.m3u8.data.TrackData;
import com.mashape.unirest.http.Unirest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import tools.activist.handler.M3UHandler;

public class GranicusParser {
  static final String GRANICUS = ".granicus.com";
  static final String GRANICUS_ACCT = "tlc";
  static final String GRANICUS_PLAYLIST = "playlist.m3u8";
  static final String GRANICUS_CLIP = "/MediaPlayer.php";
  static final String GRANICUS_STREAMS = "/player/GetStreams.php";
  static final Logger log = Logger.getLogger(GranicusParser.class.getName());

  public static Playlist getOutPlaylist(String chamber, String viewId, String clipId, String start, String end) {
    String playlistUrl = getGranicusPlaylistUrl("http://" + GRANICUS_ACCT + chamber.toLowerCase() + GRANICUS, viewId,
        clipId);
    Playlist mediaPlaylist = M3UHandler
        .getPlaylist(playlistUrl.replace(GRANICUS_PLAYLIST, M3UHandler.getMasterPlaylistUri(playlistUrl)));
    List<TrackData> outTracks = M3UHandler.getTimeClippedOutTracks(mediaPlaylist.getMediaPlaylist().getTracks(),
        playlistUrl.replace(GRANICUS_PLAYLIST, ""), getTime(start, end));
    return mediaPlaylist.buildUpon()
        .withMediaPlaylist(mediaPlaylist.getMediaPlaylist().buildUpon().withTracks(outTracks).build()).build();
  }

  private static String getGranicusPlaylistUrl(String domain, String viewId, String clipId) {
    try {
      return Unirest.get(domain + GRANICUS_STREAMS)
          .queryString("clip_id", (Object) getGranicusClip(domain, viewId, clipId)).asJson().getBody().getArray().get(1)
          .toString();
    } catch (Exception e) {
      log.info("error in jsonResponse");
    }
    return null;
  }

  private static String getGranicusClip(String domain, String viewId, String clipId) {
    String str = "";
    try {
      str = Unirest.get(domain + GRANICUS_CLIP).queryString("view_id", viewId).queryString("clip_id", clipId).asString()
          .getBody().toString();
    } catch (Exception e) {
      log.info(e.getMessage());
    }
    return Arrays.asList(str.split("\n")).stream().filter(line -> line.contains("clipId")).collect(Collectors.toList())
        .get(0).split("'")[1];
  }

  private static HashMap<String, Float> getTime(String start, String end) {
    HashMap<String, Float> time = new HashMap<String, Float>();
    time.put("start", start.contains(":") ? parseTime(start) : new Float(start));
    time.put("end", end.contains(":") ? parseTime(end) : new Float(end));
    return time;
  }

  private static int parseTime(String t) {
    String[] timePieces = t.split(":");
    if (timePieces.length == 1)
      return Integer.parseInt(timePieces[0]);
    else if (timePieces.length == 2)
      return Integer.parseInt(timePieces[0]) * 60 + Integer.parseInt(timePieces[1]);
    else if (timePieces.length == 3)
      return Integer.parseInt(timePieces[0]) * 3600 + Integer.parseInt(timePieces[1]) * 60
          + Integer.parseInt(timePieces[2]);
    else
      return 0;
  }
}
