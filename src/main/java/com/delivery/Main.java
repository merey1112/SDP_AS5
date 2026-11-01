package com.delivery;

import com.delivery.strategy.impl.*;
import com.delivery.comparator.RouteStrategyComparator;
import com.delivery.context.RoutePlanner;
import com.delivery.model.*;
import com.delivery.selector.StrategySelector;
import com.delivery.strategy.RouteStrategy;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Location start = new Location("Astana Hub", 51.09, 71.41);
        Location end = new Location("Airport", 51.02, 71.46);

        // 6-8 тестовых маршрутов[cite: 1]
        List<Route> sampleRoutes = List.of(
                new Route("Route-1 (Express Highway)", start, end, 18.5, 20, 4500, 4.2),
                new Route("Route-2 (City Center)", start, end, 14.0, 35, 2500, 3.1),
                new Route("Route-3 (Eco Ring)", start, end, 22.0, 30, 3000, 1.8),
                new Route("Route-4 (Bypass Toll)", start, end, 16.0, 22, 5000, 3.8),
                new Route("Route-5 (Night Short)", start, end, 13.5, 25, 2200, 2.9),
                new Route("Route-6 (Green Electric)", start, end, 19.0, 28, 3800, 1.2)
        );

        DeliveryRequest urgentRequest = new DeliveryRequest(DeliveryPriority.URGENT, 6000, 40, false);
        DeliveryRequest ecoRequest = new DeliveryRequest(DeliveryPriority.ECO, 5000, 45, true);

        System.out.println(">>> 1. DEMO: RUNTIME STRATEGY SWITCHING <<<");
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());
        System.out.println(planner.plan(sampleRoutes, urgentRequest));

        // Смена стратегии на лету 1[cite: 1]
        planner.setStrategy(new CheapestRouteStrategy());
        System.out.println(planner.plan(sampleRoutes, urgentRequest));

        // Смена стратегии на лету 2[cite: 1]
        planner.setStrategy(new EcoFriendlyRouteStrategy());
        System.out.println(planner.plan(sampleRoutes, urgentRequest));

        // Смена стратегии на лету 3[cite: 1]
        planner.setStrategy(new BalancedRouteStrategy(0.4, 0.3, 0.1, 0.2));
        System.out.println(planner.plan(sampleRoutes, urgentRequest));

        System.out.println("\n>>> 2. DEMO: AUTOMATIC STRATEGY SELECTION <<<");
        RouteStrategy autoStrategy = StrategySelector.selectStrategy(ecoRequest);
        planner.setStrategy(autoStrategy);
        System.out.println(planner.plan(sampleRoutes, ecoRequest));

        System.out.println("\n>>> 3. DEMO: STRATEGY COMPARISON MODE <<<");
        List<RouteStrategy> allStrategies = List.of(
                new FastestRouteStrategy(),
                new CheapestRouteStrategy(),
                new EcoFriendlyRouteStrategy(),
                new BalancedRouteStrategy(0.4, 0.3, 0.1, 0.2),
                new PremiumRouteStrategy()
        );
        RouteStrategyComparator.compareAll(allStrategies, sampleRoutes, urgentRequest);
    }
}