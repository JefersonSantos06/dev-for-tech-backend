package com.devfortech.crudservice.rest.controller;

import com.devfortech.crudservice.rest.dto.StudentRequestDTO;
import com.devfortech.crudservice.rest.dto.StudentResponseDTO;
import com.devfortech.crudservice.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/aluno")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService service;

    private final PagedResourcesAssembler<StudentResponseDTO> assembler;

    @PostMapping
    public StudentResponseDTO create(@Valid @RequestBody StudentRequestDTO dto) {
        StudentResponseDTO createDto = service.create(dto);
        createDto.add(linkById(createDto.getId()));
        return createDto;
    }

    @GetMapping(value = "{id}")
    public StudentResponseDTO findById(@PathVariable Long id){
        StudentResponseDTO dto = service.findByID(id);
        dto.add(linkById(id));
        return dto;
    }

    @GetMapping
    public ResponseEntity<?> findAll(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<StudentResponseDTO> dto = service.findAll(pageable);
        dto.stream().forEach(v -> v.add(linkById(v.getId())));
        PagedModel<EntityModel<StudentResponseDTO>> pagedModel = assembler.toModel(dto);
        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody StudentRequestDTO dto){
        var response = service.update(id,dto);
        response.add(linkById(response.getId()));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        service.deleteByID(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private Link linkById(Long id){
        return linkTo(methodOn(StudentController.class).findById(id)).withSelfRel();
    }

}
