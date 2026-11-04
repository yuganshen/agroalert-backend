// Ruta: src/main/java/com/agroalert/agroalertbackend/service/ConocimientoService.java
package com.agroalert.agroalertbackend.service;
import com.agroalert.agroalertbackend.dto.ContenidoDto;
import com.agroalert.agroalertbackend.entity.*;
import com.agroalert.agroalertbackend.entity.enums.*;
import com.agroalert.agroalertbackend.repository.*;
import com.agroalert.agroalertbackend.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service
@RequiredArgsConstructor
@Transactional
public class ConocimientoService {
    private final EtapaFenologicaRepository etapaFenologicaRepository;
    private final RiesgoRepository riesgoRepository;
    private final ReglaDecisionRepository reglaDecisionRepository;
    private final MedidaRepository medidaRepository;
    private final RecomendacionRepository recomendacionRepository;
    private final ProcedimientoRepository procedimientoRepository;
    private final PasoProcedimientoRepository pasosRepository;
    private void requerido(Object v, String campo) {
        if(v == null || v instanceof String s && s.isBlank()) throw new ValidationException(campo + " es obligatorio");
    }
    private void validar(String tipo, ContenidoDto d) {
        switch(tipo) {
            case "etapas" -> {
                requerido(d.getNombre(), "Nombre"); requerido(d.getFuenteTecnica(), "Fuente técnica");
                requerido(d.getDiasDesdeMin(), "Días mínimos"); requerido(d.getDiasDesdeMax(), "Días máximos");
                if(d.getDiasDesdeMin()<0 || d.getDiasDesdeMax()<d.getDiasDesdeMin()) throw new ValidationException("Rango de días inválido");
                if(etapaFenologicaRepository.findAll().stream().anyMatch(e -> !Objects.equals(e.getId(),d.getId()) && !Boolean.FALSE.equals(e.getActivo()) && e.getDiasDesdeMin()!=null && e.getDiasDesdeMax()!=null && d.getDiasDesdeMin()<=e.getDiasDesdeMax() && d.getDiasDesdeMax()>=e.getDiasDesdeMin())) throw new ValidationException("El rango se superpone con otra etapa activa");
            }
            case "riesgos" -> { requerido(d.getNombre(), "Nombre"); requerido(d.getVariablesAsociadas(), "Variables asociadas"); }
            case "reglas" -> {
                requerido(d.getFuenteTecnica(), "Fuente técnica"); requerido(d.getUmbral(), "Umbral"); requerido(d.getNivelResultante(), "Nivel");
                requerido(d.getVariableMeteorologica(), "Variable meteorológica"); requerido(d.getOperador(), "Operador");
                if(!List.of("temperatura","precipitacion","humedadRelativa","velocidadViento").contains(d.getVariableMeteorologica())) throw new ValidationException("Variable meteorológica inválida");
                if(!List.of(">","<",">=","<=","==","!=").contains(d.getOperador())) throw new ValidationException("Operador inválido");
            }
            case "medidas" -> { requerido(d.getNombre(), "Nombre"); requerido(d.getDescripcion(), "Descripción"); }
            case "recomendaciones" -> requerido(d.getTexto(), "Recomendación");
            case "procedimientos" -> {
                requerido(d.getTitulo(), "Título"); requerido(d.getMomentoAplicacion(), "Momento de aplicación");
                if(d.getPasos()==null || d.getPasos().isEmpty() || d.getPasos().stream().anyMatch(s -> s==null || s.isBlank())) throw new ValidationException("Agregue al menos un paso y complete todos los pasos");
            }
            default -> throw new ResourceNotFoundException("Tipo de contenido desconocido");
        }
    }
    public List<ContenidoDto> listar(String tipo) {
        return switch(tipo) {
            case "etapas" -> etapaFenologicaRepository.findAll().stream().map(this::mapear).toList();
            case "riesgos" -> riesgoRepository.findAll().stream().map(this::mapear).toList();
            case "reglas" -> reglaDecisionRepository.findAll().stream().map(this::mapear).toList();
            case "medidas" -> medidaRepository.findAll().stream().map(this::mapear).toList();
            case "recomendaciones" -> recomendacionRepository.findAll().stream().map(this::mapear).toList();
            case "procedimientos" -> procedimientoRepository.findAll().stream().map(this::mapear).toList();
            default -> throw new ResourceNotFoundException("Tipo de contenido desconocido");
        };
    }
    public ContenidoDto guardar(String tipo, Long id, ContenidoDto d) {
        d.setId(id); validar(tipo,d);
        return switch(tipo) {
            case "etapas" -> {
                EtapaFenologica e = id==null ? new EtapaFenologica() : etapaFenologicaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contenido no encontrado"));
                e.setNombre(d.getNombre());
                e.setDescripcion(d.getDescripcion());
                e.setDiasDesdeMin(d.getDiasDesdeMin());
                e.setDiasDesdeMax(d.getDiasDesdeMax());
                e.setFuenteTecnica(d.getFuenteTecnica());
                e.setEstado(EstadoValidacion.PENDIENTE); e.setComentarioValidacion(null); if(id==null) e.setActivo(true);
                etapaFenologicaRepository.saveAndFlush(e);
                yield mapear(e);
            }
            case "riesgos" -> {
                Riesgo e = id==null ? new Riesgo() : riesgoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contenido no encontrado"));
                e.setNombre(d.getNombre());
                e.setDescripcion(d.getDescripcion());
                e.setVariablesAsociadas(d.getVariablesAsociadas());
                e.setEstado(EstadoValidacion.PENDIENTE); e.setComentarioValidacion(null); if(id==null) e.setActivo(true);
                if(id==null) e.setCreadoEn(java.time.LocalDateTime.now());
                riesgoRepository.saveAndFlush(e);
                yield mapear(e);
            }
            case "reglas" -> {
                ReglaDecision e = id==null ? new ReglaDecision() : reglaDecisionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contenido no encontrado"));
                e.setVariableMeteorologica(d.getVariableMeteorologica());
                e.setOperador(d.getOperador());
                e.setUmbral(d.getUmbral());
                e.setFuenteTecnica(d.getFuenteTecnica());
                e.setNivelResultante(d.getNivelResultante());
                requerido(d.getRiesgoId(), "riesgo");
                e.setRiesgo(riesgoRepository.findById(d.getRiesgoId()).orElseThrow(() -> new ResourceNotFoundException("riesgo no encontrado")));
                requerido(d.getEtapaFenologicaId(), "etapaFenologica");
                e.setEtapaFenologica(etapaFenologicaRepository.findById(d.getEtapaFenologicaId()).orElseThrow(() -> new ResourceNotFoundException("etapaFenologica no encontrado")));
                e.setEstado(EstadoValidacion.PENDIENTE); e.setComentarioValidacion(null); if(id==null) e.setActivo(true);
                reglaDecisionRepository.saveAndFlush(e);
                yield mapear(e);
            }
            case "medidas" -> {
                Medida e = id==null ? new Medida() : medidaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contenido no encontrado"));
                e.setNombre(d.getNombre());
                e.setDescripcion(d.getDescripcion());
                requerido(d.getRiesgoId(), "riesgo");
                e.setRiesgo(riesgoRepository.findById(d.getRiesgoId()).orElseThrow(() -> new ResourceNotFoundException("riesgo no encontrado")));
                e.setEstado(EstadoValidacion.PENDIENTE); e.setComentarioValidacion(null); if(id==null) e.setActivo(true);
                medidaRepository.saveAndFlush(e);
                yield mapear(e);
            }
            case "recomendaciones" -> {
                Recomendacion e = id==null ? new Recomendacion() : recomendacionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contenido no encontrado"));
                e.setTexto(d.getTexto());
                requerido(d.getMedidaId(), "medida");
                e.setMedida(medidaRepository.findById(d.getMedidaId()).orElseThrow(() -> new ResourceNotFoundException("medida no encontrado")));
                recomendacionRepository.findByMedidaId(d.getMedidaId()).ifPresent(actual -> { if(!Objects.equals(actual.getId(),id)) throw new ValidationException("Esta medida ya tiene recomendaciones; edite el registro existente"); });
                e.setEstado(EstadoValidacion.PENDIENTE); e.setComentarioValidacion(null); if(id==null) e.setActivo(true);
                recomendacionRepository.saveAndFlush(e);
                yield mapear(e);
            }
            case "procedimientos" -> {
                Procedimiento e = id==null ? new Procedimiento() : procedimientoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contenido no encontrado"));
                e.setTitulo(d.getTitulo());
                e.setMomentoAplicacion(d.getMomentoAplicacion());
                requerido(d.getMedidaId(), "medida");
                e.setMedida(medidaRepository.findById(d.getMedidaId()).orElseThrow(() -> new ResourceNotFoundException("medida no encontrado")));
                procedimientoRepository.findByMedidaId(d.getMedidaId()).ifPresent(actual -> { if(!Objects.equals(actual.getId(),id)) throw new ValidationException("Esta medida ya tiene procedimientos; edite el registro existente"); });
                e.setEstado(EstadoValidacion.PENDIENTE); e.setComentarioValidacion(null); if(id==null) e.setActivo(true);
                procedimientoRepository.saveAndFlush(e);
                pasosRepository.deleteByProcedimientoId(e.getId()); pasosRepository.flush();
                for(int i=0;i<d.getPasos().size();i++) pasosRepository.save(PasoProcedimiento.builder().procedimiento(e).orden(i+1).descripcion(d.getPasos().get(i).trim()).build());
                yield mapear(e);
            }
            default -> throw new ResourceNotFoundException("Tipo de contenido desconocido");
        };
    }
    public void cambiarEstado(String tipo, Long id, EstadoValidacion estado, String comentario, boolean desactivar) {
        if(!desactivar && estado!=EstadoValidacion.VALIDADO && estado!=EstadoValidacion.OBSERVADO) throw new ValidationException("Seleccione VALIDADO u OBSERVADO");
        switch(tipo) {
            case "etapas" -> {
                EtapaFenologica e=etapaFenologicaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contenido no encontrado"));
                if(desactivar) e.setActivo(false);
                else { if(Boolean.FALSE.equals(e.getActivo())) throw new ValidationException("El contenido está desactivado"); e.setEstado(estado); e.setComentarioValidacion(comentario); }
                etapaFenologicaRepository.save(e);
            }
            case "riesgos" -> {
                Riesgo e=riesgoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contenido no encontrado"));
                if(desactivar) e.setActivo(false);
                else { if(Boolean.FALSE.equals(e.getActivo())) throw new ValidationException("El contenido está desactivado"); e.setEstado(estado); e.setComentarioValidacion(comentario); }
                riesgoRepository.save(e);
            }
            case "reglas" -> {
                ReglaDecision e=reglaDecisionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contenido no encontrado"));
                if(desactivar) e.setActivo(false);
                else { if(Boolean.FALSE.equals(e.getActivo())) throw new ValidationException("El contenido está desactivado"); e.setEstado(estado); e.setComentarioValidacion(comentario); }
                reglaDecisionRepository.save(e);
            }
            case "medidas" -> {
                Medida e=medidaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contenido no encontrado"));
                if(desactivar) e.setActivo(false);
                else { if(Boolean.FALSE.equals(e.getActivo())) throw new ValidationException("El contenido está desactivado"); e.setEstado(estado); e.setComentarioValidacion(comentario); }
                medidaRepository.save(e);
            }
            case "recomendaciones" -> {
                Recomendacion e=recomendacionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contenido no encontrado"));
                if(desactivar) e.setActivo(false);
                else { if(Boolean.FALSE.equals(e.getActivo())) throw new ValidationException("El contenido está desactivado"); e.setEstado(estado); e.setComentarioValidacion(comentario); }
                recomendacionRepository.save(e);
            }
            case "procedimientos" -> {
                Procedimiento e=procedimientoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contenido no encontrado"));
                if(desactivar) e.setActivo(false);
                else { if(Boolean.FALSE.equals(e.getActivo())) throw new ValidationException("El contenido está desactivado"); e.setEstado(estado); e.setComentarioValidacion(comentario); }
                procedimientoRepository.save(e);
            }
            default -> throw new ResourceNotFoundException("Tipo de contenido desconocido");
        }
    }
    private ContenidoDto mapear(EtapaFenologica e) {
        ContenidoDto d=new ContenidoDto();
        d.setId(e.getId());
        d.setEstado(e.getEstado());
        d.setActivo(e.getActivo());
        d.setComentarioValidacion(e.getComentarioValidacion());
        d.setNombre(e.getNombre());
        d.setDescripcion(e.getDescripcion());
        d.setDiasDesdeMin(e.getDiasDesdeMin());
        d.setDiasDesdeMax(e.getDiasDesdeMax());
        d.setFuenteTecnica(e.getFuenteTecnica());
        return d;
    }
    private ContenidoDto mapear(Riesgo e) {
        ContenidoDto d=new ContenidoDto();
        d.setId(e.getId());
        d.setEstado(e.getEstado());
        d.setActivo(e.getActivo());
        d.setComentarioValidacion(e.getComentarioValidacion());
        d.setNombre(e.getNombre());
        d.setDescripcion(e.getDescripcion());
        d.setVariablesAsociadas(e.getVariablesAsociadas());
        return d;
    }
    private ContenidoDto mapear(ReglaDecision e) {
        ContenidoDto d=new ContenidoDto();
        d.setId(e.getId());
        d.setEstado(e.getEstado());
        d.setActivo(e.getActivo());
        d.setComentarioValidacion(e.getComentarioValidacion());
        d.setVariableMeteorologica(e.getVariableMeteorologica());
        d.setOperador(e.getOperador());
        d.setUmbral(e.getUmbral());
        d.setFuenteTecnica(e.getFuenteTecnica());
        d.setNivelResultante(e.getNivelResultante());
        d.setRiesgoId(e.getRiesgo()==null ? null : e.getRiesgo().getId());
        d.setEtapaFenologicaId(e.getEtapaFenologica()==null ? null : e.getEtapaFenologica().getId());
        return d;
    }
    private ContenidoDto mapear(Medida e) {
        ContenidoDto d=new ContenidoDto();
        d.setId(e.getId());
        d.setEstado(e.getEstado());
        d.setActivo(e.getActivo());
        d.setComentarioValidacion(e.getComentarioValidacion());
        d.setNombre(e.getNombre());
        d.setDescripcion(e.getDescripcion());
        d.setRiesgoId(e.getRiesgo()==null ? null : e.getRiesgo().getId());
        return d;
    }
    private ContenidoDto mapear(Recomendacion e) {
        ContenidoDto d=new ContenidoDto();
        d.setId(e.getId());
        d.setEstado(e.getEstado());
        d.setActivo(e.getActivo());
        d.setComentarioValidacion(e.getComentarioValidacion());
        d.setTexto(e.getTexto());
        d.setMedidaId(e.getMedida()==null ? null : e.getMedida().getId());
        return d;
    }
    private ContenidoDto mapear(Procedimiento e) {
        ContenidoDto d=new ContenidoDto();
        d.setId(e.getId());
        d.setEstado(e.getEstado());
        d.setActivo(e.getActivo());
        d.setComentarioValidacion(e.getComentarioValidacion());
        d.setTitulo(e.getTitulo());
        d.setMomentoAplicacion(e.getMomentoAplicacion());
        d.setMedidaId(e.getMedida()==null ? null : e.getMedida().getId());
        d.setPasos(pasosRepository.findByProcedimientoIdOrderByOrdenAsc(e.getId()).stream().map(PasoProcedimiento::getDescripcion).toList());
        return d;
    }
}
