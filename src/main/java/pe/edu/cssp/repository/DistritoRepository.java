package pe.edu.cssp.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.cssp.entity.Distrito;
import java.util.List;
import java.util.Optional;
public interface DistritoRepository extends JpaRepository<Distrito, Integer> {
    List<Distrito> findAllByOrderByNombreAsc();

    Optional<Distrito> findByProvincia_IdAndCodigo(int provinciaId, String codigo);

    /** Código ubigeo completo (departamento + provincia + distrito, 6 dígitos) de cada distrito existente. */
    @Query("select concat(d.codigo, p.codigo, di.codigo) from Distrito di join di.provincia p join p.departamento d")
    List<String> codigosUbigeo();

    /** Distritos activos de un departamento (por su código ubigeo de 2 dígitos) para los desplegables de sismos y estaciones. */
    @Query("select di from Distrito di join fetch di.provincia p join fetch p.departamento d "
            + "where d.codigo = :codigoDepartamento and di.estado = 'ACTIVO' order by p.nombre, di.nombre")
    List<Distrito> activosPorDepartamento(@Param("codigoDepartamento") String codigoDepartamento);

    /** 0 en un filtro numérico y cadena vacía en un filtro de texto significan "sin filtrar". */
    @Query(value = "select di from Distrito di join fetch di.provincia p join fetch p.departamento d "
            + "where (:departamentoId = 0 or d.id = :departamentoId) and (:provinciaId = 0 or p.id = :provinciaId) "
            + "and (:estado = '' or di.estado = :estado) "
            + "and (:texto = '' or lower(di.nombre) like lower(concat('%', :texto, '%')) "
            + "or concat(d.codigo, p.codigo, di.codigo) like concat(:texto, '%'))",
            countQuery = "select count(di) from Distrito di join di.provincia p join p.departamento d "
            + "where (:departamentoId = 0 or d.id = :departamentoId) and (:provinciaId = 0 or p.id = :provinciaId) "
            + "and (:estado = '' or di.estado = :estado) "
            + "and (:texto = '' or lower(di.nombre) like lower(concat('%', :texto, '%')) "
            + "or concat(d.codigo, p.codigo, di.codigo) like concat(:texto, '%'))")
    Page<Distrito> buscar(@Param("departamentoId") int departamentoId, @Param("provinciaId") int provinciaId,
                          @Param("estado") String estado, @Param("texto") String texto, Pageable pageable);
}
