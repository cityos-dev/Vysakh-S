package com.cityos.video.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class VideoDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String location;
    private String hash;
    private Float fileSize;
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public VideoDetails() {
    }

    public VideoDetails(String location, String hash, Float fileSize) {
        this.location = location;
        this.hash = hash;
        this.fileSize = fileSize;
    }

    public Integer getId() {
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
