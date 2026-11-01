package com.delivery.strategy.impl;

import com.delivery.exception.NoSuitableRouteException;
import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;
import com.delivery.model.RouteDecision;
import com.delivery.strategy.RouteStrategy;

import java.util.ArrayList;
import java.util.List;

public class CheapestRouteStrategy implements RouteStrategy {

    @Override
    public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        if (routes == null || routes.isEmpty()) {
            throw new NoSuitableRouteException("Route list is empty or null.");
        }

        Route bestRoute = null;
        double minCost = Double.MAX_VALUE;
        List<String> rejections = new ArrayList<>();

        for (Route route : routes) {
            double calculatedCost = route.cost() + (route.distanceKm() * 0.2);

            if (calculatedCost > request.maxBudget()) {
                rejections.add(route.id() + " rejected: Cost exceeds max budget.");
                continue;
            }
            if (route.estimatedMinutes() > request.maxDeliveryMinutes()) {
                rejections.add(route.id() + " rejected: Delivery time exceeds limit.");
                continue;
            }

            if (calculatedCost < minCost) {
                minCost = calculatedCost;
                bestRoute = route;
            }
        }

        if (bestRoute == null) {
            throw new NoSuitableRouteException("No route satisfies constraints for Cheapest Strategy.");
        }

        String reason = String.format("Selected %s with total cost: %.2f", bestRoute.id(), minCost);
        return new RouteDecision(bestRoute, getStrategyName(), reason, rejections);
    }

    @Override
    public String getStrategyName() {
        return "Cheapest Route Strategy";
    }
}