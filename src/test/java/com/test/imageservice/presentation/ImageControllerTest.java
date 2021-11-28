package com.test.imageservice.presentation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.test.imageservice.models.TagContainer;
import com.test.imageservice.models.dto.ImageDTO;
import com.test.imageservice.models.dto.ImageSearchDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WebAppConfiguration
@AutoConfigureMockMvc
@TestPropertySource(value = ("/application-test.properties"))
@Sql(value = {"/sql/create-instances-before-test.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/delete-instances-after-test.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    void uploadImageSuccess() throws Exception {

        MockMultipartFile file1 = new MockMultipartFile("files", "image1.jpeg", String.valueOf(MediaType.IMAGE_JPEG), ("qwertfds".getBytes()));
        MockMultipartFile file2 = new MockMultipartFile("files", "image2.jpeg", String.valueOf(MediaType.IMAGE_JPEG), ("fdasfda".getBytes()));
        MockMultipartFile file3 = new MockMultipartFile("files", "image3.jpeg", String.valueOf(MediaType.IMAGE_JPEG), ("zvzasdsa".getBytes()));

        this.mockMvc.perform(MockMvcRequestBuilders.multipart("/images/uploadFile")
                        .file(file1)
                        .file(file2)
                        .file(file3)
                        .with(httpBasic("testuser3","test")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("All files uploaded successfully")));
    }

    @Test
    void uploadImageFailure_InvalidContext() throws Exception {

        MockMultipartFile file1 = new MockMultipartFile("files", "image1.jpeg", String.valueOf(MediaType.IMAGE_JPEG), ("qwertfds".getBytes()));
        MockMultipartFile file2 = new MockMultipartFile("files", "image2.jpeg", String.valueOf(MediaType.APPLICATION_XML), ("fdasfda".getBytes()));
        MockMultipartFile file3 = new MockMultipartFile("files", "image3.jpeg", String.valueOf(MediaType.TEXT_XML), ("zvzasdsa".getBytes()));

        this.mockMvc.perform(MockMvcRequestBuilders.multipart("/images/uploadFile")
                        .file(file1)
                        .file(file2)
                        .file(file3)
                        .with(httpBasic("testuser3","test")))
                .andDo(print())
                .andExpect(status().isExpectationFailed())
                .andExpect(content().string(containsString("message\":\"Failed to upload next files: [image2.jpeg, image3.jpeg]")));
    }

    @Test
    void deleteUserImage() throws Exception {

        long imageId = 1L;

        this.mockMvc.perform(delete("/images/delete?imageId=" + imageId)
                        .with(httpBasic("testuser1","test"))
                        .contentType("application/json; charset=utf8"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Image was successfully deleted")));
    }

    @Test
    void deleteUserImageFailure_Forbidden() throws Exception {

        long imageId = 6L;

        this.mockMvc.perform(delete("/images/delete?imageId=" + imageId)
                        .with(httpBasic("testuser1","test"))
                        .contentType("application/json; charset=utf8"))
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("This user can not delete this image")));
    }

    @Test
    void deleteUserImageFailure_NotFound()
            throws Exception {

        long imageId = 10L;

        this.mockMvc.perform(delete("/images/delete?imageId=" + imageId)
                        .with(httpBasic("testuser1","test"))
                        .contentType("application/json; charset=utf8"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Image is not found")));
    }

    @Test
    void deleteAllUserImages()
            throws Exception {

        this.mockMvc.perform(delete("/images/delete/all")
                        .with(httpBasic("testuser1","test"))
                        .contentType("application/json; charset=utf8"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Images successfully deleted")));
    }

    @Test
    void updateUserImageSuccess_AllParams()
            throws Exception {

        long imageId = 1L;
        String newImageName = "newImageName";

        TagContainer tagContainer = new TagContainer();
        List<String> tagNames = new ArrayList<>();
        tagNames.add("Cats");
        tagContainer.setTagNames(tagNames);

        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(tagContainer);

        this.mockMvc.perform(post("/images/update?imageId=" + imageId + "&imageName=" + newImageName)
                .with(httpBasic("testuser1","test"))
                .content(requestJson)
                .contentType("application/json; charset=utf8"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Image successfully updated")));
    }

    @Test
    void updateUserImageSuccess_Tags()
            throws Exception {

        long imageId = 1L;

        TagContainer tagContainer = new TagContainer();
        List<String> tagNames = new ArrayList<>();
        tagNames.add("Cats");
        tagContainer.setTagNames(tagNames);

        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(tagContainer);

        this.mockMvc.perform(post("/images/update?imageId=" + imageId)
                        .with(httpBasic("testuser1","test"))
                        .content(requestJson)
                        .contentType("application/json; charset=utf8"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Image successfully updated")));
    }

    @Test
    void updateUserImageSuccess_Name()
            throws Exception {

        long imageId = 1L;
        String newImageName = "newImageName";

        this.mockMvc.perform(post("/images/update?imageId=" + imageId + "&imageName=" + newImageName)
                        .with(httpBasic("testuser1","test"))
                        .contentType("application/json; charset=utf8"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Image successfully updated")));
    }

    @Test
    void updateUserImageFailure_NoUpdates()
            throws Exception {

        long imageId = 1L;

        this.mockMvc.perform(post("/images/update?imageId=" + imageId)
                        .with(httpBasic("testuser1","test"))
                        .contentType("application/json; charset=utf8"))
                .andExpect(status().isExpectationFailed())
                .andExpect(content().string(containsString("No updates provided")));
    }

    @Test
    void updateUserImageFailure_Forbidden()
            throws Exception {

        long imageId = 4L;
        String newImageName = "newImageName";

        this.mockMvc.perform(post("/images/update?imageId=" + imageId + "&imageName=" + newImageName)
                        .with(httpBasic("testuser1","test"))
                        .contentType("application/json; charset=utf8"))
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("This user can not modify this image")));
    }

    @Test
    void getAllUserImagesSuccess()
            throws Exception {

        MvcResult result = this.mockMvc.perform(get("/images/get/all")
                .with(httpBasic("testuser1","test"))
                .contentType("application/json; charset=utf8"))
            .andExpect(status().isOk())
            .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        List<ImageDTO> responseList = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<ImageDTO>>() {});

        assertEquals(responseList.size(), 3L);
    }

    @Test
    void getImageSuccess()
            throws Exception {

        long imageId = 4L;
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setImageName("testimage4");
        imageDTO.setSize(32543L);
        imageDTO.setId(imageId);
        imageDTO.setAccountId(2L);
        imageDTO.setReference("storage/account-2/24_testimage4.jpg");
        imageDTO.setContentType("image/jpeg");

        MvcResult result = this.mockMvc.perform(get("/images/get?imageId=" + imageId)
                        .with(httpBasic("testuser1","test"))
                        .contentType("application/json; charset=utf8"))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        ImageDTO responseImage = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ImageDTO>() {});

        assertEquals(imageDTO, responseImage);
    }

    @Test
    void getImageFailure()
            throws Exception {

        long imageId = 10L;

        this.mockMvc.perform(get("/images/get?imageId=" + imageId)
                        .with(httpBasic("testuser1","test"))
                        .contentType("application/json; charset=utf8"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Image not found")));
    }

    @Test
    void searchSuccess_FirstPage()
            throws Exception {

        int page = 0;
        int size = 3;
        ImageSearchDTO imageSearchDTO = new ImageSearchDTO();
        List<String> tagNames = new ArrayList<>();
        tagNames.add("Tag5");
        imageSearchDTO.setTags(tagNames);
        imageSearchDTO.setContentType("image/jpeg");
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(imageSearchDTO);

        MvcResult firstPageResult  = this.mockMvc.perform(get("/images/search?page=" + page + "&size=" + size)
                        .with(httpBasic("testuser2","test"))
                        .content(requestJson)
                        .contentType("application/json; charset=utf8"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("totalItems")))
                .andExpect(content().string(containsString("totalPages")))
                .andExpect(content().string(containsString("currentPage")))
                .andReturn();

        HashMap<String, Object> firstPageResponse = mapper.readValue(firstPageResult.getResponse().getContentAsString(), new TypeReference<HashMap<String, Object>>() {});
        List<ImageDTO>  images = (List<ImageDTO>) firstPageResponse.get("images");
        int totalPages = (int) firstPageResponse.get("totalPages");
        int currentPage = (int) firstPageResponse.get("currentPage");
        int totalItems = (int) firstPageResponse.get("totalItems");
        assertEquals(images.size(),3L);
        assertEquals(totalPages, 2);
        assertEquals(currentPage,0);
        assertEquals(totalItems, 5);
    }

    @Test
    void searchSuccess_SecondPage()
            throws Exception {

        int page = 1;
        int size = 3;
        ImageSearchDTO imageSearchDTO = new ImageSearchDTO();
        List<String> tagNames = new ArrayList<>();
        tagNames.add("Tag5");
        imageSearchDTO.setTags(tagNames);
        imageSearchDTO.setContentType("image/jpeg");
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(imageSearchDTO);

        MvcResult secondPageResult  = this.mockMvc.perform(get("/images/search?page=" + page + "&size=" + size)
                        .with(httpBasic("testuser2","test"))
                        .content(requestJson)
                        .contentType("application/json; charset=utf8"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("totalItems")))
                .andExpect(content().string(containsString("totalPages")))
                .andExpect(content().string(containsString("currentPage")))
                .andReturn();

        HashMap<String, Object> secondPageResponse = mapper.readValue(secondPageResult.getResponse().getContentAsString(), new TypeReference<HashMap<String, Object>>() {});
        List<ImageDTO>  imagesSecondPage = (List<ImageDTO>) secondPageResponse.get("images");
        int totalPages = (int) secondPageResponse.get("totalPages");
        int currentPage = (int) secondPageResponse.get("currentPage");
        int totalItems = (int) secondPageResponse.get("totalItems");
        assertEquals(imagesSecondPage.size(),2L);
        assertEquals(totalPages, 2);
        assertEquals(currentPage,1);
        assertEquals(totalItems, 5);
    }
}