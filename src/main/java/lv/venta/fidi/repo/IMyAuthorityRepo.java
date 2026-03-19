package lv.venta.fidi.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import lv.venta.fidi.model.MyAuthority;

public interface IMyAuthorityRepo extends JpaRepository<MyAuthority, Long> {

    Optional<MyAuthority> findByTitle(String title);

    boolean existsByTitle(String title);
}