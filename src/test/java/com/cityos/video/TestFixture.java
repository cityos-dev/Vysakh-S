package com.cityos.video;

import com.cityos.video.entity.VideoDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.UUID;

public class TestFixture {
    public static VideoDetails getDummyVideoDetails() {
        VideoDetails details = new VideoDetails(UUID.randomUUID() + "/" + "sample.mp4", "jhbcjdhsc", 2.3F);
        ReflectionTestUtils.setField(details, "id", "abc");
        LocalDateTime now = LocalDateTime.now();
        ReflectionTestUtils.setField(details, "createdAt", now);
        return details;
    }
}
