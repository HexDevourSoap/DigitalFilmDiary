package lv.venta.fidi.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import lv.venta.fidi.model.AppUser;

public interface IAppUserRepo extends JpaRepository<AppUser, Long> {

    public abstract Optional<AppUser> findByEmail(String email);

    public abstract boolean existsByEmail(String email);

}