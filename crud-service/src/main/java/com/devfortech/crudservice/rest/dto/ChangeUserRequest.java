package com.devfortech.crudservice.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeUserRequest implements Serializable {
    private static final long serialVersionUID = 4398354939876113757L;

    private String name;
    private String oldEmail;
    private String newEmail;
    private String oldPassword;
    private String newPassword;

}
