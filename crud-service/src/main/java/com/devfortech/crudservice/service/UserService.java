package com.devfortech.crudservice.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    public HashMap<String, String> dadosUsuario(){
        HashMap<String, String> dados = new HashMap<>();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        dados.put("email", auth.getName());
        dados.put("role", auth.getAuthorities()
                .stream().collect(Collectors.toList()).get(0).toString());

        return dados;
    }

}
