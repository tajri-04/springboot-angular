package com.tajri.jwtApp.controller;

import com.tajri.jwtApp.security.jwt.JwtProvider;
import com.tajri.jwtApp.security.services.UserDetailsServiceImpl;
import com.tajri.jwtApp.security.services.UserPrinciple;
import io.jsonwebtoken.Clock;
import org.assertj.core.util.DateUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RestApiTests {

    @Mock
    private Clock clockMock;

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private UserDetailsServiceImpl jwtUserDetailsService;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        MockitoAnnotations.initMocks(this);

        ReflectionTestUtils.setField(jwtProvider, "jwtExpiration", 3600); // one hour
        ReflectionTestUtils.setField(jwtProvider, "jwtSecret", "mySecret");
    }

    private String createToken(UserPrinciple userPrinciple) {
        String jwt = jwtProvider.generateJwtToken(userPrinciple);
        return jwt;
    }

    @Test
    public void testGenerateTokenGeneratesDifferentTokensForDifferentCreationDates() throws Exception {
        when(clockMock.now())
                .thenReturn(DateUtil.yesterday())
                .thenReturn(DateUtil.now());

        UserPrinciple userPrinciple = mock(UserPrinciple.class);

        final String token = createToken(userPrinciple);
        final String laterToken = createToken(userPrinciple);

        assertThat(token).isNotEqualTo(laterToken);
    }

    @Test
    public void shouldNotAllowAccessToUnauthenticatedUsers() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/test/pm")).andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldAccesResourcesWithValidatedToken() throws Exception {
        when(jwtProvider.validateJwtToken("token")).thenReturn(true);
        when(jwtProvider.getUserNameFromJwtToken("token")).thenReturn("tzse");
        when(jwtUserDetailsService.loadUserByUsername("tzse")).thenReturn(mock(UserDetails.class));

       // assertNotNull(token);
        mvc.perform(MockMvcRequestBuilders.get("/api/test/pm").
                header("Authorization", "Bearer "+"token")).andExpect(status().isOk());
    }
}
