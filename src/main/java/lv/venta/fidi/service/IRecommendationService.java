package lv.venta.fidi.service;

import java.util.Collection;

import lv.venta.fidi.model.Recommendation;

public interface IRecommendationService {

    public abstract void generateRecommendationsForUser(Long userId, String appLang) throws Exception;

    public abstract Collection<Recommendation> retrieveByUserId(Long userId) throws Exception;

    public abstract void clearRecommendationsForUser(Long userId) throws Exception;
}