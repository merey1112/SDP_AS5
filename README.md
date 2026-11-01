# Smart Delivery & Route Optimization Engine (Strategy Pattern)

## Overview
This project implements the Strategy Design Pattern for a delivery and route optimization engine. It allows dynamic selection and execution of different route calculation algorithms based on business needs and delivery constraints.

## Answers to Defense Questions

1. **What problem does Strategy solve in this project?**
   It eliminates monolithic conditional branching (`if/else` or `switch` statements) when choosing route calculation algorithms. It enables open extension of algorithms without modifying existing client/context code.

2. **Where is the Context?**
   `com.delivery.context.RoutePlanner` is the Context. It holds a reference to a `RouteStrategy` interface and delegates execution to it.

3. **Where is the Strategy interface?**
   `com.delivery.strategy.RouteStrategy`.

4. **What are the Concrete Strategies?**
   Located in `com.delivery.strategy.impl`:
   - `FastestRouteStrategy`
   - `CheapestRouteStrategy`
   - `EcoFriendlyRouteStrategy`
   - `BalancedRouteStrategy`
   - `PremiumRouteStrategy` (added to demonstrate Open-Closed Principle)

5. **Where is runtime strategy switching demonstrated?**
   In `Main.java` using `planner.setStrategy(...)` and tested in `SmartDeliveryTest#testRuntimeStrategySwitching`.

6. **Why is this Strategy rather than simple if/else?**
   With Strategy, each algorithm is isolated in its own class (Single Responsibility Principle). Adding a new strategy requires zero modifications to existing algorithm classes or the `RoutePlanner` context (Open-Closed Principle).

7. **How can a new Strategy be added?**
   Implement `RouteStrategy`, write the `selectRoute()` logic, and pass an instance to `RoutePlanner.setStrategy(...)`. No existing strategy classes need to be edited.

8. **What is the difference between Strategy and Bridge?**
   - **Strategy** is a behavioral pattern used to swap interchangeable algorithms at runtime for a single operation.
   - **Bridge** is a structural pattern used to decouple an abstraction from its implementation so that both can vary independently.

9. **Which SOLID principles are demonstrated?**
   - **SRP:** Route planning, strategy algorithms, and strategy selection are isolated into distinct classes.
   - **OCP:** New strategies can be introduced without altering `RoutePlanner` or other strategies.
   - **DIP:** `RoutePlanner` depends on the `RouteStrategy` abstraction, not concrete implementations.

10. **Architecture Limitations:**
    Route evaluation is currently done sequentially in memory ($O(N)$ for $N$ candidate routes). For large real-time datasets (thousands of candidate routes), graph-based shortest path preprocessing (e.g., Dijkstra/A*) would be necessary.