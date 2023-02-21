package com.cityos.video.view;

import java.time.LocalDateTime;

public class VideoDetailsView {

    private String fileId;
    private String name;
    private float size;
    private LocalDateTime createdAt;

    public VideoDetailsView(String fileId, String name, float size, LocalDateTime createdAt) {
        this.fileId = fileId;
        this.name = name;
        this.size = size;
        this.createdAt = createdAt;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
