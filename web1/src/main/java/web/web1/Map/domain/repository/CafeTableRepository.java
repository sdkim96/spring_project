package web.web1.Map.domain.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import web.web1.Map.domain.models.CafeTable;

import java.util.Optional;

public interface CafeTableRepository extends JpaRepository<CafeTable, Long> {

}
