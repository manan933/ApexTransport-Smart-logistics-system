# Execution Plan — Apex Transport UI Project

## Objective
Recreate the Apex Transport login page as specified in `ORIGINAL_REQUEST.md`, including cinematic 3-photo background collage with sloped transitions, floating white login card with specific positioning, branding, inputs, icons, controls, and deployment to `src/main/resources/static/login.html` and `target/classes/static/login.html`.

## Milestones

### Milestone 1: Survey & Codebase Investigation
- Dispatch Explorers (`teamwork_preview_explorer` / `teamwork_preview_spec_miner`) to check existing files, images (`slice1-truck.jpg`, `slice2-driver.jpg`, `slice3-pickup.jpg`), Spring Boot directory structure, static resource paths, and build configuration.

### Milestone 2: Background Collage & CSS Styling Setup
- Dispatch Worker (`teamwork_preview_worker`) to prepare assets/styles and verify image paths and sloped diagonal layout for the left-side cinematic background overlay.

### Milestone 3: Full HTML/CSS Implementation of Apex Transport UI
- Dispatch Worker (`teamwork_preview_worker`) to generate/update `login.html` meeting all specs (left 60% cinematic background + text + 4 features; right 35-37% floating login card with specific dimensions, inputs, icons, buttons).

### Milestone 4: Review, Testing & Verification
- Dispatch Reviewers (`teamwork_preview_reviewer`) and Challengers (`teamwork_preview_challenger`) to verify HTML layout, responsiveness, visual hierarchy, exact positioning parameters, and build/run capability.

### Milestone 5: Forensic Integrity Audit & Target Deployment
- Dispatch Forensic Auditor (`teamwork_preview_auditor`) to perform integrity verification.
- Deploy to both `src/main/resources/static/login.html` and `target/classes/static/login.html` and verify serving/copying.
- Gate approval & victory report to Sentinel.
