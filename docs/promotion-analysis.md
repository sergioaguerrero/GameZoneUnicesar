# Promotion Module — Design Analysis

## 1. Shared hierarchy and the mechanism that lets each promotion type calculate its own discount

The three promotion types share the same identity and validity-period
attributes (`id`, `name`, `startDate`, `endDate`) and the same behavior for
checking whether a promotion is currently applicable (`isActive(LocalDate)`).
This is reflected by putting all of that state and behavior in a single
abstract base class, `Promotion`, and having `PercentageDiscount`,
`CategoryDiscount`, and `BulkPurchaseDiscount` extend it. Each subclass then
only adds the attributes specific to its own calculation rule (`percentage`;
`percentage` + `targetCategory`; `minimumQuantity` + `percentage`).

The mechanism that lets each type calculate its discount differently without
the rest of the system knowing the concrete type is **polymorphism through
an abstract method**: `Promotion` declares
`public abstract double calculateDiscount(Sale sale)`, and every concrete
subclass provides its own implementation. Any code that holds a `Promotion`
reference — `PromotionService`, and later `SaleService` — can call
`promotion.calculateDiscount(sale)` and the JVM dispatches to the correct
subclass implementation at runtime. This is exactly the same pattern already
used by `Product` and `getFullDescription()` for video games, consoles and
accessories, so it keeps the codebase consistent.

## 2. Why `calculateDiscount` is declared abstract in the base class

`Promotion` cannot provide a meaningful default implementation of
`calculateDiscount` because the three calculation rules are structurally
different: a flat percentage of the whole sale, a percentage of only the
items in one category, and a percentage that only applies past a quantity
threshold. There is no shared computation to factor out — only the shared
*contract* ("every promotion can be asked, for a given sale, how much
discount it grants").

Declaring the method `abstract` in `Promotion`:

- **Forces** every concrete subclass to provide an implementation — the code
  does not compile otherwise. This is a compiler-enforced guarantee, not a
  convention the team has to remember.
- **Documents the contract** in one place: the return value is always a
  discount amount in pesos, for a specific sale, and the base class's
  Javadoc on the abstract method is the single source of truth for what
  every implementation must honor (e.g. "never negative").
- **Enables polymorphic use** from client code: `PromotionService` (and
  later `SaleService`) can treat a `List<Promotion>` uniformly and call
  `calculateDiscount` on each element without an `instanceof` chain,
  because the compiler already guarantees the method exists on every
  concrete subtype.

## 3. Where the "pick the best promotion" logic belongs

This selection logic must live in **`PromotionService`**, as a method
conceptually named `findBestPromotionFor(Sale sale)` (declared in the
Requirement 2 specification, but **not yet implemented** in the current
`PromotionService` — only `registerBulk`, `registerCategory`,
`registerPercentage`, `listAllPromotions`, `findById` and `deletePromotion`
exist today; `listActivePromotions()` and `findBestPromotionFor(Sale)` still
need to be added before this can be wired into `SaleService`).

This is coherent with the layered architecture (`ui -> service ->
persistence -> model`) for two concrete reasons:

- **It needs the full promotion catalog.** Choosing "the promotion that
  grants the largest discount" requires iterating every currently active
  `Promotion` and comparing the result of `calculateDiscount(sale)` across
  all of them. Only `PromotionService` holds that in-memory collection
  (backed by `PromotionRepository`); neither `Sale` nor `ConsoleMenu` has —
  or should have — access to "every promotion in the system."
- **It is a business rule, not data or presentation.** "Only one promotion
  applies, and it must be the most favorable one" is a store policy, the
  same category of rule as "a sale needs at least one item" or "stock must
  be validated before a sale is registered" — both of which already live in
  `Sale.register()`/`SaleService.registerSale()` rather than in
  `ConsoleMenu`.

It must **not** be in `Sale`, because `Sale` is a model class that
represents a single transaction's data (date, customer, seller, items) — it
has no reference to a promotion catalog, and giving it one would mean the
model layer reaching sideways into a repository-backed collection, which
breaks the model layer's role as passive data holder (the same reason model
classes are forbidden from doing file I/O).

It must **not** be in `ConsoleMenu`, because that would duplicate a business
rule inside the UI layer, coupling console-specific code to logic that has
nothing to do with reading input or printing output. It would also make the
rule impossible to reuse or unit-test independently of the console, and it
would violate the established `ui -> service` dependency direction by
making the UI responsible for a decision the service layer exists to make.

## 4. Changes needed in `Sale` and `generateReceipt` for the receipt to show the discount

**New state on `Sale`:** two additive private fields, `appliedPromotionName`
(`String`, nullable/absent when no promotion applied) and `discountAmount`
(`double`, defaulting to `0.0`), each with a standard getter/setter pair.
`SaleService.registerSale` will set both after finding the best applicable
promotion.

**`calculateTotal()` must not change.** This is the one point where a naive
implementation would silently break existing behavior: `calculateTotal()`
is not just used for display — every `calculateDiscount(Sale)`
implementation (`PercentageDiscount`, `BulkPurchaseDiscount`, and
`CategoryDiscount`'s per-item subtotal) uses `sale.calculateTotal()` as the
**pre-discount subtotal** to compute its own percentage from. If
`calculateTotal()` were modified to subtract the discount, promotions would
end up calculating their discount from an already-discounted amount,
corrupting the result. `calculateTotal()` must keep meaning exactly what it
means today: the raw sum of item subtotals.

The "subtotal minus discount" final amount the requirement asks for should
instead be a small additional read (either a new method such as
`calculateFinalTotal()` returning `calculateTotal() - discountAmount`, or
computed inline where the receipt is built) — a separate concern from the
subtotal `calculateTotal()` already provides.

**`generateReceipt()` does not exist yet in this codebase** — currently the
equivalent formatting logic lives in `ConsoleMenu.formatSaleReceipt(Sale)`,
in the UI layer. Since the requirement explicitly calls for a
`generateReceipt` method and `Sale` already owns every piece of data the
receipt needs (customer, seller, items, and now the promotion fields), the
Technical Lead's integration work should **add** `public String
generateReceipt()` to `Sale` itself, producing the subtotal, the discount
line (promotion name and amount, only when `discountAmount > 0`), and the
final total — then have `ConsoleMenu` call `sale.generateReceipt()` instead
of duplicating that formatting. This is additive: it introduces a new
method and two new fields without touching `calculateTotal()`,
`register()`, `addItem()`, or any existing getter/setter, so no existing
behavior (including the already-working accessory sales from Requirement 1)
is affected.

## 5. Where "is this promotion valid today" is validated

This validation happens in **both classes, each with a distinct role**:

- **`Promotion.isActive(LocalDate date)`** (already implemented) is where
  the actual comparison lives: `!date.isBefore(startDate) &&
  !date.isAfter(endDate)`. This belongs on the model because validity is an
  intrinsic property of a single promotion instance — it only needs that
  instance's own two dates and the date being checked, nothing external.
  Keeping it here also means the rule is defined exactly once and reused
  everywhere a `Promotion` needs to answer "am I active on this date,"
  rather than every caller re-implementing the date comparison.
- **`PromotionService`** is where that per-instance check gets *applied
  across the collection*: `listActivePromotions()` should filter
  `promotions` by calling `p.isActive(LocalDate.now())` on each one, and
  `findBestPromotionFor(Sale)` should do the same before comparing
  discounts. The service does not re-implement the date-range logic — it
  delegates to `isActive()` — but it is the only place that knows about
  "all promotions" and can therefore decide which subset is active *right
  now* and, among those, which one wins for a given sale.

This split mirrors the same reasoning as question 3: a single object
validating a fact about itself belongs on that object; deciding something
about a collection of objects belongs in the service that owns the
collection.