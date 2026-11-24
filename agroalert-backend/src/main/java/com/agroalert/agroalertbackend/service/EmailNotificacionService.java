// Ruta: src/main/java/com/agroalert/agroalertbackend/service/EmailNotificacionService.java
package com.agroalert.agroalertbackend.service;

import com.agroalert.agroalertbackend.entity.Agricultor;
import com.agroalert.agroalertbackend.entity.Alerta;
import com.agroalert.agroalertbackend.entity.PasoProcedimiento;
import com.agroalert.agroalertbackend.entity.enums.NivelRiesgo;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class EmailNotificacionService {

    private final JavaMailSender mailSender;
    private final String remitente;

    public EmailNotificacionService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String remitente) {
        this.mailSender = mailSender;
        this.remitente = remitente;
    }

    @Async
    public void enviarAlertaAgricultor(Alerta alerta) {
        if (alerta == null) {
            log.warn("No se puede enviar correo: la alerta proporcionada es nula.");
            return;
        }

        if (alerta.getParcela() == null) {
            log.warn("No se puede enviar correo: la alerta ID {} no tiene parcela asociada.", alerta.getId());
            return;
        }

        Agricultor agricultor = alerta.getParcela().getAgricultor();
        if (agricultor == null) {
            log.warn("No se puede enviar correo: la parcela ID {} no tiene un agricultor asignado.", alerta.getParcela().getId());
            return;
        }

        String destinatarioEmail = agricultor.getEmail();
        if (destinatarioEmail == null || destinatarioEmail.isBlank()) {
            log.warn("No se puede enviar correo: el agricultor ID {} no tiene una dirección de correo válida.", agricultor.getId());
            return;
        }

        String nombreAgricultor = agricultor.getNombre() != null ? agricultor.getNombre() : "";
        String apellidoAgricultor = agricultor.getApellido() != null ? agricultor.getApellido() : "";
        String nombreCompleto = (nombreAgricultor + " " + apellidoAgricultor).trim();
        if (nombreCompleto.isEmpty()) {
            nombreCompleto = "Agricultor";
        }

        NivelRiesgo nivelRiesgo = alerta.getNivel() != null ? alerta.getNivel() : NivelRiesgo.MODERADO;
        String nivelStr = nivelRiesgo.name();
        String nombreParcela = alerta.getParcela().getNombre() != null ? alerta.getParcela().getNombre() : "Sin nombre";

        String riesgoNombre = (alerta.getRiesgo() != null && alerta.getRiesgo().getNombre() != null)
                ? alerta.getRiesgo().getNombre()
                : "No especificado";

        String condicionDetectada = alerta.getCondicionDetectada() != null
                ? alerta.getCondicionDetectada()
                : "No especificada";

        String recomendacionTexto = "Consulte con el especialista técnico asignado para más recomendaciones.";
        if (alerta.getMedida() != null
                && alerta.getMedida().getRecomendacion() != null
                && alerta.getMedida().getRecomendacion().getTexto() != null
                && !alerta.getMedida().getRecomendacion().getTexto().isBlank()) {
            recomendacionTexto = alerta.getMedida().getRecomendacion().getTexto();
        }

        List<String> pasos = List.of();
        if (alerta.getMedida() != null
                && alerta.getMedida().getProcedimiento() != null
                && alerta.getMedida().getProcedimiento().getPasos() != null) {
            pasos = alerta.getMedida().getProcedimiento().getPasos().stream()
                    .sorted(Comparator.comparingInt(p -> p.getOrden() != null ? p.getOrden() : 0))
                    .map(PasoProcedimiento::getDescripcion)
                    .filter(desc -> desc != null && !desc.isBlank())
                    .toList();
        }

        String asunto = String.format("[AgroAlert] Alerta %s en parcela %s", nivelStr, nombreParcela);
        String contenidoHtml = construirCuerpoHtml(nombreCompleto, nombreParcela, nivelRiesgo, riesgoNombre, condicionDetectada, recomendacionTexto, pasos);
        String contenidoTextoPlano = construirCuerpoTextoPlano(nombreCompleto, nombreParcela, nivelRiesgo, riesgoNombre, condicionDetectada, recomendacionTexto, pasos);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            if (remitente != null && !remitente.isBlank()) {
                helper.setFrom(remitente);
            }
            helper.setTo(destinatarioEmail);
            helper.setSubject(asunto);
            helper.setText(contenidoTextoPlano, contenidoHtml);

            mailSender.send(mimeMessage);
            log.info("Notificación de alerta ID {} enviada correctamente al agricultor {} ({})",
                    alerta.getId(), nombreCompleto, destinatarioEmail);
        } catch (Exception ex) {
            log.error("Error al enviar notificación por correo al agricultor {} ({}) para la alerta ID {}: {}",
                    nombreCompleto, destinatarioEmail, alerta.getId(), ex.getMessage(), ex);
        }
    }

    private String construirCuerpoTextoPlano(
            String nombreCompleto,
            String nombreParcela,
            NivelRiesgo nivelRiesgo,
            String riesgoNombre,
            String condicionDetectada,
            String recomendacionTexto,
            List<String> pasos) {

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Estimado(a) %s,\n\n", nombreCompleto));
        sb.append("Se ha generado una nueva alerta climática en el sistema AgroAlert:\n\n");
        sb.append(String.format("- Nivel de Alerta: %s\n", nivelRiesgo.name()));
        sb.append(String.format("- Parcela Afectada: %s\n", nombreParcela));
        sb.append(String.format("- Riesgo Identificado: %s\n", riesgoNombre));
        sb.append(String.format("- Condición Detectada: %s\n\n", condicionDetectada));
        sb.append("RECOMENDACIÓN PRINCIPAL:\n");
        sb.append(recomendacionTexto).append("\n\n");

        if (!pasos.isEmpty()) {
            sb.append("PASOS DEL PROCEDIMIENTO:\n");
            for (int i = 0; i < pasos.size(); i++) {
                sb.append(String.format("%d. %s\n", (i + 1), pasos.get(i)));
            }
            sb.append("\n");
        }

        sb.append("Por favor, revise oportunamente sus parcelas y aplique las medidas preventivas para proteger su cultivo.\n\n");
        sb.append("---\n");
        sb.append("Sistema de Alerta Temprana AgroAlert - Región Arequipa, Perú\n");

        return sb.toString();
    }

    private String construirCuerpoHtml(
            String nombreCompleto,
            String nombreParcela,
            NivelRiesgo nivelRiesgo,
            String riesgoNombre,
            String condicionDetectada,
            String recomendacionTexto,
            List<String> pasos) {

        String colorNivel = switch (nivelRiesgo) {
            case CRITICO -> "#d32f2f";
            case ALTO -> "#e65100";
            case MODERADO -> "#f57f17";
            case BAJO -> "#2e7d32";
        };

        StringBuilder pasosHtml = new StringBuilder();
        if (pasos.isEmpty()) {
            pasosHtml.append("<p style=\"margin: 0; font-size: 13px; color: #666666; font-style: italic;\">No se registraron pasos adicionales para este procedimiento.</p>");
        } else {
            pasosHtml.append("<ol style=\"margin: 0; padding-left: 20px; line-height: 1.6; font-size: 14px; color: #333333;\">");
            for (String paso : pasos) {
                pasosHtml.append(String.format("<li style=\"margin-bottom: 6px;\">%s</li>", escaparHtml(paso)));
            }
            pasosHtml.append("</ol>");
        }

        return "<!DOCTYPE html>"
                + "<html lang=\"es\">"
                + "<head><meta charset=\"UTF-8\"><title>Alerta AgroAlert</title></head>"
                + "<body style=\"font-family: Arial, sans-serif; background-color: #f4f6f9; margin: 0; padding: 20px; color: #333333;\">"
                + "<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1); border: 1px solid #e0e0e0;\">"
                + "  <div style=\"background-color: #2e7d32; color: #ffffff; padding: 20px 25px; text-align: center;\">"
                + "    <h1 style=\"margin: 0; font-size: 24px; letter-spacing: 1px;\">AgroAlert</h1>"
                + "    <p style=\"margin: 5px 0 0 0; font-size: 13px; opacity: 0.9;\">Sistema de Alerta Temprana Agrícola — Arequipa</p>"
                + "  </div>"
                + "  <div style=\"padding: 25px;\">"
                + "    <p style=\"font-size: 15px; margin-top: 0;\">Estimado(a) <strong>" + escaparHtml(nombreCompleto) + "</strong>,</p>"
                + "    <p style=\"font-size: 14px; line-height: 1.5; color: #555555;\">Se ha detectado una condición meteorológica de riesgo para su parcela. A continuación se presentan los detalles de la alerta generada:</p>"
                + "    <div style=\"background-color: #fafafa; border-left: 5px solid " + colorNivel + "; padding: 15px 20px; border-radius: 4px; margin-bottom: 20px;\">"
                + "      <p style=\"margin: 6px 0; font-size: 14px;\"><strong>Nivel de Alerta:</strong> <span style=\"background-color: " + colorNivel + "; color: #ffffff; padding: 3px 8px; border-radius: 3px; font-weight: bold; font-size: 12px;\">" + nivelRiesgo.name() + "</span></p>"
                + "      <p style=\"margin: 6px 0; font-size: 14px;\"><strong>Parcela Afectada:</strong> " + escaparHtml(nombreParcela) + "</p>"
                + "      <p style=\"margin: 6px 0; font-size: 14px;\"><strong>Riesgo Identificado:</strong> " + escaparHtml(riesgoNombre) + "</p>"
                + "      <p style=\"margin: 6px 0; font-size: 14px;\"><strong>Condición Detectada:</strong> " + escaparHtml(condicionDetectada) + "</p>"
                + "    </div>"
                + "    <div style=\"background-color: #e8f5e9; border: 1px solid #c8e6c9; border-radius: 4px; padding: 15px 20px; margin-bottom: 20px;\">"
                + "      <h3 style=\"color: #2e7d32; margin: 0 0 8px 0; font-size: 15px;\">Recomendación Principal</h3>"
                + "      <p style=\"margin: 0; font-size: 14px; line-height: 1.5; color: #1b5e20;\">" + escaparHtml(recomendacionTexto) + "</p>"
                + "    </div>"
                + "    <div style=\"background-color: #ffffff; border: 1px solid #e0e0e0; border-radius: 4px; padding: 15px 20px; margin-bottom: 20px;\">"
                + "      <h3 style=\"color: #333333; margin: 0 0 10px 0; font-size: 15px;\">Pasos del Procedimiento Preventivo</h3>"
                + "      " + pasosHtml
                + "    </div>"
                + "    <p style=\"font-size: 13px; color: #666666; line-height: 1.5;\">Por favor, revise oportunamente sus parcelas y aplique las medidas preventivas indicadas para salvaguardar su cultivo.</p>"
                + "  </div>"
                + "  <div style=\"background-color: #f5f5f5; padding: 12px 25px; text-align: center; font-size: 12px; color: #777777; border-top: 1px solid #e0e0e0;\">"
                + "    <p style=\"margin: 0;\">Este es un mensaje automático emitido por <strong>AgroAlert</strong>.</p>"
                + "  </div>"
                + "</div>"
                + "</body></html>";
    }

    private String escaparHtml(String texto) {
        if (texto == null) return "";
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
