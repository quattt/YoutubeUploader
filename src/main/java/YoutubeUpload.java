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
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class YoutubeUpload {
    private static final String VIDEO_DIRECTORY = "path/to/your/videos"; // Thay bằng đường dẫn thư mục video

    public static void multipleUpload() {
        List<String> videoFilePaths = getVideoFiles(VIDEO_DIRECTORY);
        for (String path : videoFilePaths) {
            String videoId = upload(path);
            if (videoId != null) {
                saveVideoIdToFile(videoId);
            }
        }
    }

    public static void saveVideoIdToFile(String videoId) {
        try (FileWriter writer = new FileWriter("video_ids.txt", true)) {
            writer.write(videoId + "\n");
            System.out.println("Save id to file successfully, id: " + videoId);
        } catch (IOException e) {
            System.out.println("Error save videoId to file, id: " + videoId);
        }
    }

    public static String upload(String videoPath) {
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
            AbstractInputStreamContent mediaContent = new FileContent("video/*", new File(videoPath));
            YouTube.Videos.Insert request = youtube.videos().insert("snippet,status", video, mediaContent);
            Video response = request.execute();

            System.out.println("Upload successful! Video ID: " + response.getId());
            return response.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> getVideoFiles(String directoryPath) {
        List<String> videoFilePaths = new ArrayList<>();
        try {
            Files.walk(Paths.get(directoryPath))
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    File file = path.toFile();
                    if (file.getName().endsWith(".mp4") || file.getName().endsWith(".avi") || file.getName().endsWith(".mov")) {
                        videoFilePaths.add(file.getAbsolutePath());
                    }
                });
        } catch (IOException e) {
            System.out.println("Error scan folder");
        }
        return videoFilePaths;
    }
}
