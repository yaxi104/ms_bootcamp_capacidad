package co.com.reactive.r2dbc.capacity.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "CAPACIDADES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CapacityEntity {

    @Id
    private Long id;

    @Column("nombre")
    private String name;

    @Column("descripcion")
    private String description;

}