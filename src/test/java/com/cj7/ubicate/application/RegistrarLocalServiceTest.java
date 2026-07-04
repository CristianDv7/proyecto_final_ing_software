package com.cj7.ubicate.application;

import com.cj7.ubicate.application.command.NuevoLocal;
import com.cj7.ubicate.application.usecase.RegistrarLocalService;
import com.cj7.ubicate.domain.exception.DatosInvalidosException;
import com.cj7.ubicate.domain.exception.LocalDuplicadoException;
import com.cj7.ubicate.domain.model.Comerciante;
import com.cj7.ubicate.domain.model.DiaSemana;
import com.cj7.ubicate.domain.model.HorarioAtencion;
import com.cj7.ubicate.domain.model.Local;
import com.cj7.ubicate.domain.model.OrigenUbicacion;
import com.cj7.ubicate.domain.model.Ubicacion;
import com.cj7.ubicate.domain.port.ComercianteRepositoryPort;
import com.cj7.ubicate.domain.port.LocalRepositoryPort;
import com.cj7.ubicate.domain.port.TipoNegocioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrarLocalServiceTest {

    private FakeLocalRepo localRepo;
    private FakeComercianteRepo comercianteRepo;
    private FakeTipoRepo tipoRepo;
    private RegistrarLocalService service;

    @BeforeEach
    void setUp() {
        localRepo = new FakeLocalRepo();
        comercianteRepo = new FakeComercianteRepo();
        tipoRepo = new FakeTipoRepo(Set.of(1L, 3L));
        service = new RegistrarLocalService(localRepo, comercianteRepo, tipoRepo);
    }

    private NuevoLocal comando(String whatsapp) {
        return new NuevoLocal("Panadería", 3L,
                new Ubicacion(-0.18, -78.46, OrigenUbicacion.GPS, null), whatsapp,
                List.of(new HorarioAtencion(DiaSemana.LUN, LocalTime.of(8, 0), LocalTime.of(18, 0), false)),
                List.of());
    }

    @Test
    void publicaLocalYCreaComercianteCuandoDatosValidos() {
        Local local = service.registrar(comando("+593987654321"));

        assertEquals(1, localRepo.guardados.size());
        assertTrue(comercianteRepo.almacen.containsKey("+593987654321"));
        assertEquals(local.getComercianteId(), comercianteRepo.almacen.get("+593987654321").id());
    }

    @Test
    void lanzaDatosInvalidosCuandoTipoNegocioNoExiste() {
        NuevoLocal comando = new NuevoLocal("Panadería", 999L,
                new Ubicacion(-0.18, -78.46, OrigenUbicacion.GPS, null), "+593987654321",
                List.of(new HorarioAtencion(DiaSemana.LUN, LocalTime.of(8, 0), LocalTime.of(18, 0), false)),
                List.of());

        DatosInvalidosException ex = assertThrows(DatosInvalidosException.class,
                () -> service.registrar(comando));
        assertEquals(List.of("tipoNegocioId"), ex.getCampos());
    }

    @Test
    void lanzaDuplicadoCuandoYaExisteMismoWhatsappYUbicacion() {
        localRepo.duplicados.add("+593911111111|-0.18|-78.46");

        assertThrows(LocalDuplicadoException.class,
                () -> service.registrar(comando("+593911111111")));
    }

    @Test
    void reutilizaComercianteExistentePorWhatsapp() {
        Comerciante existente = Comerciante.nuevo("+593922222222");
        comercianteRepo.almacen.put("+593922222222", existente);

        Local local = service.registrar(comando("+593922222222"));

        assertEquals(existente.id(), local.getComercianteId());
        // El comerciante existente se reutiliza: no se persiste uno nuevo.
        assertEquals(0, comercianteRepo.guardadosNuevos);
    }

    // --- Fakes de los puertos ---

    static class FakeLocalRepo implements LocalRepositoryPort {
        final List<Local> guardados = new ArrayList<>();
        final Set<String> duplicados = new HashSet<>();

        @Override
        public Local guardar(Local local) {
            guardados.add(local);
            return local;
        }

        @Override
        public boolean existeDuplicado(String whatsapp, double latitud, double longitud) {
            return duplicados.contains(whatsapp + "|" + latitud + "|" + longitud);
        }
    }

    static class FakeComercianteRepo implements ComercianteRepositoryPort {
        final Map<String, Comerciante> almacen = new HashMap<>();
        int guardadosNuevos = 0;

        @Override
        public Optional<Comerciante> buscarPorWhatsapp(String whatsapp) {
            return Optional.ofNullable(almacen.get(whatsapp));
        }

        @Override
        public Comerciante guardar(Comerciante comerciante) {
            almacen.put(comerciante.whatsapp(), comerciante);
            guardadosNuevos++;
            return comerciante;
        }
    }

    static class FakeTipoRepo implements TipoNegocioRepositoryPort {
        private final Set<Long> ids;

        FakeTipoRepo(Set<Long> ids) {
            this.ids = ids;
        }

        @Override
        public boolean existePorId(Long id) {
            return ids.contains(id);
        }
    }
}
