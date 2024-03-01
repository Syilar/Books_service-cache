package com.example.booksmanagement;

import com.example.booksmanagement.dto.BookResponse;
import com.example.booksmanagement.dto.UpsertBookRequest;
import com.example.booksmanagement.repository.DatabaseBookRepository;
import com.example.booksmanagement.service.BookService;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest
@Sql("classpath:db/init.sql")
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
public class AbstractTest {

    public static final Long UPDATED_ID = 1L;

    public static final String CATEGORY_NAME = "testCategoryName_1";

    public static final Long UPDATED_CATEGORY_ID = 1L;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected BookService bookService;

    @Autowired
    protected DatabaseBookRepository databaseBookRepository;

    @RegisterExtension
    protected static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    protected static PostgreSQLContainer postgreSQLContainer;

    @Container
    protected static final RedisContainer REDIS_CONTAINER = new RedisContainer(DockerImageName.parse("redis:7.0.12"))
            .withExposedPorts(6379)
            .withReuse(true);

    static {
        DockerImageName postgres = DockerImageName.parse("postgres:12.3");

        postgreSQLContainer = (PostgreSQLContainer) new PostgreSQLContainer(postgres)
                .withReuse(true);

        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    public static void registerProperties(DynamicPropertyRegistry registry) {
        String jdbcUrl = postgreSQLContainer.getJdbcUrl();

        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.url", () -> jdbcUrl);

        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379).toString());

//        registry.add("app.integration.base-url", wireMockServer::baseUrl);
    }

    @BeforeEach
    public void before() throws Exception {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();

        stubClient();
    }

    @AfterEach
    public void afterEach() {
        wireMockServer.resetAll();
    }

    private void stubClient() throws Exception {
        List<BookResponse> findAllResponseBody = new ArrayList<>();

        findAllResponseBody.add(new BookResponse(1L, "Book 1", "Author 1", 100, "testCategoryName_1"));
        findAllResponseBody.add(new BookResponse(2L, "Book 2", "Author 2", 100, "testCategoryName_2"));

        wireMockServer.stubFor(WireMock.get("/api/v1/book")
                .willReturn(aResponse()
                        .withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(findAllResponseBody))
                        .withStatus(200)));

        List<BookResponse> findAllByNameCategoryResponseBody = new ArrayList<>();
        findAllByNameCategoryResponseBody.add(new BookResponse(3L,"Book 1", "Author 1", 100, CATEGORY_NAME));
        findAllByNameCategoryResponseBody.add(new BookResponse(4L,"Book 2", "Author 2", 100, CATEGORY_NAME));

        wireMockServer.stubFor(WireMock.get("/api/v1/book/" + CATEGORY_NAME)
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(findAllByNameCategoryResponseBody))
                        .withStatus(200)));

        UpsertBookRequest createRequest = new UpsertBookRequest();
        createRequest.setName("newBook");
        BookResponse createResponseBody = new BookResponse(5L,"newBook", "Author 1", 100, CATEGORY_NAME);

        wireMockServer.stubFor(WireMock.post("/api/v1/book")
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(createRequest)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(createResponseBody))
                        .withStatus(201)));

        UpsertBookRequest updateRequest = new UpsertBookRequest();
        updateRequest.setName("updateName");
        BookResponse updateResponseBody = new BookResponse(UPDATED_ID,"newBook", "Author 1", 100, CATEGORY_NAME);

        wireMockServer.stubFor(put("/api/v1/book/" + UPDATED_ID)
//                .withRequestBody(equalToJson(objectMapper.writeValueAsString(updateRequest)))
                .willReturn(aResponse()
                        .withHeader("Content-ype", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(updateResponseBody))
                        .withStatus(200)));

        wireMockServer.stubFor(delete("/api/v1/book/" + UPDATED_ID)
                .willReturn(aResponse().withStatus(204)));
    }
}
