package movie.service.bookmyshow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import movie.service.bookmyshow.entity.Theatre;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TheatreDto {
    private String name;
    private String address;
    private String city;
    private String state;
    private String country;
    private Theatre.TheatreType type;
    private Theatre.IntegrationType integrationType;
    private String integrationEndpoint;
    private Integer screenCount;
    private Integer totalSeats;
}
