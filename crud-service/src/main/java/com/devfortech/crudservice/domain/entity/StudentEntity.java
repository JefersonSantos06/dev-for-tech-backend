package com.devfortech.crudservice.domain.entity;

import com.devfortech.crudservice.rest.dto.StudentRequestDTO;
import com.devfortech.crudservice.rest.dto.StudentResponseDTO;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_student")
@EqualsAndHashCode
public class StudentEntity implements Serializable {
    private static final long serialVersionUID = 5852269417245222814L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(nullable = false)
    private BigDecimal fees;

    @ManyToOne
    private ClassEntity classe;

    @NotNull
    @OneToOne
    private PessoaEntity pessoa;

    private boolean createUser;

    public StudentEntity(StudentResponseDTO dto) {
        this.id = dto.getId();
        this.fees = dto.getFees();
        this.pessoa = new PessoaEntity(dto.getPessoa());
        this.createUser = dto.isCreateUser();
    }

    public StudentEntity(StudentRequestDTO dto) {
        this.id = dto.getId();
        this.fees = dto.getFees();
        this.pessoa = new PessoaEntity(dto.getPessoa());
        this.createUser = dto.isCreateUser();
    }
}
