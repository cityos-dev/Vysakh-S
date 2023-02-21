package com.cityos.video.service;

import com.cityos.video.TestFixture;
import com.cityos.video.entity.VideoDetails;
import com.cityos.video.repo.VideoDetailsRepo;
import com.cityos.video.view.VideoDetailsView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideoServiceTest {

    @Mock
    private ParameterService parameterService;
    @Mock
    private VideoDetailsRepo detailsRepo;
    @InjectMocks
    private VideoService videoService;

    @AfterEach
    void cleanUp() {
        try {
            Files.deleteIfExists(Path.of("./src/test/resources/app/uuid-hbchjbj/sample.mp4"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void verify_saveVideo_throwsException_whenTheSizeIsZero() {
        final MultipartFile file = new MockMultipartFile("sample.mp4", "sample.mp4", "multipart/form-data",
                "".getBytes());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> videoService.saveVideo(file));
        assertEquals("400 BAD_REQUEST \"The file cannot be empty\"", exception.getMessage());
        assertEquals(HttpStatusCode.valueOf(400), exception.getStatusCode());
        verify(detailsRepo, times(0)).existsByHash(any(String.class));
        verify(parameterService, times(0)).getVideoLocation(any(String.class));
        verify(detailsRepo, times(0)).save(any(VideoDetails.class));
    }

    @Test
    void verify_saveVideo_throwsException_whenSameContentExists() {
        final MultipartFile file = new MockMultipartFile("sample.mp4", "sample.mp4", "multipart/form-data",
                "video".getBytes());
        when(detailsRepo.existsByHash("421b47ffd946ca083b65cd668c6b17e6")).thenReturn(true);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> videoService.saveVideo(file));
        assertEquals("409 CONFLICT \"A video with the same contents exists\"", exception.getMessage());
        assertEquals(HttpStatusCode.valueOf(409), exception.getStatusCode());
        verify(detailsRepo, times(1)).existsByHash("421b47ffd946ca083b65cd668c6b17e6");
        verify(parameterService, times(0)).getVideoLocation(any(String.class));
        verify(detailsRepo, times(0)).save(any(VideoDetails.class));
    }

    @Test
    void verify_saveVideo_throwsException_fileNameIsNotMP4() {
        final MultipartFile file = new MockMultipartFile("sample.mp3", "sample.mp3", "multipart/form-data",
                "video".getBytes());
        when(detailsRepo.existsByHash("421b47ffd946ca083b65cd668c6b17e6")).thenReturn(false);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> videoService.saveVideo(file));
        assertEquals("415 UNSUPPORTED_MEDIA_TYPE \"The file format should be mp4\"", exception.getMessage());
        assertEquals(HttpStatusCode.valueOf(415), exception.getStatusCode());
        verify(detailsRepo, times(1)).existsByHash("421b47ffd946ca083b65cd668c6b17e6");
        verify(parameterService, times(0)).getVideoLocation(any(String.class));
        verify(detailsRepo, times(0)).save(any(VideoDetails.class));
    }

    @Test
    void verify_saveVideo_fileIsSaved() {
        final MultipartFile file = new MockMultipartFile("sample.mp4", "sample.mp4", "multipart/form-data",
                "video".getBytes());
        when(detailsRepo.existsByHash("421b47ffd946ca083b65cd668c6b17e6")).thenReturn(false);
        when(parameterService.getVideoLocation("sample.mp4")).thenReturn("./src/test/resources/app/uuid-hbchjbj/sample.mp4");
        ResponseEntity<String> response = assertDoesNotThrow(() -> videoService.saveVideo(file));
        assertEquals(HttpStatusCode.valueOf(201), response.getStatusCode());
        assertEquals("Video uploaded successfully at location", response.getBody());
        assertTrue(response.getHeaders().containsKey("Location"));
        assertEquals(List.of("./src/test/resources/app/uuid-hbchjbj/sample.mp4"), response.getHeaders().get("Location"));
        verify(detailsRepo, times(1)).existsByHash("421b47ffd946ca083b65cd668c6b17e6");
        verify(parameterService, times(1)).getVideoLocation("sample.mp4");
        verify(detailsRepo, times(1)).save(any(VideoDetails.class));
        assertTrue(Files.exists(Path.of("./src/test/resources/app/uuid-hbchjbj/sample.mp4")));
    }

    @Test
    void verify_getVideoDetails_throwsException_whenLocationCannotBeSplitUsingUUID() {
        VideoDetails details = new VideoDetails("abc", "12hh", 2.3F);
        when(detailsRepo.findAll()).thenReturn(List.of(details));
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> videoService.getVideoDetails());
        assertEquals(HttpStatusCode.valueOf(500), exception.getStatusCode());
        assertEquals("500 INTERNAL_SERVER_ERROR \"Unexpected error; Video was not stored in a UUID location\"",
                exception.getMessage());
    }

    @Test
    void verify_getVideoDetails_worksNormally() {
        VideoDetails details = TestFixture.getDummyVideoDetails();
        when(detailsRepo.findAll()).thenReturn(List.of(details));
        List<VideoDetailsView> response = assertDoesNotThrow(() -> videoService.getVideoDetails());
        assertEquals(1, response.size());
        VideoDetailsView view = response.get(0);
        assertEquals("abc", view.getFileId());
        assertEquals("sample.mp4", view.getName());
        assertEquals(2.3F, view.getSize());
        assertEquals(details.getCreatedAt(), view.getCreatedAt());
        verify(detailsRepo, times(1)).findAll();
    }

    @Test
    void verify_deleteVideo_throwsException_whenVideoWithIdDoesNotExist() {
        final String id = "abc";
        when(detailsRepo.findById(id)).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> videoService.deleteVideo(id));
        assertEquals(HttpStatusCode.valueOf(404), exception.getStatusCode());
        assertEquals("404 NOT_FOUND \"Video with the id abc does not exist\"", exception.getMessage());
        verify(detailsRepo, times(1)).findById(id);
        verify(detailsRepo, times(0)).deleteById(any(String.class));
    }

    @Test
    void verify_deleteVideo_worksNormally() {
        final String id = "abc";
        VideoDetails details = TestFixture.getDummyVideoDetails();
        when(detailsRepo.findById(id)).thenReturn(Optional.of(details));
        try {
            Path path = Path.of(details.getLocation());
            Files.createDirectories(path.getParent());
            Files.write(path, "video".getBytes());
            ResponseEntity<String> response = assertDoesNotThrow(() -> videoService.deleteVideo(id));
            assertFalse(Files.exists(path));
            assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());
            assertEquals("File was successfully removed", response.getBody());
            verify(detailsRepo, times(1)).deleteById(id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void verify_downloadVideo_throwsException_whenVideoWithIdDoesNotExist() {
        final String id = "abc";
        when(detailsRepo.findById(id)).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> videoService.downloadVideo(id));
        assertEquals(HttpStatusCode.valueOf(404), exception.getStatusCode());
        assertEquals("404 NOT_FOUND \"Video with the id abc does not exist\"", exception.getMessage());
        verify(detailsRepo, times(1)).findById(id);
    }

    @Test
    void verify_downloadVideo_worksNormally() {
        final String id = "abc";
        VideoDetails details = TestFixture.getDummyVideoDetails();
        when(detailsRepo.findById(id)).thenReturn(Optional.of(details));
        Resource resource = assertDoesNotThrow(() -> videoService.downloadVideo(id));
        assertEquals(resource.getFilename(), "sample.mp4");
        verify(detailsRepo, times(1)).findById(id);
    }
}
