package com.delivery.selector;

import com.delivery.strategy.impl.*;
import com.delivery.model.DeliveryPriority;
import com.delivery.model.DeliveryRequest;
import com.delivery.strategy.RouteStrategy;

public class StrategySelector {

    public static RouteStrategy selectStrategy(DeliveryRequest request) {
        if (request.ecoPreferred()) {
            return new EcoFriendlyRouteStrategy();
        }

        DeliveryPriority priority = request.priority();
        if (priority == null) {
            return new BalancedRouteStrategy(0.4, 0.3, 0.1, 0.2);
        }

        return switch (priority) {
            case URGENT -> new FastestRouteStrategy();
            case ECONOMY -> new CheapestRouteStrategy();
            case ECO -> new EcoFriendlyRouteStrategy();
            case PREMIUM -> new PremiumRouteStrategy();
            case STANDARD -> new BalancedRouteStrategy(0.4, 0.3, 0.1, 0.2);
        };
    }
}