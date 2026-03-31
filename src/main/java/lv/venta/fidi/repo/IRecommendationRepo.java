package lv.venta.fidi.repo;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import lv.venta.fidi.model.AppUser;
import lv.venta.fidi.model.Movie;
import lv.venta.fidi.model.Recommendation;

public interface IRecommendationRepo extends JpaRepository<Recommendation, Long> {

    Collection<Recommendation> findByUser(AppUser user);

    Optional<Recommendation> findByUserAndMovie(AppUser user, Movie movie);

    boolean existsByUserAndMovie(AppUser user, Movie movie);

    void deleteByUser(AppUser user);
}