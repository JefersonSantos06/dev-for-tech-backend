package com.devfortech.crudservice.rest.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class StudentRequestDTO implements Serializable {
    private static final long serialVersionUID = 2306816499507776610L;

    private Long id;
    private BigDecimal fees;
    private PessoaDTO pessoa;
    private Long classeID;
    private boolean createUser;
    private String newPassword;
    private String confirmNewPassword;
    private String actualPassword;

}
