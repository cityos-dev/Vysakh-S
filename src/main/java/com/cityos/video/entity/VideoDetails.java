package com.cityos.video.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class VideoDetails {

    @Id
    private final String id;
    private String location;
    private String hash;
    private Float fileSize;
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public VideoDetails() {
        id = RandomStringUtils.randomAlphabetic(15);
    }

    public VideoDetails(String location, String hash, Float fileSize) {
        id = RandomStringUtils.randomAlphabetic(15);
        this.location = location;
        this.hash = hash;
        this.fileSize = fileSize;
    }

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHash() {
        return hash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Float getFileSize() {
        return fileSize;
    }

    public void setFileSize(Float fileSize) {
        this.fileSize = fileSize;
    }
}
