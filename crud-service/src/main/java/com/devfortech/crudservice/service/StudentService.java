package com.devfortech.crudservice.service;

import com.devfortech.crudservice.domain.entity.AddresEntity;
import com.devfortech.crudservice.domain.entity.PessoaEntity;
import com.devfortech.crudservice.domain.entity.StudentEntity;
import com.devfortech.crudservice.domain.repository.AddresRepository;
import com.devfortech.crudservice.domain.repository.ClassRepository;
import com.devfortech.crudservice.domain.repository.PessoaRepository;
import com.devfortech.crudservice.domain.repository.StudentRepository;
import com.devfortech.crudservice.exception.DatabaseException;
import com.devfortech.crudservice.exception.ResourceExistsException;
import com.devfortech.crudservice.exception.ResourceNotFoundException;
import com.devfortech.crudservice.exception.UnauthorizedException;
import com.devfortech.crudservice.rest.dto.ChangeUserRequest;
import com.devfortech.crudservice.rest.dto.DeleteUserRequest;
import com.devfortech.crudservice.rest.dto.StudentRequestDTO;
import com.devfortech.crudservice.rest.dto.StudentResponseDTO;
import com.devfortech.crudservice.rest.message.AuthSendMessage;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Objects;

@Service
@AllArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final PessoaRepository pessoaRepository;
    private final ClassRepository classRepository;
    private final AddresRepository addresRepository;
    private final AuthSendMessage authSendMessage;
    private final UserService userService;

    @Transactional
    public StudentResponseDTO create(StudentRequestDTO dto) {
        verificaSeJaExisteEmail(dto.getPessoa().getEmailAddress());

        StudentEntity entity = new StudentEntity(dto);

        AddresEntity addres = addresRepository.findByPostalCode(dto.getPessoa().getAddres().getPostalCode());

        entity.getPessoa().setAddress(addresRepository.save(
                addres != null ? addres : new AddresEntity(dto.getPessoa().getAddres())));

        entity.setPessoa(pessoaRepository.save(entity.getPessoa()));

        if (dto.getClasseID() != null)
            entity.setClasse(classRepository.findById(dto.getClasseID())
                    .orElseThrow(() -> new ResourceNotFoundException("Classe", "Id", dto.getClasseID())));

        StudentResponseDTO saveEntity = convertToResponseDTO(studentRepository.save(entity));

        if (dto.isCreateUser()) {
            if (dto.getNewPassword().equals(dto.getConfirmNewPassword()) && dto.getConfirmNewPassword() != null && !dto.getNewPassword().equals("")) {
                dto.setNewPassword(dto.getConfirmNewPassword());
            }

            authSendMessage.sendMessageCreateUser(dto);
        }

        return saveEntity;
    }

    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> findAll(Pageable pageable) {
        Page<StudentEntity> entity = studentRepository.findAll(pageable);
        return entity.map(this::convertToResponseDTO);
    }

    @Transactional(readOnly = true)
    public StudentResponseDTO findByID(Long id) {
        StudentEntity entity = checkById(id);
        return convertToResponseDTO(entity);
    }

    @Transactional
    public StudentResponseDTO update(Long id, StudentRequestDTO dto) {
        HashMap<String, String> dadosUsuario = userService.dadosUsuario();

        StudentEntity entity = checkById(id);

        if (!dadosUsuario.get("email").equals(entity.getPessoa().getEmailAddress()) && !dadosUsuario.get("role").equals("ADMIN")) {
            throw new UnauthorizedException("Você não tem permição para alterar este estudante");
        } else if ((Objects.equals(dto.getActualPassword(), "") || dto.getActualPassword() == null) && !dadosUsuario.get("role").equals("ADMIN")) {
            throw new UnauthorizedException("Informe sua senha para realizar as alterações");
        }


        PessoaEntity pessoaEntity = entity.getPessoa();
        AddresEntity addres = entity.getPessoa().getAddress();
        dto.getPessoa().setId(pessoaEntity.getId());
        dto.getPessoa().getAddres().setId(addres.getId());
        dto.setId(entity.getId());

        String oldEmail = entity.getPessoa().getEmailAddress();
        String oldName = entity.getPessoa().getName();
        boolean usuarioJaCriado = entity.isCreateUser();

        if (!oldEmail.equals(dto.getPessoa().getEmailAddress())) {
            verificaSeJaExisteEmail(dto.getPessoa().getEmailAddress());
        }

        BeanUtils.copyProperties(dto.getPessoa(), pessoaEntity);
        BeanUtils.copyProperties(dto.getPessoa().getAddres(), addres);
        BeanUtils.copyProperties(dto, entity);

        pessoaRepository.save(pessoaEntity);
        addresRepository.save(addres);
        studentRepository.save(entity);

        if (usuarioJaCriado && dto.isCreateUser()) {
            ChangeUserRequest signUpRequest = ChangeUserRequest.builder()
                    .name(entity.getPessoa().getName())
                    .newEmail(entity.getPessoa().getEmailAddress())
                    .oldEmail(oldEmail)
                    .newPassword(dto.getNewPassword())
                    .oldPassword(dto.getActualPassword())
                    .build();

            boolean mudouSenha = !Objects.equals(dto.getNewPassword(), dto.getActualPassword())
                    && !Objects.equals(dto.getNewPassword(), "")
                    && dto.getNewPassword() != null;

            boolean mudouEmail = !Objects.equals(dto.getPessoa().getEmailAddress(), oldEmail)
                    && !Objects.equals(dto.getPessoa().getEmailAddress(), "")
                    && dto.getPessoa().getEmailAddress() != null;

            boolean mudouNome = !Objects.equals(dto.getPessoa().getName(), oldName)
                    && !Objects.equals(dto.getPessoa().getName(), "")
                    && dto.getPessoa().getName() != null;

            if (mudouSenha || mudouEmail || mudouNome) {
                if (mudouSenha) {
                    if (dto.getNewPassword().equals(dto.getConfirmNewPassword()))
                        throw new BadCredentialsException("Senha de confirmação esta diferente da nova senha.");
                }
                authSendMessage.sendMessageUpdateUser(signUpRequest);
            }
        } else if (!usuarioJaCriado && dto.isCreateUser()) {
            if (Objects.equals(dto.getNewPassword(), dto.getConfirmNewPassword())
                    && dto.getConfirmNewPassword() != null
                    && !Objects.equals(dto.getNewPassword(), "")) {
                dto.setNewPassword(dto.getConfirmNewPassword());
            }
            authSendMessage.sendMessageCreateUser(dto);
        } else if (usuarioJaCriado && !dto.isCreateUser()) {
            DeleteUserRequest delReq = new DeleteUserRequest();
            delReq.setEmail(dto.getPessoa().getEmailAddress());
            delReq.setPassword(dto.getActualPassword());
            delReq.setRole(dadosUsuario.get("role"));

            authSendMessage.sendMessageDeleteUser(delReq);
        }


        return convertToResponseDTO(entity);
    }

    @Transactional
    public void deleteByID(Long id) {
        try {
            StudentEntity entity = checkById(id);
            studentRepository.delete(entity);
            pessoaRepository.deleteById(id);
            addresRepository.deleteById(id);

            if (entity.isCreateUser()) {

                DeleteUserRequest delReq = new DeleteUserRequest();
                delReq.setEmail(entity.getPessoa().getEmailAddress());
                delReq.setRole(userService.dadosUsuario().get("role"));

                authSendMessage.sendMessageDeleteUser(delReq);
            }
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }
    }

    private void verificaSeJaExisteEmail(String email) {
        if (pessoaRepository.existsByEmailAddress(email)) {
            throw new ResourceExistsException("Ja existe alguem com este endereço de email");
        }
    }

    private StudentEntity checkById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno nao encontrado com o ID: " + id));
    }

    private StudentResponseDTO convertToResponseDTO(StudentEntity entity) {

        if (entity.getClasse() == null)
            return new StudentResponseDTO(entity, null);
        else
            return new StudentResponseDTO(entity, entity.getClasse().getId());
    }
}
