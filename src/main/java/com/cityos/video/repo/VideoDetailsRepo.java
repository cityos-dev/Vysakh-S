package com.cityos.video.repo;

import com.cityos.video.entity.VideoDetails;
import org.springframework.data.repository.CrudRepository;

public interface VideoDetailsRepo extends CrudRepository<VideoDetails, Integer> {

    boolean existsByHash(String hash);
}
