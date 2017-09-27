package cc.sjyuan.spring.jwt.api;

import cc.sjyuan.spring.jwt.configuration.security.LoginRequestUser;
import cc.sjyuan.spring.jwt.entity.Privilege;
import cc.sjyuan.spring.jwt.entity.Role;
import cc.sjyuan.spring.jwt.entity.User;
import cc.sjyuan.spring.jwt.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthenticationControllerTest extends BaseControllerTest {

    @Autowired
    private UserService userService;

    @Override
    @BeforeEach
    void setup() {
        super.setup();
        Privilege privilege = Privilege.builder().name(Privilege.Symbol.CREATE_USER.description()).symbol(Privilege.Symbol.CREATE_USER).build();
        Role role = Role.builder().name(Role.Symbol.SYSTEM_ADMIN.description()).symbol(Role.Symbol.SYSTEM_ADMIN).build();
        role.setPrivileges(Collections.singletonList(privilege));
        userService.create(User.builder().role(role).name("future_star").password("123").build());
    }

    @Test
    void should_login_successfully() throws Exception {
        LoginRequestUser loginRequestBody = LoginRequestUser.builder()
                .username("future_star").password("123").build();

        mockMvc.perform(post("/api/authentication")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(loginRequestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("future_star"));
    }

    @Test
    void should_login_failed_when_login_with_bad_credential() throws Exception {
        LoginRequestUser loginRequestBody = LoginRequestUser.builder()
                .username("future_sta").password("wrong_password").build();

        mockMvc.perform(post("/api/authentication")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(loginRequestBody)))
                .andExpect(status().isUnauthorized());
    }

}