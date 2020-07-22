package de.eklaesener.inventorizer.locations;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(
    name = "building",
    uniqueConstraints = @UniqueConstraint(name = "building_uc_name", columnNames = "name")
)
@Check(constraints = "street_number > 0 AND zip_code > 0")
@Getter
@Setter
@ToString
public class Building {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "building_sequence"
    )
    @GenericGenerator(
        name = "building_sequence",
        strategy = "sequence",
        parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "building_sequence"),
            @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
            @org.hibernate.annotations.Parameter(name = "increment_size", value = "3"),
            @org.hibernate.annotations.Parameter(name = "optimizer", value = "pooled-lo")
        }
    )
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "street_number", nullable = false)
    private int streetNumber;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "zip_code", nullable = false)
    private int zipCode;

    @Column(name = "country", nullable = false)
    private String country;
}
