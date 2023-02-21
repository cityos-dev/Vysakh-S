package com.cityos.video.controller;

import com.cityos.video.VideoApplication;
import com.cityos.video.repo.VideoDetailsRepo;
import com.cityos.video.service.VideoService;
import com.cityos.video.view.VideoDetailsView;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = {VideoApplication.class})
class VideoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private VideoService videoService;
    @Autowired
    private VideoDetailsRepo videoDetailsRepo;

    @Test
    void verify_submit_worksNormally() throws Exception {
        final MockMultipartFile file = new MockMultipartFile("file", "file,txt", "multipart/form-data",
                "video".getBytes());
        MockMultipartHttpServletRequestBuilder multipart = MockMvcRequestBuilders
                .multipart("/v1/files");
        ResponseEntity<String> response = ResponseEntity.status(HttpStatusCode.valueOf(201))
                .header("Location", "/app/videos/sample.mp4")
                .body("Video uploaded successfully at location");
        when(videoService.saveVideo(file)).thenReturn(response);
        mockMvc.perform(multipart.file(file)).andExpect(status().is(201))
                .andExpect(jsonPath("$").value("Video uploaded successfully at location"));
    }

    @Test
    void verify_videoDetails_worksNormally() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        VideoDetailsView view = new VideoDetailsView("1", "sample.mp4", 2.3F, now);
        when(videoService.getVideoDetails()).thenReturn(List.of(view));
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/files")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fileId").value("1"))
                .andExpect(jsonPath("$[0].name").value("sample.mp4"))
                .andExpect(jsonPath("$[0].size").value("2.3"))
                .andExpect(jsonPath("$[0].createdAt").value(now.toString()));
    }

    @Test
    void verify_deleteVideo_worksNormally() throws Exception {
        final String id = "1";
        ResponseEntity<String> response = ResponseEntity.status(HttpStatusCode.valueOf(204)).body("File was successfully removed");
        when(videoService.deleteVideo(id)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/files/{id}", id)).andExpect(status().is(204))
                .andExpect(jsonPath("$").value("File was successfully removed"));
    }

}
