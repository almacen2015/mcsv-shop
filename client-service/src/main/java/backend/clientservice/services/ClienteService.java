package backend.clientservice.services;

import backend.clientservice.models.dtos.ClienteRequestDTO;
import backend.clientservice.models.dtos.ClienteResponseDTO;

import java.util.List;

public interface ClienteService {
    ClienteResponseDTO add(ClienteRequestDTO cliente);

    List<ClienteResponseDTO> listAll();

    ClienteResponseDTO getById(Long id);

    ClienteResponseDTO getByDocumentNumber(String documentNumber, String documentType);

}
