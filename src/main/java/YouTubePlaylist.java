import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.ResourceId;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class YouTubePlaylist {
    private static final String PLAYLIST_ID = "YOUR_PLAYLIST_ID";

    public static void add() {
        List<String> videoIds = new ArrayList<>();
        List<String> failedVideos = new ArrayList<>();

        // Read videoIds from videos_ids.txt
        try (BufferedReader reader = new BufferedReader(new FileReader("video_ids.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                videoIds.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        if (!videoIds.isEmpty()) {
            for (String id : videoIds) {
                System.out.println("Adding video to playlist with videoId: " + id);
                boolean success = addToPlaylist(id);
                if (!success) {
                    failedVideos.add(id);
                }
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("video_ids.txt"))) {
            for (String videoId : failedVideos) {
                writer.write(videoId + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error write file: " + e.getMessage());
        }
        System.out.println("Successfully");
    }

    public static boolean addToPlaylist(String videoId) {
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
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
