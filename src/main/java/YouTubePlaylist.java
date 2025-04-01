import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.ResourceId;

public class YouTubePlaylist {
    private static final String PLAYLIST_ID = "YOUR_PLAYLIST_ID";

    public static void addToPlaylist(String videoId) {
        try {
            YouTube youtube = new YouTube.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), YouTubeAuth.authorize())
                .setApplicationName("YouTube Uploader")
                .build();

            PlaylistItemSnippet snippet = new PlaylistItemSnippet();
            snippet.setPlaylistId(PLAYLIST_ID);
            ResourceId resourceId = new ResourceId();
            resourceId.setKind("youtube#video");
            resourceId.setVideoId(videoId);
            snippet.setResourceId(resourceId);

            PlaylistItem playlistItem = new PlaylistItem();
            playlistItem.setSnippet(snippet);

            YouTube.PlaylistItems.Insert request = youtube.playlistItems().insert("snippet", playlistItem);
            request.execute();

            System.out.println("Video added to playlist!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
