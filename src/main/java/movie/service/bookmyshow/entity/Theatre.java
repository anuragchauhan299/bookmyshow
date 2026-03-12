package movie.service.bookmyshow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "theatres", indexes = {
        @Index(name = "idx_theatre_city", columnList = "city"),
        @Index(name = "idx_theatre_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Theatre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uuid;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column
    private String state;

    @Column
    private String country;

    @Column(name = "postal_code")
    private String postalCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TheatreType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IntegrationType integrationType;

    @Column(name = "integration_endpoint")
    private String integrationEndpoint;

    @Column(name = "integration_api_key")
    private String integrationApiKey;

    @Column(name = "screen_count")
    private Integer screenCount;

    @Column(name = "total_seats")
    private Integer totalSeats;

    @Column(nullable = false)
    private Boolean active;

    @OneToMany(mappedBy = "theatre", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Show> shows = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (uuid == null || uuid.isEmpty()) {
            uuid = java.util.UUID.randomUUID().toString();
        }
        if (active == null) {
            active = true;
        }
        if (integrationType == null) {
            integrationType = IntegrationType.NEW;
        }
    }

    public enum TheatreType {
        MULTIPLEX, SINGLE_SCREEN, INDOOR, OUTDOOR
    }

    public enum IntegrationType {
        NEW, LEGACY_REST, LEGACY_SOAP, LEGACY_FILE
    }
}
