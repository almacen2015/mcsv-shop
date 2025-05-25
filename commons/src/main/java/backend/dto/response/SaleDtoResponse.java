package backend.dto.response;

import java.util.List;

public record SaleDtoResponse(
        String id,
        String client,
        String date,
        Double total,
        List<DetailSaleDtoResponse> details) {
}
