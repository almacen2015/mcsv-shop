package backend.dto.request;

import java.util.List;

public record SaleDtoRequest(Integer clientId,
                             List<DetailSaleDtoRequest> details) {
}
