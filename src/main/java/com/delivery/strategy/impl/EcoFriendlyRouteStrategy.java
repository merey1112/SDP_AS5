package com.delivery.strategy.impl;

import com.delivery.exception.NoSuitableRouteException;
import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;
import com.delivery.model.RouteDecision;
import com.delivery.strategy.RouteStrategy;

import java.util.ArrayList;
import java.util.List;

public class EcoFriendlyRouteStrategy implements RouteStrategy {

    @Override
    public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        if (routes == null || routes.isEmpty()) {
            throw new NoSuitableRouteException("Route list is empty or null.");
        }

        Route bestRoute = null;
        double minEcoScore = Double.MAX_VALUE;
        List<String> rejections = new ArrayList<>();

        for (Route route : routes) {
            double ecoScore = (route.carbonEmission() * 0.6) + (route.distanceKm() * 0.4);

            if (route.cost() > request.maxBudget()) {
                rejections.add(route.id() + " rejected: Exceeds budget.");
                continue;
            }
            if (route.estimatedMinutes() > request.maxDeliveryMinutes()) {
                rejections.add(route.id() + " rejected: Exceeds max delivery time.");
                continue;
            }

            if (ecoScore < minEcoScore) {
                minEcoScore = ecoScore;
                bestRoute = route;
            }
        }

        if (bestRoute == null) {
            throw new NoSuitableRouteException("No route satisfies constraints for Eco Strategy.");
        }

        String reason = String.format("Selected %s with best eco score: %.2f", bestRoute.id(), minEcoScore);
        return new RouteDecision(bestRoute, getStrategyName(), reason, rejections);
    }

    @Override
    public String getStrategyName() {
        return "Eco-Friendly Route Strategy";
    }
}