package com.delivery.strategy.impl;

import com.delivery.exception.InvalidStrategyWeightsException;
import com.delivery.exception.NoSuitableRouteException;
import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;
import com.delivery.model.RouteDecision;
import com.delivery.strategy.RouteStrategy;

import java.util.ArrayList;
import java.util.List;

public class BalancedRouteStrategy implements RouteStrategy {
    private final double timeWeight;
    private final double costWeight;
    private final double distanceWeight;
    private final double emissionWeight;

    public BalancedRouteStrategy(double timeWeight, double costWeight, double distanceWeight, double emissionWeight) {
        double sum = timeWeight + costWeight + distanceWeight + emissionWeight;
        if (Math.abs(sum - 1.0) > 0.0001) {
            throw new InvalidStrategyWeightsException("Strategy weights must sum to 1.0! Current sum: " + sum);
        }
        this.timeWeight = timeWeight;
        this.costWeight = costWeight;
        this.distanceWeight = distanceWeight;
        this.emissionWeight = emissionWeight;
    }

    @Override
    public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        if (routes == null || routes.isEmpty()) {
            throw new NoSuitableRouteException("Route list is empty or null.");
        }

        Route bestRoute = null;
        double minScore = Double.MAX_VALUE;
        List<String> rejections = new ArrayList<>();

        for (Route route : routes) {
            if (route.cost() > request.maxBudget()) {
                rejections.add(route.id() + " rejected: Exceeds budget.");
                continue;
            }
            if (route.estimatedMinutes() > request.maxDeliveryMinutes()) {
                rejections.add(route.id() + " rejected: Exceeds deadline.");
                continue;
            }

            double score = (route.estimatedMinutes() * timeWeight)
                    + (route.cost() * costWeight)
                    + (route.distanceKm() * distanceWeight)
                    + (route.carbonEmission() * emissionWeight);

            if (score < minScore) {
                minScore = score;
                bestRoute = route;
            }
        }

        if (bestRoute == null) {
            throw new NoSuitableRouteException("No route satisfies constraints for Balanced Strategy.");
        }

        String reason = String.format("Selected %s with weighted balanced score: %.2f", bestRoute.id(), minScore);
        return new RouteDecision(bestRoute, getStrategyName(), reason, rejections);
    }

    @Override
    public String getStrategyName() {
        return "Balanced Route Strategy";
    }
}