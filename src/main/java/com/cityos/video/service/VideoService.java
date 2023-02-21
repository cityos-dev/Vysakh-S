package com.cityos.video.service;

import com.cityos.video.entity.VideoDetails;
import com.cityos.video.repo.VideoDetailsRepo;
import com.cityos.video.util.Constants;
import com.cityos.video.view.VideoDetailsView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VideoService {

    @Autowired
    private ParameterService parameterService;

    @Autowired
    private VideoDetailsRepo detailsRepo;

    @Transactional
    public ResponseEntity<String> saveVideo(MultipartFile file) {

        byte[] videoBytes;
        try {
            // verifying that the file has some contents
            videoBytes = file.getBytes();
            if (0 == videoBytes.length) {
                throw new ResponseStatusException(HttpStatusCode.valueOf(400), "The file cannot be empty");
            }
            // checking whether another video with the same contents exists
            String md5 = DigestUtils.md5DigestAsHex(videoBytes);
            if (detailsRepo.existsByHash(md5)) {
                throw new ResponseStatusException(HttpStatusCode.valueOf(409),
                        "A video with the same contents exists");
            }
            // saving the video to FS and the details to the DB
            String fileName = file.getOriginalFilename();
            // verifying the extension - should be mp4
            assert fileName != null;
            if (!fileName.endsWith(".mp4") && !fileName.endsWith(".mpg")) {
                throw new ResponseStatusException(HttpStatusCode.valueOf(415), "The file format should be mp4");
            }
            Path path = Path.of(parameterService.getVideoLocation(fileName));
            Files.createDirectories(path.getParent());
            Files.write(path, videoBytes);
            float sizeInMb = (float) file.getSize() / 1000000;
            VideoDetails videoDetails = new VideoDetails(path.toString(), md5, sizeInMb);
            detailsRepo.save(videoDetails);
            // creating the response entity
            return ResponseEntity.status(HttpStatusCode.valueOf(201)).header("Location", path.toString())
                    .body("Video uploaded successfully at location");
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500),
                    "Error reading the request");
        }
    }

    @Transactional(readOnly = true)
    public List<VideoDetailsView> getVideoDetails() {
        Iterable<VideoDetails> details = detailsRepo.findAll();
        List<VideoDetailsView> responseList = new ArrayList<>();
        details.forEach(detail -> {
            String[] split = detail.getLocation().split(Constants.UUID_REGEX);
            if (2 != split.length) {
                throw new ResponseStatusException(HttpStatusCode.valueOf(500),
                        "Unexpected error; Video was not stored in a UUID location");
            }
            String fileName = split[1].substring(1);
            VideoDetailsView view = new VideoDetailsView(detail.getId(), fileName, detail.getFileSize(), detail.getCreatedAt());
            responseList.add(view);
        });
        return responseList;
    }

    @Transactional
    public ResponseEntity<String> deleteVideo(String id) {
        Optional<VideoDetails> detailsOptional = detailsRepo.findById(id);
        VideoDetails videoDetails = detailsOptional.orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404),
                String.format("Video with the id %s does not exist", id)));
        try {
            // deleting the directories
            Files.delete(Path.of(videoDetails.getLocation()));
            Files.delete(Path.of(videoDetails.getLocation()).getParent());
            detailsRepo.deleteById(id);
            return ResponseEntity.status(HttpStatusCode.valueOf(204)).body("File was successfully removed");
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Error while deleting the video");
        }
    }

    @Transactional(readOnly = true)
    public Resource downloadVideo(String id) {
        Optional<VideoDetails> detailsOptional = detailsRepo.findById(id);
        VideoDetails videoDetails = detailsOptional.orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404),
                String.format("Video with the id %s does not exist", id)));
        try {
            return new UrlResource(Path.of(videoDetails.getLocation()).toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Could not process the video for download");
        }
    }

}
