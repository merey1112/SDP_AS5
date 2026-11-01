package com.delivery.strategy.impl;

import com.delivery.exception.NoSuitableRouteException;
import com.delivery.model.DeliveryPriority;
import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;
import com.delivery.model.RouteDecision;
import com.delivery.strategy.RouteStrategy;

import java.util.ArrayList;
import java.util.List;

public class FastestRouteStrategy implements RouteStrategy {

    @Override
    public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        if (routes == null || routes.isEmpty()) {
            throw new NoSuitableRouteException("Route list is empty or null.");
        }

        Route bestRoute = null;
        double minTime = Double.MAX_VALUE;
        List<String> rejections = new ArrayList<>();

        for (Route route : routes) {
            double trafficPenalty = (request.priority() == DeliveryPriority.URGENT) ? 1.0 : 1.15;
            double adjustedTime = route.estimatedMinutes() * trafficPenalty;

            if (route.cost() > request.maxBudget()) {
                rejections.add(route.id() + " rejected: Exceeds budget (" + route.cost() + " > " + request.maxBudget() + ")");
                continue;
            }
            if (adjustedTime > request.maxDeliveryMinutes()) {
                rejections.add(route.id() + " rejected: Exceeds delivery deadline (" + adjustedTime + " min > " + request.maxDeliveryMinutes() + " min)");
                continue;
            }

            if (adjustedTime < minTime) {
                minTime = adjustedTime;
                bestRoute = route;
            }
        }

        if (bestRoute == null) {
            throw new NoSuitableRouteException("No route satisfies constraints for Fastest Strategy.");
        }

        String reason = String.format("Selected %s with estimated time: %.1f mins.", bestRoute.id(), minTime);
        return new RouteDecision(bestRoute, getStrategyName(), reason, rejections);
    }

    @Override
    public String getStrategyName() {
        return "Fastest Route Strategy";
    }
}