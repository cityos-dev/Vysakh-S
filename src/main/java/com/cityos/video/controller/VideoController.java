package com.cityos.video.controller;

import com.cityos.video.service.VideoService;
import com.cityos.video.view.VideoDetailsView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("v1")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @PostMapping("files")
    public ResponseEntity<String> submit(@RequestParam("file") MultipartFile file) {
        return videoService.saveVideo(file);
    }

    @GetMapping("files")
    public List<VideoDetailsView> getVideoDetails() {
        return videoService.getVideoDetails();
    }

    @DeleteMapping("files/{id}")
    public ResponseEntity<String> deleteVideo(@PathVariable("id") int id) {
        return videoService.deleteVideo(id);
    }

    @GetMapping("files/{id}")
    public ResponseEntity<Resource> downloadVideo(@PathVariable("id") int id) {
        Resource resource = videoService.downloadVideo(id);
        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }

    @GetMapping("health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok().body("OK");
    }

}
