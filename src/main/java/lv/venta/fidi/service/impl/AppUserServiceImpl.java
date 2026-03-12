package lv.venta.fidi.service.impl;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lv.venta.fidi.model.AppUser;
import lv.venta.fidi.repo.IAppUserRepo;
import lv.venta.fidi.service.IAppUserService;

@Service
public class AppUserServiceImpl implements IAppUserService {

    @Autowired
    private IAppUserRepo appUserRepo;

    @Override
    public Collection<AppUser> retrieveAll() throws Exception {
        if (appUserRepo.count() == 0) {
            throw new Exception("There are no users in the database");
        }

        return appUserRepo.findAll();
    }

    @Override
    public AppUser retrieveById(Long id) throws Exception {
        if (id == null || id < 0) {
            throw new Exception("ID cannot be null or negative");
        }

        return appUserRepo.findById(id)
                .orElseThrow(() -> new Exception("User with ID " + id + " was not found"));
    }

    @Override
    public void deleteById(Long id) throws Exception {
        if (id == null || id < 0) {
            throw new Exception("ID cannot be null or negative");
        }

        AppUser user = appUserRepo.findById(id)
                .orElseThrow(() -> new Exception("User with ID " + id + " was not found"));

        appUserRepo.delete(user);
    }

    @Override
    public void create(String email, String passwordHash) throws Exception {
        if (email == null || email.isBlank()) {
            throw new Exception("Email cannot be empty");
        }

        if (passwordHash == null || passwordHash.isBlank()) {
            throw new Exception("Password hash cannot be empty");
        }

        if (appUserRepo.existsByEmail(email)) {
            throw new Exception("User with email " + email + " already exists");
        }

        AppUser user = new AppUser(email, passwordHash);
        appUserRepo.save(user);
    }

    @Override
    public void update(Long id, String email, String passwordHash) throws Exception {
        if (id == null || id < 0) {
            throw new Exception("ID cannot be null or negative");
        }

        if (email == null || email.isBlank()) {
            throw new Exception("Email cannot be empty");
        }

        if (passwordHash == null || passwordHash.isBlank()) {
            throw new Exception("Password hash cannot be empty");
        }

        AppUser user = appUserRepo.findById(id)
                .orElseThrow(() -> new Exception("User with ID " + id + " was not found"));

        Optional<AppUser> existingUser = appUserRepo.findByEmail(email);
        if (existingUser.isPresent() && existingUser.get().getUserId() != id) {
            throw new Exception("Another user with email " + email + " already exists");
        }

        user.setEmail(email);
        user.setPasswordHash(passwordHash);

        appUserRepo.save(user);
    }

    @Override
    public Optional<AppUser> findByEmail(String email) throws Exception {
        if (email == null || email.isBlank()) {
            throw new Exception("Email cannot be empty");
        }

        return appUserRepo.findByEmail(email);
    }
}