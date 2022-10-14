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
public class DeleteUserRequest implements Serializable {
    private static final long serialVersionUID = -7836239264891823851L;

    private String email;
    private String password;
    private String role;

}
