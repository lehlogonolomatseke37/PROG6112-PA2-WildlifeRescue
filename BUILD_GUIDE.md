# Wildlife Rescue Operations System — Build Guide

PROG6112 Practical Assignment 2. A console app, built with Maven, JUnit 5, and Java 17 or newer.

---

## 1. Open and run it in NetBeans

| Step | Action |
|---|---|
| 1 | Unzip `WildlifeRescueSystem.zip`. |
| 2 | NetBeans → **File → Open Project** → select the `WildlifeRescueSystem` folder (it has the Maven icon). |
| 3 | Wait for "Resolving dependencies" to finish. NetBeans downloads JUnit, so you need internet the first time. |
| 4 | **Run → Run Project (F6)**. The menu appears in the Output window. Type there. |
| 5 | **Run → Test Project (Alt+F6)**. The Test Results window should show **29 tests passed**. |

If NetBeans asks for a main class, pick `wildliferescue.RescueApp`.

---

## 2. Build order — do it yourself in this order

Each file depends only on the files above it. If you rebuild from scratch in this order, it compiles at every step.

| # | File | What it is | Why it exists |
|---|---|---|---|
| 1 | `RescueStatus`, `RescuePriority`, `ConservationStatus` | **enums** | A fixed list of allowed values. A typo like `"In Progres"` can't sneak in, because the compiler rejects anything else. |
| 2 | `Validator` | static helper methods | Holds every "not blank" and "greater than zero" rule in one place. The model and the UI both call it, so the rules can't disagree. |
| 3 | `Money` | formatter | Prints `R20 500.00` exactly like the brief's example report. |
| 4 | `RescueOperations` | **interface** | A contract: any class that implements it *must* have `startRescue()`, `completeRescue()`, `generateSummary()`. |
| 5 | `RescueCase` | **abstract superclass** | Holds the 8 shared fields, validation, status logic and summary. It declares `calculateTotalCost()`, `determinePriority()` and `getRescueType()` as `abstract`, which forces every subclass to write its own version. |
| 6 | `InjuredAnimalRescue`, `OrphanedAnimalRescue`, `EndangeredSpeciesRescue` | **subclasses** (`extends RescueCase`) | Add their own fields and **override** the cost, priority and details methods. |
| 7 | `RescueManager` | the `ArrayList<RescueCase>` + rules | Add (rejects duplicate IDs), search, update status, totals and report. There's no `Scanner` in here, which is why it can be unit tested. |
| 8 | `RescueApp` | console UI + `main` | Menu, input loops and printing only. |
| 9 | 3 test classes | JUnit 5 | They prove the rules above work. |

---

## 3. Business rules (use these for your explanations)

**Base care cost** = Number of Rescue Days × Daily Care Cost. Every type builds on this.

| Rescue type | Total cost | Priority rule |
|---|---|---|
| Injured | base + vet cost **+ R5 000** if surgery | Surgery → Critical · vet ≥ R10 000 → High · else Medium |
| Orphaned | base + feeding cost **+ R2 500** if foster care | Age < 3 months → Critical · < 12 → High · else Medium |
| Endangered | base + security cost **+ R8 000** if specialist team | Critically Endangered *or* specialist team → Critical · Endangered → High · else Medium |

The brief doesn't define the priority rules, so these are design decisions. Be ready to justify them. For example: "younger orphans can't feed themselves, so they're more urgent."

**Status flow:** `Reported` → *Start* → `Rescue in Progress` → *Complete* → `Rescue Completed`.
You can't complete a rescue that was never started, and you can't restart a completed one. Option 3 → "Set Status Manually" also allows `Under Observation` and `In Rehabilitation`.

---

## 4. Rubric map

| Rubric line (marks) | Where it's earned |
|---|---|
| Rescue Case Management (20) | `RescueManager` (ArrayList, `addCase`, `findById`, `updateStatus`, `getAllCases`) + menu options 1–4. Search (option 2) shows full details plus the rescue summary |
| Inheritance & Case Types (30) | `RescueCase` (abstract) → 3 subclasses, each with its own fields, `calculateTotalCost()`, `determinePriority()`, `getDetails()` |
| Operations: Interface & Polymorphism (15) | `RescueOperations` implemented by `RescueCase`. `generateSummary()` calls the subclass cost and priority at runtime. The `RescueCase rescueCase = new InjuredAnimalRescue(...)` line and the loops in option 4 and the report are polymorphism in action |
| Reports & Validation (15) | `RescueManager.generateReport()` (every required field + total cases + total cost), `Validator`, and the `read...` loops in `RescueApp` |
| UI & Execution (10) | Menu matches the brief's example. Invalid input never crashes the app, it asks again |
| JUnit (10) | `RescueCostTest` (all 6 cost paths), `RescuePriorityTest` (every branch), `RescueManagerTest` (search, duplicates, status, summary, validation, report) |
| Design (OOP list) | inheritance ✔ abstraction ✔ interface ✔ encapsulation (all fields `private`, getters, read-only list) ✔ overriding (`@Override` everywhere) ✔ polymorphism ✔ |

---

## 5. Demo script (reproduces the brief's example report)

```
1 → 1 → WR101, Tembo, African Elephant, Kruger, S. Nkosi, 10, 500, Snare wound, 10500, Y
1 → 2 → WR102, Nandi, White Rhino, Hluhluwe, P. Mokoena, 10, 600, 8, 3000, Y
3 → WR101 → 1                       (start → Rescue in Progress)
3 → WR102 → 3 → 3                   (Under Observation)
5                                   → Total Rescue Cases: 2, Total Rescue Cost: R32 000.00
```
Then try to break it: menu option `9`, letters, blank ID, duplicate `wr101`, `0` days, `-5` cost, `maybe` for Y/N. Each one should print a message and ask again.

---

## 6. Concepts worth being able to explain

- **Abstract class vs interface.** `RescueCase` is abstract because it *shares code and fields*. `RescueOperations` is an interface because it's only a *promise of behaviour*. A class can extend one class but implement many interfaces.
- **Why the UI and the logic are separate.** JUnit can't type into a `Scanner`. Everything testable lives in `RescueManager` and the model classes. `RescueApp` is thin. This is the same reason your web apps keep database logic out of the page templates.
- **`getAllCases()` returns an unmodifiable list.** If it returned the real `ArrayList`, other code could call `.add()` and skip the duplicate-ID check. That's encapsulation protecting a rule.
- **Comparing doubles in tests.** `assertEquals(20500.0, cost, 0.001)`. Money stored as `double` can pick up tiny rounding errors, so tests allow a small tolerance. Real banking systems use `BigDecimal`.
- **Case-insensitive IDs.** `wr101` and `WR101` count as the same ID. Otherwise a user could create "duplicates" that look identical in a report.
