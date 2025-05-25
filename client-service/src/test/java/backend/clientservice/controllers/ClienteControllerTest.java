package backend.clientservice.controllers;

import backend.dto.request.ClientDtoRequest;
import backend.dto.response.ClientDtoResponse;
import backend.clientservice.models.entities.TipoDocumento;
import backend.clientservice.security.TestSecurityConfig;
import backend.clientservice.services.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClienteController.class)
@Import(TestSecurityConfig.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClientService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetByDocumentNumber_whenClientFound_returnClient() throws Exception {
        ClientDtoResponse client = new ClientDtoResponse(
                1L,
                "Victor",
                "Orbegozo",
                "DNI",
                "1994-05-04",
                "12345678"
        );

        when(service.getByDocumentNumber("12345678", TipoDocumento.DNI.name())).thenReturn(client);
        // Act
        mockMvc.perform(get("/api/clients/document/{documentNumber}/{documentType}", "12345678", "DNI")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Victor"));

    }

    @Test
    void testAdd_whenRequestIsValid_returnClient() throws Exception {
        ClientDtoRequest dto = new ClientDtoRequest(
                "Victor",
                "Orbegozo",
                "DNI",
                "1994-05-04",
                "12345678"
        );

        ClientDtoResponse response = new ClientDtoResponse(
                1L,
                "Victor",
                "Orbegozo",
                "DNI",
                "1994-05-04",
                "12345678"
        );
        String json = objectMapper.writeValueAsString(dto);

        when(service.add(any(ClientDtoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Victor"));

        verify(service, times(1)).add(any(ClientDtoRequest.class));

    }

    @Test
    void testGetById_whenClientFound_returnClient() throws Exception {
        ClientDtoResponse client = new ClientDtoResponse(
                1L,
                "Victor",
                "Orbegozo",
                "DNI",
                "1994-05-04",
                "12345678"
        );

        when(service.getById(1L)).thenReturn(client);
        // Act
        mockMvc.perform(get("/api/clients/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Victor"));


        verify(service, times(1)).getById(1L);
    }

    @Test
    void testListAll_whenDataNotFound_returnEmpty() throws Exception {
        when(service.listAll(1, 10, "id")).thenReturn(Page.empty());
        // Act
        mockMvc.perform(get("/api/clients")
                        .param("page", "1")
                        .param("size", "10")
                        .param("orderBy", "id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void testListAll() throws Exception {
        ClientDtoResponse cliente1 = new ClientDtoResponse(
                1L,
                "Victor",
                "Orbegozo",
                "DNI",
                "1994-05-04",
                "12345678"
        );

        ClientDtoResponse cliente2 = new ClientDtoResponse(
                2L,
                "Maria",
                "Martinez",
                "DNI",
                "1994-05-04",
                "11111111"
        );

        List<ClientDtoResponse> listClientes = List.of(cliente1, cliente2);
        Pageable pageable = PageRequest.of(0, 10);

        when(service.listAll(1, 10, "id")).thenReturn(new PageImpl<>(listClientes, pageable, listClientes.size()));

        // Act
        mockMvc.perform(get("/api/clients")
                        .param("page", "1")
                        .param("size", "10")
                        .param("orderBy", "id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[1].id").value(2));
    }

}