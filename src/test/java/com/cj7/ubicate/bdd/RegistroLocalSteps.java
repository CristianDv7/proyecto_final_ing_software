package com.cj7.ubicate.bdd;

import com.cj7.ubicate.infrastructure.persistence.ComercianteJpaRepository;
import com.cj7.ubicate.infrastructure.persistence.LocalJpaRepository;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/** Step definitions BDD que ejercen el endpoint de registro vía MockMvc. */
public class RegistroLocalSteps {

    private static final String PATH = "/locales";
    private static final double BASE_LAT = -0.180653;
    private static final double BASE_LNG = -78.467834;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LocalJpaRepository localRepository;

    @Autowired
    private ComercianteJpaRepository comercianteRepository;

    private MockHttpServletResponse respuesta;
    private String whatsappRegistrado;

    @Before
    public void limpiar() {
        localRepository.deleteAll();
        comercianteRepository.deleteAll();
    }

    private Map<String, Object> solicitudValida() {
        Map<String, Object> req = new HashMap<>();
        req.put("nombre", "Panadería La Espiga");
        req.put("tipoNegocioId", 3);
        req.put("ubicacion", ubicacion(BASE_LAT, BASE_LNG, "GPS"));
        req.put("whatsapp", "+593987659999");
        req.put("horarios", new ArrayList<>(List.of(horario("08:00:00", "18:00:00"))));
        req.put("servicios", new ArrayList<>(List.of("Domicilio")));
        return req;
    }

    private Map<String, Object> ubicacion(double lat, double lng, String origen) {
        Map<String, Object> u = new HashMap<>();
        u.put("latitud", lat);
        u.put("longitud", lng);
        u.put("origen", origen);
        u.put("direccionTexto", "Av. Amazonas");
        return u;
    }

    private Map<String, Object> horario(String apertura, String cierre) {
        Map<String, Object> h = new HashMap<>();
        h.put("diaSemana", "LUN");
        h.put("horaApertura", apertura);
        h.put("horaCierre", cierre);
        h.put("cruzaMedianoche", false);
        return h;
    }

    private void registrar(Map<String, Object> req) throws Exception {
        respuesta = mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andReturn().getResponse();
    }

    private JsonNode cuerpo() throws Exception {
        return objectMapper.readTree(respuesta.getContentAsString());
    }

    // --- Given ---

    @Dado("un comerciante sin perfil previo")
    public void unComercianteSinPerfilPrevio() {
        // Estado limpio garantizado por @Before.
    }

    @Dado("un local ya registrado con WhatsApp {string}")
    public void unLocalYaRegistradoConWhatsApp(String whatsapp) throws Exception {
        whatsappRegistrado = whatsapp;
        Map<String, Object> req = solicitudValida();
        req.put("whatsapp", whatsapp);
        registrar(req);
        assertEquals(201, respuesta.getStatus());
    }

    // --- When ---

    @Cuando("registra su local con los datos obligatorios completos")
    public void registraConDatosCompletos() throws Exception {
        registrar(solicitudValida());
    }

    @Cuando("registra su local con la ubicación detectada por GPS")
    public void registraConGps() throws Exception {
        registrar(solicitudValida());
    }

    @Cuando("registra su local con el WhatsApp {string}")
    public void registraConWhatsApp(String whatsapp) throws Exception {
        Map<String, Object> req = solicitudValida();
        req.put("whatsapp", whatsapp);
        registrar(req);
    }

    @Cuando("intenta registrar su local sin WhatsApp ni horarios")
    public void registraSinWhatsAppNiHorarios() throws Exception {
        Map<String, Object> req = solicitudValida();
        req.remove("whatsapp");
        req.remove("horarios");
        registrar(req);
    }

    @Cuando("registra su local sin servicios")
    public void registraSinServicios() throws Exception {
        Map<String, Object> req = solicitudValida();
        req.remove("servicios");
        req.put("whatsapp", "+593987650100");
        registrar(req);
    }

    @Cuando("otro registro usa el mismo WhatsApp y la misma ubicación")
    public void registraDuplicado() throws Exception {
        Map<String, Object> req = solicitudValida();
        req.put("whatsapp", whatsappRegistrado);
        registrar(req);
    }

    @Cuando("el mismo comerciante registra otro local en distinta ubicación")
    public void registraOtroLocalDistintaUbicacion() throws Exception {
        Map<String, Object> req = solicitudValida();
        req.put("whatsapp", whatsappRegistrado);
        req.put("ubicacion", ubicacion(1.5, 2.5, "MANUAL"));
        registrar(req);
    }

    @Cuando("registra su local con un WhatsApp con formato inválido")
    public void registraConWhatsAppInvalido() throws Exception {
        Map<String, Object> req = solicitudValida();
        req.put("whatsapp", "12345");
        registrar(req);
    }

    @Cuando("registra su local con un horario cuyo cierre es anterior a la apertura")
    public void registraConHorarioIncoherente() throws Exception {
        Map<String, Object> req = solicitudValida();
        req.put("whatsapp", "+593987650101");
        req.put("horarios", new ArrayList<>(List.of(horario("18:00:00", "08:00:00"))));
        registrar(req);
    }

    @Cuando("registra su local con un tipo de negocio inexistente")
    public void registraConTipoInexistente() throws Exception {
        Map<String, Object> req = solicitudValida();
        req.put("whatsapp", "+593987650102");
        req.put("tipoNegocioId", 999);
        registrar(req);
    }

    // --- Then ---

    @Entonces("el local queda publicado con código {int}")
    public void elLocalQuedaPublicado(int codigo) throws Exception {
        assertEquals(codigo, respuesta.getStatus());
        assertEquals("PUBLICADO", cuerpo().get("estadoPublicacion").asText());
    }

    @Entonces("el registro es rechazado con código {int}")
    public void elRegistroEsRechazado(int codigo) {
        assertEquals(codigo, respuesta.getStatus());
    }

    @Y("el perfil expone el origen de ubicación {string}")
    public void elPerfilExponeOrigen(String origen) throws Exception {
        assertEquals(origen, cuerpo().get("ubicacion").get("origen").asText());
    }

    @Y("el perfil expone el WhatsApp {string}")
    public void elPerfilExponeWhatsApp(String whatsapp) throws Exception {
        assertEquals(whatsapp, cuerpo().get("whatsapp").asText());
    }

    @Y("la respuesta informa campos obligatorios faltantes")
    public void laRespuestaInformaCamposFaltantes() throws Exception {
        JsonNode campos = cuerpo().get("camposFaltantes");
        assertTrue(campos.isArray());
        assertFalse(campos.isEmpty());
    }

    @Y("el perfil no expone servicios")
    public void elPerfilNoExponeServicios() throws Exception {
        JsonNode servicios = cuerpo().get("servicios");
        assertTrue(servicios == null || servicios.isEmpty());
    }
}
