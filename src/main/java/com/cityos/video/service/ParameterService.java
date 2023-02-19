package com.cityos.video.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ParameterService {

    @Value("${application.path}")
    private String applicationPath;

    @Value("${application.video.folder}")
    private String videoFolder;

    public String getApplicationPath() {
        return applicationPath;
    }

    public String getVideoLocation(String fileName) {
        // each video will be stored in a unique folder
        // so that multiple videos can have the same name
        UUID uuid = UUID.randomUUID();
        return applicationPath + videoFolder + "/" + uuid + "/" + fileName;
    }
}
