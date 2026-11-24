// Ruta: src/test/java/com/agroalert/agroalertbackend/service/EmailNotificacionServiceTest.java
package com.agroalert.agroalertbackend.service;

import com.agroalert.agroalertbackend.entity.*;
import com.agroalert.agroalertbackend.entity.enums.EstadoAlerta;
import com.agroalert.agroalertbackend.entity.enums.NivelRiesgo;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailNotificacionServiceTest {

    @Mock
    private JavaMailSender mailSender;

    private EmailNotificacionService emailNotificacionService;

    private Alerta alerta;
    private Agricultor agricultor;
    private Parcela parcela;

    @BeforeEach
    void setUp() {
        emailNotificacionService = new EmailNotificacionService(mailSender, "soporte@agroalert.com");

        agricultor = Agricultor.builder()
                .id(1L)
                .nombre("Pedro")
                .apellido("Mamani")
                .email("pedro.mamani@agrotest.pe")
                .dni("12345678")
                .telefono("987654321")
                .build();

        parcela = Parcela.builder()
                .id(10L)
                .nombre("Parcela Santa Rita")
                .agricultor(agricultor)
                .build();

        Riesgo riesgo = Riesgo.builder()
                .id(2L)
                .nombre("Piricularia en arroz")
                .build();

        Medida medida = Medida.builder()
                .id(3L)
                .nombre("Manejo de lámina de agua")
                .build();

        Recomendacion recomendacion = Recomendacion.builder()
                .id(4L)
                .texto("Drenar temporalmente el lote para bajar la humedad relativa")
                .medida(medida)
                .build();
        medida.setRecomendacion(recomendacion);

        Procedimiento procedimiento = Procedimiento.builder()
                .id(5L)
                .titulo("Drenaje preventivo")
                .medida(medida)
                .build();

        PasoProcedimiento paso1 = PasoProcedimiento.builder()
                .id(11L)
                .orden(1)
                .descripcion("Abrir compuertas de desagüe gradualmente")
                .procedimiento(procedimiento)
                .build();
        PasoProcedimiento paso2 = PasoProcedimiento.builder()
                .id(12L)
                .orden(2)
                .descripcion("Mantener sin lámina durante 48 horas")
                .procedimiento(procedimiento)
                .build();

        procedimiento.setPasos(List.of(paso2, paso1)); // desordenado para comprobar ordenamiento
        medida.setProcedimiento(procedimiento);

        alerta = Alerta.builder()
                .id(100L)
                .parcela(parcela)
                .riesgo(riesgo)
                .medida(medida)
                .nivel(NivelRiesgo.ALTO)
                .estado(EstadoAlerta.ACTIVA)
                .generadaEn(LocalDateTime.now())
                .condicionDetectada("Humedad relativa > 85% durante 12 horas consecutivas")
                .build();
    }

    @Test
    void testEnviarAlertaAgricultor_Exitoso() {
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        assertDoesNotThrow(() -> emailNotificacionService.enviarAlertaAgricultor(alerta));

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testEnviarAlertaAgricultor_FalloDeRedNoInterrumpeFlujo() {
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("Fallo en servidor SMTP")).when(mailSender).send(any(MimeMessage.class));

        // Debe capturar la excepción y registrar log sin lanzar excepción al invocador
        assertDoesNotThrow(() -> emailNotificacionService.enviarAlertaAgricultor(alerta));

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testEnviarAlertaAgricultor_SinAgricultorOEmailInvalido() {
        parcela.setAgricultor(null);

        assertDoesNotThrow(() -> emailNotificacionService.enviarAlertaAgricultor(alerta));
        verify(mailSender, never()).send(any(MimeMessage.class));

        // Agricultor sin correo
        agricultor.setEmail("");
        parcela.setAgricultor(agricultor);

        assertDoesNotThrow(() -> emailNotificacionService.enviarAlertaAgricultor(alerta));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void testEnviarAlertaAgricultor_AlertaNulaOPareclaNula() {
        assertDoesNotThrow(() -> emailNotificacionService.enviarAlertaAgricultor(null));
        verify(mailSender, never()).send(any(MimeMessage.class));

        alerta.setParcela(null);
        assertDoesNotThrow(() -> emailNotificacionService.enviarAlertaAgricultor(alerta));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void testEnviarAlertaAgricultor_SinMedidaNiProcedimiento() {
        alerta.setMedida(null);
        alerta.setRiesgo(null);
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        assertDoesNotThrow(() -> emailNotificacionService.enviarAlertaAgricultor(alerta));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}
