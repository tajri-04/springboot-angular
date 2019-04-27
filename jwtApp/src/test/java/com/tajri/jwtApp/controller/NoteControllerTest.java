package com.tajri.jwtApp.controller;


import com.tajri.jwtApp.Util.JsonHelper;
import com.tajri.jwtApp.Util.TokenHelper;
import com.tajri.jwtApp.model.notes.Note;
import com.tajri.jwtApp.repository.notes.NoteRepository;
import com.tajri.jwtApp.security.jwt.JwtProvider;
import com.tajri.jwtApp.security.services.UserDetailsServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NoteControllerTest {

    @Autowired
    TokenHelper tokenHelper;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    String token;

    @MockBean
    NoteRepository noteRepository;


    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        MockitoAnnotations.initMocks(this);

         token = tokenHelper.mockToken();
    }

    @Test
    public void cantAccessRessourcewithoutToken() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/notes")).andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldAccesResourcesWithValidatedToken() throws Exception {

        List<Note> notes = Arrays.asList(new Note());
        when(noteRepository.findAll()).thenReturn(notes);

        // assertNotNull(token);
        mvc.perform(MockMvcRequestBuilders.get("/api/notes").
                header("Authorization", "Bearer "+token)).andExpect(status().isOk())
        .andExpect(content().string( "[{\"id\":null," +
                                                    "\"title\":null," +
                                                    "\"content\":null," +
                                                    "\"createdAt\":null," +
                                                    "\"updatedAt\":null}]"));
    }

    @Test
    public void createNoteTest() throws Exception {
        Note note = new Note();
        when(noteRepository.save(note)).thenReturn(note);

        mvc.perform(MockMvcRequestBuilders.get("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonHelper.asJsonString(note))
                        .header("Authorization", "Bearer "+token))
                .andExpect(status().isOk());
    }


}
