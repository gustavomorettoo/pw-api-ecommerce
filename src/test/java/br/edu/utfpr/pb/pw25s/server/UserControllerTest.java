package br.edu.utfpr.pb.pw25s.server;

import br.edu.utfpr.pb.pw25s.server.model.User;
import br.edu.utfpr.pb.pw25s.server.repository.UserRepository;
import br.edu.utfpr.pb.pw25s.server.shared.GenericResponse;
import org.apiguardian.api.API;
import org.assertj.core.api.Assertions;
import org.hibernate.annotations.NaturalId;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.awt.event.WindowStateListener;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerTest// <- isso é uma switch
{
    private final String API_USERS= "/users";

    //imersão de controle, o spring irá entregar uma estância a cada vez que for requisitado
    //injeção de dependência
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;
    //methodName_condition_expectedBehavior

    @BeforeEach
    public void cleanup(){
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    @Test
    @DisplayName("Post a new User when the User is valid receive a 200 Ok")
    public void postUser_whenUserIsValid_receiveOk(){
    //estanciar um novo usuario


      /* User user = new User();
       user.setUsername("test-user");
       user.setDisplayName("test-Display");
       user.setPassword("P4ssword");
*/
       //criar um endpoint para fazer um post do usuario
        ResponseEntity<Object> response =
                testRestTemplate.postForEntity(
                        API_USERS,
                        createValidUser(),
                        Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void postUser_whenUserIsValid_userSavedToDatabase(){
        //estanciar um novo usuario
        testRestTemplate.postForEntity(
                API_USERS,
                createValidUser(),
                Object.class);

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    public void postUser_whenUserIsValid_passwordIsHashedInDatabase(){
        User user = createValidUser();
        testRestTemplate.postForEntity(API_USERS, user, Object.class);

        List<User> userList = userRepository.findAll();
        assertThat(userList.get(0).getPassword()).isNotEqualTo(user.getPassword());
    }

    @Test
    public void postUser_whenUserHasNullUsername_receiveBadRequest() {
        User user = createValidUser();
        user.setUsername(null);
        ResponseEntity<Object> response =
                testRestTemplate.postForEntity(API_USERS, user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasNullPassword_receiveBadRequest() {
        User user = createValidUser();
        user.setPassword(null);
        ResponseEntity<Object> response =
                testRestTemplate.postForEntity(API_USERS, user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasNullDisplayName_receiveBadRequest() {
        User user = createValidUser();
        user.setDisplayName(null);
        ResponseEntity<Object> response =
                testRestTemplate.postForEntity(API_USERS, user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasUsernameWithLessThanRequired_receiveBadRequest()
    {
        User user = createValidUser();
        user.setUsername("abc");

        ResponseEntity<Object> response =
                testRestTemplate.postForEntity(API_USERS, user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordWithLessThanRequired_receiveBadRequest()
    {
        User user = createValidUser();
        user.setPassword("abc");

        ResponseEntity<Object> response =
                testRestTemplate.postForEntity(API_USERS, user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasDisplayNameWithLessThanRequired_receiveBadRequest()
    {
        User user = createValidUser();
        user.setDisplayName("abc");

        ResponseEntity<Object> response =
                testRestTemplate.postForEntity(API_USERS, user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasUsernameExceededTheLenghtLimit_receiveBadRequest()
    {
        User user = createValidUser();
        String usernameWith256Chars = IntStream.range(1, 256)
                .mapToObj(x -> "a").collect(Collectors.joining());
        user.setUsername(usernameWith256Chars);

        ResponseEntity<Object> response =
                testRestTemplate.postForEntity(API_USERS, user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordAllLowercase_receiveBadRequest(){
        User user = createValidUser();
        user.setPassword("password");

        ResponseEntity<Object> response =
                testRestTemplate.postForEntity(API_USERS, user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    private User createValidUser(){
        //estanciar um novo usuario
        return User.builder()
                .username("test-user")
                .displayName("test-Display")
                .password("P4ssword")
                .build();
    }

    @Test
    public void postUser_whenUserIsValid_receiveSuccessMessage(){
        ResponseEntity<GenericResponse> response =
                testRestTemplate.postForEntity(
                        API_USERS,
                        createValidUser(),
                        GenericResponse.class);
        assertThat(response.getBody().getMessage()).isNotNull();
    }

}
