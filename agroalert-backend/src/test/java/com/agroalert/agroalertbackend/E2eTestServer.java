// Ruta: src/test/java/com/agroalert/agroalertbackend/E2eTestServer.java
package com.agroalert.agroalertbackend;
import org.springframework.boot.SpringApplication;
import com.agroalert.agroalertbackend.dto.*;
import com.agroalert.agroalertbackend.service.*;
import com.agroalert.agroalertbackend.entity.enums.*;
/** Servidor exclusivamente de pruebas. Datos de ejemplo, base H2 aislada en memoria. */
public class E2eTestServer {
 public static void main(String[] args) {
  var app=new SpringApplication(AgroalertBackendApplication.class); app.setAdditionalProfiles("test");
  var ctx=app.run("--server.port=18080","--agroalert.cors.allowed-origins=http://127.0.0.1:5174,http://localhost:5174","--spring.devtools.restart.enabled=false");
  var users=ctx.getBean(UsuarioService.class);
  for(var rol:RolNombre.values()) users.crearUsuario(CrearRegistroUsuarioDto.builder().nombre("Dato de ejemplo").apellido(rol.name()).email(rol.name().toLowerCase()+"@example.test").password("Prueba-local-123").rol(rol).dni("00000000").telefono("900000000").codigoProfesional("EJEMPLO").especialidad("Dato de ejemplo").build());
  var c=ctx.getBean(ComunidadService.class).crearComunidad(ComunidadDto.builder().nombre("Comunidad de ejemplo").distrito("Ejemplo").provincia("Ejemplo").latitud(-16.6).longitud(-72.7).build());
  Long farmer=users.listarUsuarios().stream().filter(u->u.getRoles().contains("AGRICULTOR")).findFirst().orElseThrow().getId();
  ctx.getBean(ParcelaService.class).registrarParcela(ParcelaRequestDto.builder().nombre("Parcela de ejemplo").ubicacion("Ubicación de ejemplo").areaHectareas(1d).fechaSiembra(java.time.LocalDate.now()).comunidadId(c.getId()).agricultorId(farmer).build());
  System.out.println("E2E_READY puerto 18080; datos de ejemplo en H2, sin cambios en PostgreSQL");
 }
}
