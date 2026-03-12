package lv.venta.fidi.service;

import java.util.Collection;
import java.util.Optional;

import lv.venta.fidi.model.AppUser;

public interface IAppUserService{

    public abstract void create(String email, String passwordHash) throws Exception;

    public abstract void update(Long id, String email, String passwordHash) throws Exception;

    public abstract Optional<AppUser> findByEmail(String email) throws Exception;

    public abstract Collection<AppUser> retrieveAll() throws Exception;

    public abstract AppUser retrieveById(Long id) throws Exception;

    public abstract void deleteById(Long id) throws Exception;

}