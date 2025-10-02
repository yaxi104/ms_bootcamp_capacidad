package co.com.reactive.r2dbc.capacitybootcamp.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "CAPACIDAD_BOOTCAMP")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CapacityBootcampEntity {

    @Id
    private Long id;

    @Column("id_capacidad")
    private Long capacityId;

    @Column("id_bootcamp")
    private Long bootcampId;

}