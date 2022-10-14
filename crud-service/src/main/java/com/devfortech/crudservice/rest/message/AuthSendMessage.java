package com.devfortech.crudservice.rest.message;

import com.devfortech.crudservice.rest.dto.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthSendMessage {

    @Value("${spring.rabbitmq.auth.exchange}")
    private String exchange;

    @Value("${spring.rabbitmq.auth.routingkey.main}")
    private String mainRK;

    @Value("${spring.rabbitmq.auth.routingkey.update}")
    private String updateRK;

    @Value("${spring.rabbitmq.auth.routingkey.delete}")
    private String deleteRK;


    public final RabbitTemplate rabbitTemplate;

    @Autowired
    public AuthSendMessage(RabbitTemplate template){
        this.rabbitTemplate = template;
    }

    @RabbitListener
    public void sendMessageCreateUser(StudentRequestDTO studentDTO){
        rabbitTemplate.convertAndSend(exchange, mainRK,
                SignUpRequest.builder()
                        .email(studentDTO.getPessoa().getEmailAddress())
                        .password(studentDTO.getNewPassword())
                        .role("STUDENT")
                        .nome(studentDTO.getPessoa().getName())
                .build());
    }

    @RabbitListener
    public void sendMessageCreateUser(TeacherDTO teacherDto){
        rabbitTemplate.convertAndSend(exchange, mainRK,
                SignUpRequest.builder()
                        .email(teacherDto.getPessoa().getEmailAddress())
                        .password(teacherDto.getPassword())
                        .role("TEACHER")
                        .nome(teacherDto.getPessoa().getName())
                .build());
    }

    @RabbitListener
    public void sendMessageDeleteUser(DeleteUserRequest req){
        rabbitTemplate.convertAndSend(exchange, deleteRK, req);
    }

    @RabbitListener
    public void sendMessageUpdateUser(ChangeUserRequest req){
        rabbitTemplate.convertAndSend(exchange, updateRK, req);
    }



}
