import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

public class YtUploadApplication {
    private static final String VIDEO_FILE_PATH = "video.mp4";

    public static void main(String[] args) {
        try {
            YouTube youtube = new YouTube.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), YouTubeAuth.authorize())
                .setApplicationName("YouTube Uploader")
                .build();

            Video video = new Video();
            VideoSnippet snippet = new VideoSnippet();
            snippet.setTitle("My Scheduled Video");
            snippet.setDescription("Video will be published later!");
            snippet.setTags(Arrays.asList("tag1", "tag2"));
            snippet.setCategoryId("22"); // Change

            LocalDateTime localDateTime = LocalDateTime.of(2025, 12, 3, 8, 0, 0);
            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh"));
            Instant instant = zonedDateTime.toInstant();
            DateTime publishAt = new DateTime(instant.toEpochMilli());

            VideoStatus status = new VideoStatus();
            status.setPrivacyStatus("private");
            status.setPublishAt(publishAt);
            status.setEmbeddable(true);
            status.setMadeForKids(false);

            video.setSnippet(snippet);
            video.setStatus(status);

            // Upload video
            AbstractInputStreamContent mediaContent = new FileContent("video/*", new File(VIDEO_FILE_PATH));
            YouTube.Videos.Insert request = youtube.videos().insert("snippet,status", video, mediaContent);
            Video response = request.execute();

            System.out.println("Upload successful! Video ID: " + response.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
