package com.rdu.temp.storage.rest;

import com.rdu.temp.storage.api.TempFileDescriptor;
import com.rdu.temp.storage.api.TempFileService;
import com.rdu.temp.storage.api.TempFileUpload;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
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

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.*;

/**
 * @author rdu
 * @since 08.10.2016
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TempFileControllerTest {
    private static final TempFileDescriptor TEMP_FILE_DESCRIPTOR = TempFileDescriptor.builder()
            .name("upload.file.test.txt")
            .contentType("text/plain")
            .description("Description 1")
            .size(10)
            .fileId(UUID.randomUUID().toString())
            .uploaded(new Date())
            .comments(Collections.emptyList())
            .build();

    @Autowired
    private TestRestTemplate restTemplate;
    @MockBean
    private TempFileService tempFileService;
    @LocalServerPort
    private int port;

    @Test
    public void shouldFindByIdSuccess() throws Exception {
        String fileId = TEMP_FILE_DESCRIPTOR.getFileId();

        given(tempFileService.findById(fileId)).willReturn(TEMP_FILE_DESCRIPTOR);
        ResponseEntity<TempFileDescriptor> response = restTemplate.getForEntity("/files/{fileId}", TempFileDescriptor.class, fileId);

        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(TEMP_FILE_DESCRIPTOR);
    }

    @Test
    public void shouldFindByAllSuccess() throws Exception {
        given(tempFileService.findAll()).willReturn(Collections.singletonList(TEMP_FILE_DESCRIPTOR));
        ResponseEntity<Collection> response = restTemplate.getForEntity("/files", Collection.class);

        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(1);
    }

    @Test
    public void shouldUploadSuccess() throws Exception {
        given(tempFileService.upload(any())).willReturn(TEMP_FILE_DESCRIPTOR);

        ClassPathResource resource = new ClassPathResource("upload.file.test.txt", getClass());

        MultiValueMap<String, Object> request = new LinkedMultiValueMap<>();
        request.add("file", resource);
        request.add("description", "Description 1");
        ResponseEntity<TempFileDescriptor> response = restTemplate.postForEntity("/files", request, TempFileDescriptor.class);

        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(TEMP_FILE_DESCRIPTOR);

        then(tempFileService).should().upload(refEq(TempFileUpload.builder()
                .name("upload.file.test.txt")
                .description("Description 1")
                .contentType("text/plain")
                .build(), "stream"));
    }

    @Test
    public void shouldDownloadSuccess() throws Exception {
        String fileId = UUID.randomUUID().toString();

        ClassPathResource resource = new ClassPathResource("upload.file.test.txt", getClass());

        given(tempFileService.download(eq(fileId))).willReturn(resource);

        ResponseEntity<String> response = restTemplate.getForEntity("/files/{fileId}/download", String.class, fileId);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
                .isEqualTo("attachment; filename=\"upload.file.test.txt\"");
        assertThat(response.getBody()).isEqualTo("Temp File");
    }

    @Test
    public void shouldUpdateFileSuccess() throws Exception {
        given(tempFileService.update(any())).willReturn(TEMP_FILE_DESCRIPTOR);

        String fileId = TEMP_FILE_DESCRIPTOR.getFileId();
        TempFileDescriptor request = TempFileDescriptor.builder()
                .name(TEMP_FILE_DESCRIPTOR.getName())
                .contentType(TEMP_FILE_DESCRIPTOR.getContentType())
                .description(TEMP_FILE_DESCRIPTOR.getDescription())
                .size(TEMP_FILE_DESCRIPTOR.getSize())
                .uploaded(TEMP_FILE_DESCRIPTOR.getUploaded())
                .comments(TEMP_FILE_DESCRIPTOR.getComments())
                .build();
        restTemplate.put("/files/{fileId}", request, fileId);

        then(tempFileService).should().update(TEMP_FILE_DESCRIPTOR);
    }

}
