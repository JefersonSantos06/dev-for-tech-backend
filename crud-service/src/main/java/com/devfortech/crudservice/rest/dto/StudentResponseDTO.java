package com.devfortech.crudservice.rest.dto;

import com.devfortech.crudservice.domain.entity.StudentEntity;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.math.BigDecimal;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponseDTO extends RepresentationModel<StudentResponseDTO>  implements Serializable {
    private static final long serialVersionUID = 2306816499507776610L;

    private Long id;
    private BigDecimal fees;
    private PessoaDTO pessoa;
    private Long classeID;
    private boolean createUser;

    public StudentResponseDTO(StudentEntity entity, Long classeID) {
        this.id = entity.getId();
        this.fees = entity.getFees();
        this.pessoa = new PessoaDTO(entity.getPessoa());
        this.classeID = classeID;
        this.createUser = entity.isCreateUser();
    }
}
