package com.buildconnect;

import com.buildconnect.auth.dto.AuthDto;
import com.buildconnect.org.model.OrgType;
import com.buildconnect.project.dto.ProjectDto;
import com.buildconnect.project.model.ProjectStage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@org.springframework.test.context.ActiveProfiles("test")
// @Testcontainers // Disabled because user does not have Docker
class BuildConnectApplicationTests {

        // @Container
        // @ServiceConnection
        // static PostgreSQLContainer<?> postgres = new
        // PostgreSQLContainer<>("postgres:15-alpine");

        @Autowired
        TestRestTemplate restTemplate;

        @Test
        void testRegisterLoginAndCreateProject() {
                // 1. Register
                var registerRequest = AuthDto.RegisterRequest.builder()
                                .email("dev@example.com")
                                .password("password123")
                                .orgName("Dev Corp")
                                .orgType(OrgType.DEVELOPER)
                                .build();

                ResponseEntity<AuthDto.AuthResponse> registerResponse = restTemplate.postForEntity(
                                "/api/v1/auth/register",
                                registerRequest,
                                AuthDto.AuthResponse.class);

                assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(registerResponse.getBody()).isNotNull();
                String token = registerResponse.getBody().getToken();

                // 2. Create Project
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token);

                var projectRequest = ProjectDto.CreateProjectRequest.builder()
                                .title("Skyline Tower")
                                .city("New York")
                                .stage(ProjectStage.PLANNING)
                                .description("Luxury apartments")
                                .build();

                HttpEntity<ProjectDto.CreateProjectRequest> createEntity = new HttpEntity<>(projectRequest, headers);

                ResponseEntity<ProjectDto.ProjectResponse> createResponse = restTemplate.exchange(
                                "/api/v1/projects",
                                HttpMethod.POST,
                                createEntity,
                                ProjectDto.ProjectResponse.class);

                assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(createResponse.getBody()).isNotNull();
                assertThat(createResponse.getBody().getTitle()).isEqualTo("Skyline Tower");

                // 3. List Projects
                HttpEntity<Void> listEntity = new HttpEntity<>(headers);
                ResponseEntity<String> listResponse = restTemplate.exchange(
                                "/api/v1/projects",
                                HttpMethod.GET,
                                listEntity,
                                String.class);

                assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(listResponse.getBody()).contains("Skyline Tower");
        }
}
