# Project: Apex Transport UI

## Architecture
- Static Web Page served by Spring Boot application
- File paths:
  - Source: `d:\Apex Transport learnathon\Apex Transport\src\main\resources\static\login.html`
  - Target: `d:\Apex Transport learnathon\Apex Transport\target\classes\static\login.html`
  - Images: `slice1-truck.jpg`, `slice2-driver.jpg`, `slice3-pickup.jpg` (to be discovered/verified in static assets)

## Feature Inventory
| # | Feature | Description | Milestone | Source |
|---|---------|-------------|-----------|--------|
| 1 | Survey & Asset Discovery | Locate reference images, existing static files, Spring Boot setup | M1 | ORIGINAL_REQUEST §23-26 |
| 2 | Cinematic Left Collage | 60% width, 3-photo collage with diagonal sloped transitions, dark overlay | M2 | ORIGINAL_REQUEST §14, 23-32 |
| 3 | Left Side Branding & Features | Pill badge, headline, subheading, 4 dark circular features with green icons, copyright | M3 | ORIGINAL_REQUEST §34-43 |
| 4 | Right Side Floating Login Card | White card (width ~36%, height ~78%, rounded 24px, subtle shadow, specific positioning) | M3 | ORIGINAL_REQUEST §17-20, 45-47, 58-59 |
| 5 | Login Card Controls & Header | Light/Lang top-right controls, green rounded logo box, truck icon, Apex Transport branding | M3 | ORIGINAL_REQUEST §48-50 |
| 6 | Form Inputs & Actions | Email field + icon, Password field + lock & eye icons, Remember/Forgot row, Sign In button | M3 | ORIGINAL_REQUEST §51-53 |
| 7 | Alternative Auth & Footer | Social login buttons (Google & GitHub), Create account link | M3 | ORIGINAL_REQUEST §54-56 |
| 8 | Verification & Review | Build check, layout verification, visual check, challenger verification | M4 | System Requirement |
| 9 | Audit & Dual Deployment | Integrity audit, deployment to src/.../login.html and target/.../login.html | M5 | ORIGINAL_REQUEST §12, 60 |

## Milestones
| # | Name | Scope | Dependencies | Status |
|---|------|-------|-------------|--------|
| 1 | Survey & Discovery | Codebase & image asset discovery | none | DONE |
| 2 | Background Collage & Assets | Prepare image layout & CSS diagonal slopes | M1 | DONE |
| 3 | HTML/CSS Implementation | Build complete login.html per spec | M2 | DONE |
| 4 | Verification & Review | Code review, layout validation, testing | M3 | DONE |
| 5 | Audit & Deployment | Forensic audit, dual directory deployment | M4 | DONE |

## Code Layout
- `src/main/resources/static/login.html`
- `target/classes/static/login.html`
- `src/main/resources/static/images/` (or relative path where images exist)
