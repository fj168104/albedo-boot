package com.albedo.java.web.rest;

import com.albedo.java.AlbedoBootWebApp;
import com.albedo.java.common.config.AlbedoProperties;
import com.albedo.java.common.security.MailService;
import com.albedo.java.common.security.SecurityUtil;
import com.albedo.java.common.security.jwt.TokenProvider;
import com.albedo.java.modules.sys.domain.User;
import com.albedo.java.modules.sys.repository.UserRepository;
import com.albedo.java.modules.sys.service.UserService;
import com.albedo.java.modules.sys.web.UserResource;
import com.albedo.java.util.PublicUtil;
import com.albedo.java.util.domain.Globals;
import com.albedo.java.vo.account.LoginVo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the AccountResource REST controller.
 *
 * @see UserResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AlbedoBootWebApp.class)
public class AccoutResourceIntTest {

    public final static String DEFAULT_LOGINID="user-jwt-controller";
    public final static String DEFAULT_PASSWORD="test11";
    @Mock
    private UserService userService;

//    @Mock
//    private MailService mockMailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
//        doNothing().when(mockMailService).sendActivationEmail(anyObject());

//        AccoutResource accountResource =
//            new AccoutResource(tokenProvider, authenticationManager);
//
//        AccoutResource accountUserMockResource =
//                new AccoutResource(tokenProvider, authenticationManager);

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
//                .standaloneSetup(accountResource)
//            .setMessageConverters(httpMessageConverters)
            .build();

    }
    @Before
    public void initTest() {
        User user = new User();
        user.setLoginId(DEFAULT_LOGINID);
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));

        userService.save(user);
    }


    @Test
    public void testGetUnknownAccount() throws Exception {
        when(userService.getUserWithAuthorities(SecurityUtil.getCurrentUserId())).thenReturn(null);

        mockMvc.perform(get("/api/account")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Globals.MSG_TYPE_WARNING));
    }

    @Test
    public void testAuthorize() throws Exception {

        LoginVo login = new LoginVo();
        login.setUsername(DEFAULT_LOGINID);
        login.setPassword(DEFAULT_PASSWORD);
        mockMvc.perform(post("/api/authenticate")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testAuthorizeWithRememberMe() throws Exception {
        User user = new User();
        user.setLoginId("user-jwt-controller-remember-me");
        user.setEmail("user-jwt-controller-remember-me@example.com");
        user.setActivated(true);
        user.setPassword(passwordEncoder.encode("test11"));

        userService.save(user);

        LoginVo login = new LoginVo();
        login.setUsername("user-jwt-controller-remember-me");
        login.setPassword("test11");
        login.setRememberMe(true);
        mockMvc.perform(post("/api/authenticate")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testAuthorizeFails() throws Exception {
        LoginVo login = new LoginVo();
        login.setUsername("wrong-user");
        login.setPassword("wrong password");
        mockMvc.perform(post("/api/authenticate")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.data").isEmpty());
    }
    @Test
    public void testAuthorizeSuccess() throws Exception {
        LoginVo login = new LoginVo();
        login.setUsername("admin");
        login.setPassword("111111");
        mockMvc.perform(post("/api/authenticate")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(login)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.data").isEmpty());
    }
}
