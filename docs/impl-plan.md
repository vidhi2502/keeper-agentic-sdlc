# Implementation Plan

| Task ID | Description | Priority | Depends On | Requirement IDs | Status |
|---|---|---|---|---|---|
| T1 [all] | Spring Boot project setup: create the Maven project with Java 17, `spring-boot-starter-web`, and `spring-boot-starter-test`; add baseline ignore rules for local secret files and commit no secret values. | P0 | — | NFR-1, NFR-5, NFR-12 | Not Started |
| T2 [KEEP-3, KEEP-4] | Task model, thread-safe insertion-ordered in-memory repository, and service: implement create/list/complete/uncomplete/delete operations; trim Unicode whitespace, enforce the 100-code-point title limit, preserve order, and return immutable task values/snapshots. | P0 | T1 | FR-1, FR-2, FR-3, FR-4, FR-5, FR-6, NFR-3, NFR-6, NFR-11 | Not Started |
| T3 [KEEP-3, KEEP-4] | Task REST controller and error handler: expose the documented `/api/tasks` routes, JSON request/response shapes, 400 invalid-title errors, 404 unknown-task errors, and the consistent error envelope. | P0 | T2 | FR-3, FR-4, FR-6, NFR-2, NFR-4, NFR-9 | Not Started |
| T4 [KEEP-3, KEEP-4, KEEP-5] | Frontend (`index.html`, `style.css`, `app.js`): implement add, ordered list, complete/uncomplete, delete, and All/Active/Done filters; reapply the current filter after completion updates; provide accessible, responsive controls and render dynamic text safely. | P0 | T3 | FR-1, FR-2, FR-3, FR-5, FR-7, FR-8, FR-9, NFR-1, NFR-4, NFR-7, NFR-8, NFR-9, NFR-10 | Not Started |
| T5 [all] | Tests: add JUnit 5 service tests and MockMvc tests for validation, CRUD, ordering, concurrency safety, and error responses; perform a browser-level text-rendering check and verify the stated response-time, UI-update, and startup targets. | P1 | T2, T3, T4 | FR-1, FR-2, FR-3, FR-4, FR-5, FR-6, FR-7, FR-8, FR-9, NFR-3, NFR-4, NFR-5, NFR-6, NFR-7, NFR-8, NFR-9, NFR-10 | Not Started |
| T6 [all] | README: document prerequisites, run/build/test commands, API routes, task behavior, in-memory reset on restart, supported limits, and secret-safe configuration practices. | P2 | T1, T3, T4, T5 | FR-4, NFR-1, NFR-11, NFR-12 | Not Started |

All functional requirements FR-1 through FR-9 are covered by T2, T3, T4, and T5.

## Blocked Tasks

T2 is blocked by T1.  
T3 is blocked by T2.  
T4 is blocked by T3.  
T5 is blocked by T2, T3, and T4.  
T6 is blocked by T1, T3, T4, and T5.

## Suggested Order of Execution

T1 → T2 → T3 → T4 → T5 → T6
