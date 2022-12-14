package com.devfortech.crudservice.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequest implements Serializable {
    private static final long serialVersionUID = -4436153936699398961L;

    private String email;
    private String password;
    private String nome;
    private String role;

}
