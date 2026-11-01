package com.delivery;

import com.delivery.strategy.RouteStrategy;
import com.delivery.strategy.impl.*;
import com.delivery.comparator.RouteStrategyComparator;
import com.delivery.context.RoutePlanner;
import com.delivery.exception.InvalidStrategyWeightsException;
import com.delivery.exception.NoSuitableRouteException;
import com.delivery.model.*;
import com.delivery.selector.StrategySelector;
import com.delivery.strategy.RouteStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SmartDeliveryTest {

    private List<Route> sampleRoutes;
    private DeliveryRequest standardRequest;

    @BeforeEach
    void setUp() {
        Location start = new Location("Astana Hub", 51.09, 71.41);
        Location end = new Location("Airport", 51.02, 71.46);

        sampleRoutes = List.of(
                new Route("Route-1 (Express Highway)", start, end, 18.5, 20, 4500, 4.2),
                new Route("Route-2 (City Center)", start, end, 14.0, 35, 2500, 3.1),
                new Route("Route-3 (Eco Ring)", start, end, 22.0, 30, 3000, 1.8),
                new Route("Route-4 (Bypass Toll)", start, end, 16.0, 22, 5000, 3.8),
                new Route("Route-5 (Night Short)", start, end, 13.5, 25, 2200, 2.9)
        );

        standardRequest = new DeliveryRequest(DeliveryPriority.STANDARD, 6000, 40, false);
    }

    @Test
    @DisplayName("1. Fastest Strategy Test")
    void testFastestRouteStrategy() {
        RouteStrategy strategy = new FastestRouteStrategy();
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.URGENT, 6000, 40, false);
        RouteDecision decision = strategy.selectRoute(sampleRoutes, request);

        assertNotNull(decision.getSelectedRoute());
        assertEquals("Route-1 (Express Highway)", decision.getSelectedRoute().id());
    }

    @Test
    @DisplayName("2. Cheapest Strategy Test")
    void testCheapestRouteStrategy() {
        RouteStrategy strategy = new CheapestRouteStrategy();
        RouteDecision decision = strategy.selectRoute(sampleRoutes, standardRequest);

        assertNotNull(decision.getSelectedRoute());
        assertEquals("Route-5 (Night Short)", decision.getSelectedRoute().id());
    }

    @Test
    @DisplayName("3. Eco Strategy Test")
    void testEcoFriendlyRouteStrategy() {
        RouteStrategy strategy = new EcoFriendlyRouteStrategy();
        RouteDecision decision = strategy.selectRoute(sampleRoutes, standardRequest);

        assertNotNull(decision.getSelectedRoute());
        assertEquals("Route-5 (Night Short)", decision.getSelectedRoute().id());
    }


    @Test
    @DisplayName("4. Balanced Strategy Test")
    void testBalancedRouteStrategy() {
        RouteStrategy strategy = new BalancedRouteStrategy(0.4, 0.3, 0.1, 0.2);
        RouteDecision decision = strategy.selectRoute(sampleRoutes, standardRequest);

        assertNotNull(decision.getSelectedRoute());
    }

    @Test
    @DisplayName("5. Runtime Strategy Switching Test (3 switches)")
    void testRuntimeStrategySwitching() {
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());
        assertEquals("Fastest Route Strategy", planner.getStrategy().getStrategyName());

        planner.setStrategy(new CheapestRouteStrategy());
        assertEquals("Cheapest Route Strategy", planner.getStrategy().getStrategyName());

        planner.setStrategy(new EcoFriendlyRouteStrategy());
        assertEquals("Eco-Friendly Route Strategy", planner.getStrategy().getStrategyName());
    }

    @Test
    @DisplayName("6. Automatic Strategy Selection Test")
    void testAutomaticStrategySelection() {
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.URGENT, 5000, 30, false);
        RouteStrategy strategy = StrategySelector.selectStrategy(request);

        assertTrue(strategy instanceof FastestRouteStrategy);
    }

    @Test
    @DisplayName("7. Hard Constraints Filtering Test")
    void testHardConstraintFiltering() {
        // Устанавливаем очень малый бюджет
        DeliveryRequest tightBudgetRequest = new DeliveryRequest(DeliveryPriority.STANDARD, 2400, 40, false);
        RouteStrategy strategy = new CheapestRouteStrategy();

        RouteDecision decision = strategy.selectRoute(sampleRoutes, tightBudgetRequest);
        assertEquals("Route-5 (Night Short)", decision.getSelectedRoute().id());
    }

    @Test
    @DisplayName("8. Budget Restriction Hard Constraint Test")
    void testBudgetRestriction() {
        DeliveryRequest lowBudget = new DeliveryRequest(DeliveryPriority.STANDARD, 2000, 40, false);
        RouteStrategy strategy = new FastestRouteStrategy();

        assertThrows(NoSuitableRouteException.class, () -> strategy.selectRoute(sampleRoutes, lowBudget));
    }

    @Test
    @DisplayName("9. Deadline Restriction Hard Constraint Test")
    void testDeadlineRestriction() {
        DeliveryRequest tightDeadline = new DeliveryRequest(DeliveryPriority.STANDARD, 10000, 15, false);
        RouteStrategy strategy = new FastestRouteStrategy();

        assertThrows(NoSuitableRouteException.class, () -> strategy.selectRoute(sampleRoutes, tightDeadline));
    }

    @Test
    @DisplayName("10. No Valid Route Scenario Test")
    void testNoValidRouteScenario() {
        DeliveryRequest impossibleRequest = new DeliveryRequest(DeliveryPriority.URGENT, 100, 5, false);
        RouteStrategy strategy = new BalancedRouteStrategy(0.25, 0.25, 0.25, 0.25);

        assertThrows(NoSuitableRouteException.class, () -> strategy.selectRoute(sampleRoutes, impossibleRequest));
    }

    @Test
    @DisplayName("11. Invalid Balanced Weights Exception Test")
    void testInvalidBalancedWeights() {
        assertThrows(InvalidStrategyWeightsException.class, () ->
                new BalancedRouteStrategy(0.5, 0.5, 0.5, 0.5) // Сумма = 2.0 (должна быть 1.0)
        );
    }

    @Test
    @DisplayName("12. New 5th Strategy (Premium) Test")
    void testPremiumRouteStrategy() {
        RouteStrategy strategy = new PremiumRouteStrategy();
        RouteDecision decision = strategy.selectRoute(sampleRoutes, standardRequest);

        assertNotNull(decision.getSelectedRoute());
        assertEquals("Premium VIP Route Strategy", strategy.getStrategyName());
    }

    @Test
    @DisplayName("13. Comparison of Strategies Test")
    void testStrategyComparisonMode() {
        List<RouteStrategy> strategies = List.of(new FastestRouteStrategy(), new CheapestRouteStrategy());
        assertDoesNotThrow(() -> RouteStrategyComparator.compareAll(strategies, sampleRoutes, standardRequest));
    }

    @Test
    @DisplayName("14. Boundary Case: Empty Route List Test")
    void testEmptyRouteList() {
        RouteStrategy strategy = new FastestRouteStrategy();
        assertThrows(NoSuitableRouteException.class, () -> strategy.selectRoute(Collections.emptyList(), standardRequest));
    }

    @Test
    @DisplayName("15. Failure Scenario: Null Request / Routes Test")
    void testFailureScenarioNullData() {
        RouteStrategy strategy = new EcoFriendlyRouteStrategy();
        assertThrows(NoSuitableRouteException.class, () -> strategy.selectRoute(null, standardRequest));
    }
}