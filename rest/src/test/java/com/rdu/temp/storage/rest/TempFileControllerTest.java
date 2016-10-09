package com.rdu.temp.storage.rest;

import com.rdu.temp.storage.api.TempFileDescriptor;
import com.rdu.temp.storage.api.TempFileService;
import com.rdu.temp.storage.api.TempFileUpload;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;

/**
 * @author rdu
 * @since 08.10.2016
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TempFileControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private TempFileService tempFileService;

    @Test
    public void shouldUploadSuccess() throws Exception {
        TempFileDescriptor tempFileDescriptor = TempFileDescriptor.builder()
                .name("upload.file.test.txt")
                .contentType("text/plain")
                .size(10)
                .id(UUID.randomUUID().toString())
                .uploaded(new Date())
                .comments(Collections.emptyList())
                .build();

        given(tempFileService.upload(any())).willReturn(tempFileDescriptor);

        ClassPathResource resource = new ClassPathResource("upload.file.test.txt", getClass());

        MultiValueMap<String, Object> request = new LinkedMultiValueMap<>();
        request.add("file", resource);
        ResponseEntity<TempFileDescriptor> response = restTemplate.postForEntity("/", request, TempFileDescriptor.class);

        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(tempFileDescriptor);

        then(tempFileService).should().upload(refEq(TempFileUpload.builder()
                .name("upload.file.test.txt")
                .contentType("text/plain")
                .build(), "stream"));
    }

    @Test
    public void shouldDownloadSuccess() throws Exception {
        String fileId = UUID.randomUUID().toString();

        ClassPathResource resource = new ClassPathResource("upload.file.test.txt", getClass());

        given(tempFileService.download(eq(fileId))).willReturn(resource);

        ResponseEntity<String> response = restTemplate.getForEntity("/{fileId}", String.class, fileId);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
                .isEqualTo("attachment; filename=\"upload.file.test.txt\"");
        assertThat(response.getBody()).isEqualTo("Temp File");
    }
}
