# Integration Analysis - GameZone Unicesar

This document details the integration adjustments (A1 to A7) implemented to unify the accessories, promotions, warranties, and returns modules into a single, cohesive system.

## A1 - Accessory Category Discount
* **Cause / Problem:** Requirement 2 limited the `CategoryDiscount` target strictly to "VIDEOGAME" and "CONSOLE". With the new accessory module, the store must be able to launch category promotions for accessories.
* **Applied Solution:** Modified `CategoryDiscount` to accept "ACCESSORY" as a target category and correctly recognize `Accessory` instances in `calculateDiscount()`. Updated `PromotionService.registerCategoryDiscount` to validate the three allowed categories, and added the accessory option in `ConsoleMenu`.

## A2 - Warranty Circular Dependency
* **Cause / Problem:** Requirement 4 introduced a circular dependency loop (`SaleService` -> `WarrantyService` -> `WarrantyRepository` -> `SaleService`) because the repository attempted to resolve `Sale` objects during data load.
* **Applied Solution:** Refactored `WarrantyRepository` to persist and load only the string identifiers (IDs) for sales and products. The object resolution responsibility was moved to `WarrantyService`, which now resolves `Sale` and `Product` references via constructor-injected repositories and services, breaking the cycle.

## A3 - Unified Sale Registration Flow
* **Cause / Problem:** Previous modules modified `SaleService.registerSale` independently. When integrated, the order of operations became critical (e.g., applying discounts before/after warranty costs).
* **Applied Solution:** Refactored `SaleService.registerSale` into a strict sequential flow:
    1. Item validation.
    2. Stock verification (products and accessories).
    3. Subtotal calculation.
    4. Best promotion lookup and discount application (on subtotal only).
    5. Warranty generation and cost calculation.
    6. Final total calculation.
    7. Inventory update delegation.
    8. Persistence.

## A4 - Accessory Return Stock
* **Cause / Problem:** Requirement 3 only invoked `ProductService.restoreStock`, meaning returned accessories did not restore their inventory levels.
* **Applied Solution:** Injected `AccessoryService` into `ReturnService`. Added a `restoreStock(String accessoryId, int quantity)` method in `AccessoryService` and configured `ReturnService` to delegate the stock restoration dynamically based on the returned item's type.

## A5 - Discounted Sale Refund
* **Cause / Problem:** `Return.calculateRefundAmount` summed the standard list prices. If the original sale had a discount, the system would refund more money than the customer actually paid.
* **Applied Solution:** Modified `Return.calculateRefundAmount` to compute a proportional refund per item based on the original sale's discount ratio: `price - (price * (discount / subtotal))`. Receipts were updated to display this proportional breakdown.

## A6 - Monthly Balance Report
* **Cause / Problem:** The previous monthly report calculated total sales without factoring in integrated discounts and extended warranty costs, and `generateMonthlyBalance` only returned the net value.
* **Applied Solution:** Added `calculateMonthlySales` and `calculateMonthlyReturns` to `ReturnService`. The total sales calculation now strictly utilizes the `calculateFinalTotal()` of each sale. The console menu was updated to display total sales, total returns, and the net balance separately.

## A7 - Warranty Cancellation on Console Return
* **Cause / Problem:** Returning a console left its associated warranty active in the system, which is an invalid business state.
* **Applied Solution:** Implemented `cancelWarranties(String productId, String saleId)` in `WarrantyService` to delete warranties tied to returned products and return the refundable cost (for extended warranties). `ReturnService.registerReturn` now invokes this method and adds the refunded warranty cost to the total return amount.