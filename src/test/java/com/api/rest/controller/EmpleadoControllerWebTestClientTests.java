package com.api.rest.controller;

import com.api.rest.model.Empleado;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmpleadoControllerWebTestClientTests {

    @Autowired
    private WebTestClient webtestClient;

    @Test
    @Order(1)
    void testGuardarEmpleado(){
        //given
        Empleado empleado = Empleado.builder()
                .id(1l)
                .nombre("Ericson")
                .apellido("Veliz")
                .email("ev01@gmail.com")
                .build();

        //when
        webtestClient.post().uri("http://localhost:8080/api/empleados")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(empleado)
                .exchange() //envia el request

        //then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(empleado.getId())
                .jsonPath("$.nombre").isEqualTo(empleado.getNombre())
                .jsonPath("$.apellido").isEqualTo(empleado.getApellido())
                .jsonPath("$.email").isEqualTo(empleado.getEmail());
    }
    @Test
    @Order(2)
    void testObtenerEmpleadoPorId(){
        webtestClient.get().uri("http://localhost:8080/api/empleados/1").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.nombre").isEqualTo("Ericson")
                .jsonPath("$.apellido").isEqualTo("Veliz")
                .jsonPath("$.email").isEqualTo("ev01@gmail.com");

    }

    @Test
    @Order(3)
    void testListarEmpleados(){
        webtestClient.get().uri("http://localhost:8080/api/empleados").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].nombre").isEqualTo("Ericson")
                .jsonPath("$[0].apellido").isEqualTo("Veliz")
                .jsonPath("$[0].email").isEqualTo("ev01@gmail.com")
                .jsonPath("$").isArray()
                .jsonPath("$").value(hasSize(1));

    }

    @Test
    @Order(4)
    void testObtenerListadoDeEmpleados(){
        webtestClient.get().uri("http://localhost:8080/api/empleados").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Empleado.class)
                .consumeWith(response -> {
                    List<Empleado> empleados = response.getResponseBody();
                    Assertions.assertEquals(1,empleados.size());
                    Assertions.assertNotNull(empleados);
                });
    }

    @Test
    @Order(5)
    void testActualizarEmpleado(){
        Empleado empleadoActualizado = Empleado.builder()
                .nombre("Juan")
                .apellido("Valle")
                .email("jv01@gmail.com")
                .build();

        webtestClient.put().uri("http://localhost:8080/api/empleados/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(empleadoActualizado)
                .exchange()//envia el request

        //then
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @Order(6)
    void testEliminarEmpleado(){
        webtestClient.get().uri("http://localhost:8080/api/empleados").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Empleado.class)
                .hasSize(1);

        webtestClient.delete().uri("http://localhost:8080/api/empleados/1")
                .exchange()
                .expectStatus().isOk();

        webtestClient.get().uri("http://localhost:8080/api/empleados").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Empleado.class)
                .hasSize(0);

        webtestClient.get().uri("http://localhost:8080/api/empleados/1").exchange()
                .expectStatus().is4xxClientError();
    }
}
