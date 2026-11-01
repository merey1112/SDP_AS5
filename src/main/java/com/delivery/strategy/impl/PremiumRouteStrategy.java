package com.delivery.strategy.impl;

import com.delivery.exception.NoSuitableRouteException;
import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;
import com.delivery.model.RouteDecision;
import com.delivery.strategy.RouteStrategy;

import java.util.ArrayList;
import java.util.List;

public class PremiumRouteStrategy implements RouteStrategy {

    @Override
    public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        if (routes == null || routes.isEmpty()) {
            throw new NoSuitableRouteException("Route list is empty or null.");
        }

        Route bestRoute = null;
        double bestScore = Double.MAX_VALUE;
        List<String> rejections = new ArrayList<>();

        for (Route route : routes) {
            if (route.cost() > request.maxBudget()) {
                rejections.add(route.id() + " rejected: Budget constraint.");
                continue;
            }
            if (route.estimatedMinutes() > request.maxDeliveryMinutes()) {
                rejections.add(route.id() + " rejected: Time constraint.");
                continue;
            }

            // Приоритет надёжности и скорости без учёта цены
            double score = route.estimatedMinutes() * 0.8 + route.distanceKm() * 0.2;
            if (score < bestScore) {
                bestScore = score;
                bestRoute = route;
            }
        }

        if (bestRoute == null) {
            throw new NoSuitableRouteException("No route satisfies constraints for Premium Strategy.");
        }

        String reason = String.format("Selected premium VIP route %s with score: %.2f", bestRoute.id(), bestScore);
        return new RouteDecision(bestRoute, getStrategyName(), reason, rejections);
    }

    @Override
    public String getStrategyName() {
        return "Premium VIP Route Strategy";
    }
}