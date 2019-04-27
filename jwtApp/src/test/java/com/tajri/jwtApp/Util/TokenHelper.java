package com.tajri.jwtApp.Util;

import com.tajri.jwtApp.security.jwt.JwtProvider;
import com.tajri.jwtApp.security.services.UserDetailsServiceImpl;
import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;


@Component
public class TokenHelper {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private JwtProvider jwtProvider;

    private MockMvc mvc;

    @MockBean
    private UserDetailsServiceImpl jwtUserDetailsService;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(jwtProvider, "jwtExpiration", 3600); // one hour
        ReflectionTestUtils.setField(jwtProvider, "jwtSecret", "mySecret");
    }

   public String  mockToken(){
       when(jwtProvider.validateJwtToken("token")).thenReturn(true);
       when(jwtProvider.getUserNameFromJwtToken("token")).thenReturn("tzse");
       when(jwtUserDetailsService.loadUserByUsername("tzse")).thenReturn(mock(UserDetails.class));
       return "token";
    }
}
