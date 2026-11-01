package com.delivery.model;

import java.util.List;

public class RouteDecision {
    private final Route selectedRoute;
    private final String strategyName;
    private final String selectionReason;
    private final List<String> rejectionReasons;

    public RouteDecision(Route selectedRoute, String strategyName, String selectionReason, List<String> rejectionReasons) {
        this.selectedRoute = selectedRoute;
        this.strategyName = strategyName;
        this.selectionReason = selectionReason;
        this.rejectionReasons = rejectionReasons;
    }

    public Route getSelectedRoute() {
        return selectedRoute;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public String getSelectionReason() {
        return selectionReason;
    }

    public List<String> getRejectionReasons() {
        return rejectionReasons;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== [").append(strategyName).append("] ===\n");
        sb.append("Selected Route: ").append(selectedRoute != null ? selectedRoute.id() : "None").append("\n");
        sb.append("Reason: ").append(selectionReason).append("\n");
        if (rejectionReasons != null && !rejectionReasons.isEmpty()) {
            sb.append("Rejections:\n");
            for (String reason : rejectionReasons) {
                sb.append(" - ").append(reason).append("\n");
            }
        }
        return sb.toString();
    }
}