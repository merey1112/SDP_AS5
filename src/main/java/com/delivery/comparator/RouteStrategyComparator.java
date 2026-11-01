package com.delivery.comparator;

import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;
import com.delivery.model.RouteDecision;
import com.delivery.strategy.RouteStrategy;

import java.util.List;

public class RouteStrategyComparator {

    public static void compareAll(List<RouteStrategy> strategies, List<Route> routes, DeliveryRequest request) {
        System.out.println("==================================================");
        System.out.println("          STRATEGY COMPARISON MODE               ");
        System.out.println("==================================================");

        for (RouteStrategy strategy : strategies) {
            try {
                RouteDecision decision = strategy.selectRoute(routes, request);
                System.out.println(decision);
            } catch (Exception e) {
                System.out.println("[" + strategy.getStrategyName() + "] Failed: " + e.getMessage());
            }
            System.out.println("--------------------------------------------------");
        }
    }
}