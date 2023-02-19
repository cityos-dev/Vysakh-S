package com.cityos.video.service;

import com.cityos.video.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ParameterServiceTest {

    @InjectMocks
    private ParameterService parameterService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(parameterService, "applicationPath", "/app/dev");
        ReflectionTestUtils.setField(parameterService, "videoFolder", "video");
    }

    @Test
    void verify_getApplicationPath() {
        assertEquals("/app/dev", parameterService.getApplicationPath());
    }

    @Test
    void verify_getVideoLocation() {
        String location = parameterService.getVideoLocation("sample.mp4");
        assertNotNull(location);
        assertTrue(location.contains("sample.mp4"));
        Pattern pattern = Pattern.compile(Constants.UUID_REGEX);
        Matcher matcher = pattern.matcher(location);
        assertTrue(matcher.find());
    }
}
