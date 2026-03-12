package movie.service.bookmyshow.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cities", indexes = {
    @Index(name = "idx_city_name", columnList = "name"),
    @Index(name = "idx_city_country", columnList = "country")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uuid;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String country;

    @Column(name = "country_code", length = 3)
    private String countryCode;

    @Column(name = "timezone", nullable = false)
    private String timezone;

    @Column(name = "currency_code", length = 3)
    private String currencyCode;

    @Column(name = "currency_symbol")
    private String currencySymbol;

    @Column(name = "locale", nullable = false)
    private String locale;

    @Column(name = "default_language")
    private String defaultLanguage;

    @ElementCollection
    @CollectionTable(name = "city_supported_locales", joinColumns = @JoinColumn(name = "city_id"))
    @Column(name = "locale")
    @Builder.Default
    private Set<String> supportedLocales = new HashSet<>();

    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "display_order")
    private Integer displayOrder;

    @PrePersist
    public void prePersist() {
        if (uuid == null || uuid.isEmpty()) {
            uuid = java.util.UUID.randomUUID().toString();
        }
        if (active == null) {
            active = true;
        }
        if (displayOrder == null) {
            displayOrder = 0;
        }
    }
}
