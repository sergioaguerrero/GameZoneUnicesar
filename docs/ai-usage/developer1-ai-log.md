AI Consultation Log — Developer 1 (Product Module)

Student: Isaac David Mattos Botello Student ID: 1042854691 Role: Developer 1 — Product Module Tool used: Claude (Anthropic)

This log documents each significant consultation made during the development of the product module, following the structure required by the assignment: problem raised → suggested solution → key lesson.

Entry 1 — Compiler error after extending an abstract class

Problem raised: After creating VideoGame extends Product, NetBeans reported: "VideoGame is not abstract and does not override abstract method getFullDescription() in Product."

Suggested solution: Explained that the error is expected — a concrete subclass must implement every abstract method inherited from its parent. The fix was adding a properly annotated @Override implementation of getFullDescription() in VideoGame and, later, in Console.

Key lesson: The Java compiler actively enforces the "contract" established by an abstract method — it will not compile a concrete class that leaves an inherited abstract method unimplemented. This reinforced why the design decision in Entry 1 is safe: it's impossible to accidentally forget to implement the description logic.

Entry 2 — Layered architecture and file-based persistence

Problem raised: How to persist a list of products (a polymorphic list containing both VideoGame and Console) to disk, while keeping ProductService completely unaware of how the data is stored.

Suggested solution: Introduce ProductRepository as the only class allowed to read/write files, exposing just two public methods (saveAll(List<Product>) and loadAll()), which ProductService calls without knowing the underlying storage format.

Key lesson: Separating persistence from business logic means the storage format can change (see Entry 4) without touching the service or model layers at all — a direct, practical demonstration of why the assignment requires a layered architecture with restricted dependencies (service → persistence, never ui → persistence).

Entry 3 — Switching from serialization to CSV

Problem raised: After comparing the initial implementation (Java serialization with ObjectOutputStream/ObjectInputStream) against a classmate's CSV-based approach, decided to switch ProductRepository to CSV for a human-readable, inspectable data file.

Suggested solution: Rewrite saveAll/loadAll using BufferedWriter/BufferedReader, and add two private helper methods, toCsvLine and fromCsvLine, using a discriminator column (VIDEOGAME/CONSOLE) and Java's pattern-matching instanceof to identify each product's concrete type:

java
if (product instanceof VideoGame videoGame) { ... }

Key lesson: Confirmed, in practice, the value of the layered design from Entry 3 — switching the entire persistence mechanism only required changes inside ProductRepository. Product, VideoGame, Console, and ProductService needed no modification at all (beyond an optional cleanup: removing the now-unnecessary Serializable interface from Product).

Entry 4 — Git: resolving a "detached HEAD" state

Problem raised: After checking out a remote tracking branch in Git GUI (origin/develop, later origin/feature/product-module), got the message "You are no longer on a local branch." multiple times during the workflow.

Suggested solution: Explained that this is the "detached HEAD" state — positioned exactly on the remote commit, but without a local branch name pointing to it. The fix: Branch > Create Branch, using "Revision Expression: HEAD" (and "Reset" when the local branch name already existed) to create a proper local branch at that exact commit.

Key lesson: Understood the difference between a commit (a point in history) and a branch (a movable pointer to a commit) — checking out a specific commit by itself doesn't give you a branch to work from unless you explicitly create one.

Entry 5 — Merging team changes without losing local work

Problem raised: Needed to bring a teammate's completed work (person module classes, updated .gitignore) from develop into the feature/product-module branch, without discarding uncommitted local changes.

Suggested solution: A two-step merge: first fast-forward the local develop branch to match origin/develop (Fetch + Merge), then merge that updated develop into feature/product-module (Merge > Local Merge). Emphasized committing and pushing all pending local work before switching branches.

Key lesson: Integration in a team project flows through the shared branch (develop), not directly between feature branches — this matches the Git Flow model required by the assignment, where all feature/* branches derive from and reintegrate through develop.

Entry 6 — Diagnosing a Maven/JDK version mismatch

Problem raised: Clean and Build failed with error: invalid target release: 26, unrelated to any code written for this module.

Suggested solution: Identified the cause in pom.xml (maven.compiler.source/target set to 26, a version not matching the locally installed JDK), and proposed testing locally with a temporary downgrade to 25 — explicitly without committing that change, since pom.xml is a shared file owned by the Technical Lead.

Key lesson: Learned to distinguish between a code error and a build/environment configuration error, and the importance of not modifying shared configuration files unilaterally in a team Git workflow — instead communicating the issue to the responsible teammate.

