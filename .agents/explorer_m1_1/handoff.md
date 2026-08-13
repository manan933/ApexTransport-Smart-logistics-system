# Handoff Report: Specification Mining & Static Asset Discovery for Milestone 1

## 1. Observation

### 1.1 Static Image Inventory & Locations
- **`slice1-truck.jpg`**
  - Path: `src/main/resources/static/images/slice1-truck.jpg` and `target/classes/static/images/slice1-truck.jpg`
  - Size: 194,894 bytes
  - Last modified: 2026-08-10 16:42:17
  - Subject: Decorated Tata truck on mountain road (PHOTO 3)
- **`slice2-driver.jpg`**
  - Path: `src/main/resources/static/images/slice2-driver.jpg` and `target/classes/static/images/slice2-driver.jpg`
  - Size: 342,537 bytes
  - Last modified: 2026-08-10 16:42:17
  - Subject: Indian truck driver reading newspaper (PHOTO 2)
- **`slice3-pickup.jpg`**
  - Path: `src/main/resources/static/images/slice3-pickup.jpg` and `target/classes/static/images/slice3-pickup.jpg`
  - Size: 217,234 bytes
  - Last modified: 2026-08-10 16:42:17
  - Subject: Mahindra pickup truck in green field (PHOTO 1)
- **Additional Static Images in `src/main/resources/static/images/`**:
  - `driver-portrait.jpg` (205,472 bytes)
  - `hero-collage.jpg` (316,109 bytes)
  - `hero-truck.jpg` (194,894 bytes)
  - `mountain-highway.jpg` (170,706 bytes)
- **Root-level High-Res Photographic References**:
  - `1.jpg` (3,017,877 bytes, timestamp: 2026-08-06 00:36:41)
  - `2.jpg` (830,276 bytes, timestamp: 2026-08-06 00:37:21)
  - `3.jpg` (3,520,963 bytes, timestamp: 2026-08-10 16:35:13)

### 1.2 Existing Static HTML/CSS/JS Assets
- **`login.html`**:
  - Location: `src/main/resources/static/login.html` and `target/classes/static/login.html`
  - Size: 24,499 bytes (806 lines)
  - CSS Background References:
    - `.apx-slice-1`: `url('/images/slice1-truck.jpg')`
    - `.apx-slice-2`: `url('/images/slice2-driver.jpg')`
    - `.apx-slice-3`: `url('/images/slice3-pickup.jpg')`
  - Structure: Fullscreen container `.apx-viewport` with `.apx-slices-wrapper` background (3 diagonal sloped image slices via CSS `clip-path`) and `.apx-main-grid` (left hero content + right floating white card `.apx-login-card`).
- **Other Static Assets in `src/main/resources/static/`**:
  - `index.html`, `admin.html`, `driver.html`, `transporter.html`, `profile.html`
  - `css/main.css`
  - `js/api.js`, `js/audio-alerts.js`, `js/i18n.js`, `js/theme.js`
- Target folder `target/classes/static/` is populated with all 21 static files.

### 1.3 Spring Boot Configuration (`pom.xml`, Java Config, properties)
- **`pom.xml`**:
  - Parent: `org.springframework.boot:spring-boot-starter-parent:3.5.11`
  - Java version: 17
  - Packaging: `jar`
  - Dependencies: `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`, `postgresql`, `h2`, `spring-security-crypto`, `firebase-admin`, `spring-boot-devtools`
  - Plugin: `spring-boot-maven-plugin`
- **`application.properties`**:
  - `spring.application.name=Apex Transport`
  - `server.port=${PORT:8080}`
  - Datasource configured for PostgreSQL with H2 in-memory fallback.
- **`WebConfig.java`**:
  - Maps `/**` to `classpath:/static/` with `PathResourceResolver`.
  - Configures no-cache response headers: `CacheControl.noCache().mustRevalidate()`.
  - External `/uploads/**` handling via filesystem `uploads/`.
- **`SpaForwardingController.java`**:
  - `@GetMapping("/")` forwards to `forward:/login.html`.
  - `@GetMapping("/login")` forwards to `forward:/login.html`.

---

## 2. Logic Chain

1. **Image Discovery & Verification**:
   - The user requirement specifies three images: `slice1-truck.jpg`, `slice2-driver.jpg`, and `slice3-pickup.jpg`.
   - Inspection of `src/main/resources/static/images/` and `target/classes/static/images/` confirms all three files exist in both locations with sizes 194.8KB, 342.5KB, and 217.2KB respectively.
   - Root level high-res images (`1.jpg`, `2.jpg`, `3.jpg`) exist as source assets, but the application specifically serves the sliced image files from `/images/`.

2. **Static Asset Serving Architecture**:
   - Spring Boot web starter automatically serves static resources located under `classpath:/static/`.
   - `WebConfig.java` explicitly confirms that requests to `/**` resolve against `classpath:/static/`.
   - `SpaForwardingController.java` maps root route `/` and `/login` directly to `login.html`.
   - Browsers accessing `http://localhost:8080/` or `http://localhost:8080/login` receive `login.html` and fetch `/images/slice1-truck.jpg`, `/images/slice2-driver.jpg`, and `/images/slice3-pickup.jpg`.

3. **Build & Target Synchronization Requirement**:
   - When running the Spring Boot application, Maven packages files from `src/main/resources/` into `target/classes/`.
   - If the application is running from compiled classes in `target/classes/`, modifying `src/main/resources/static/login.html` alone may not immediately take effect until mirrored to `target/classes/static/login.html` or rebuilt. Both locations currently contain matching files.

---

## 3. Specification Findings (Features Discovered & Edge Cases)

## Features Discovered
| # | Category | Feature | Description | Inputs | Outputs | Error Behavior | Discovered Via |
|---|----------|---------|-------------|--------|---------|----------------|----------------|
| 1 | Static Assets | Image Asset - Slice 1 | Tata truck on mountain road image (`slice1-truck.jpg`) | HTTP GET `/images/slice1-truck.jpg` | JPEG image data (194,894 bytes) | 404 Not Found if missing | Directory inspection & `login.html` CSS line 60 |
| 2 | Static Assets | Image Asset - Slice 2 | Indian truck driver reading newspaper (`slice2-driver.jpg`) | HTTP GET `/images/slice2-driver.jpg` | JPEG image data (342,537 bytes) | 404 Not Found if missing | Directory inspection & `login.html` CSS line 74 |
| 3 | Static Assets | Image Asset - Slice 3 | Mahindra pickup truck in green field (`slice3-pickup.jpg`) | HTTP GET `/images/slice3-pickup.jpg` | JPEG image data (217,234 bytes) | 404 Not Found if missing | Directory inspection & `login.html` CSS line 89 |
| 4 | Routing | Root & Login Forwarding | `/` and `/login` routes forwarded to `login.html` | HTTP GET `/` or `/login` | `login.html` content | 404/500 if file missing or controller error | `SpaForwardingController.java` lines 9-17 |
| 5 | Static Asset Serving | Spring MVC Static Resource Handler | Maps `/**` to `classpath:/static/` with no-cache header | HTTP GET `/*` | Static file from `classpath:/static/` or SPA fallback `index.html` | Fallback to `index.html` for unknown routes | `WebConfig.java` lines 50-67 |
| 6 | UI Layout | Cinematic 3-Slice Background | 3 sloped photographic sections created with CSS `clip-path` | Viewport render | 3 diagonal photography regions with dark overlay | Visual misalignment if clip paths or image paths fail | `login.html` lines 42-107 |
| 7 | UI Layout | Right Floating Login Card | Floating white card occupying ~36% width, 78% height | User interaction | Sign in form, social logins, mode switch | Validation errors via API or toast alerts | `login.html` lines 240-522 |

## Edge Cases
| # | Feature | Input | Observed Behavior |
|---|---------|-------|-------------------|
| 1 | Static Asset Updates | File changed only in `src/main/resources/static/login.html` without target copy or devtools restart | Running Spring Boot app serves stale file from `target/classes/static/login.html` |
| 2 | Image Asset Resolution | Requesting relative path `images/slice1-truck.jpg` vs `/images/slice1-truck.jpg` | Spring Boot handles both if requested from root path, but `/images/...` absolute path is safer for nested SPA routes |
| 3 | SPA Route Fallback | Non-existent static resource request (e.g. `/unknown-page`) | `WebConfig.java` PathResourceResolver falls back to `/static/index.html` |

---

## 4. Caveats
- No code execution or server launch was required as this task is read-only spec discovery.
- Root high-res images (`1.jpg`, `2.jpg`, `3.jpg`) are stored in project root; the application relies on resized/processed slices in `/static/images/`.

---

## 5. Conclusion
All required static images (`slice1-truck.jpg`, `slice2-driver.jpg`, `slice3-pickup.jpg`) exist and are properly located in `src/main/resources/static/images/` and `target/classes/static/images/`. The existing `login.html` landing page in `src/main/resources/static/login.html` and `target/classes/static/login.html` correctly integrates these images via CSS clip-path sloped slices. Spring Boot serves these static assets directly from `classpath:/static/` via `WebConfig.java` and forwards `/` and `/login` routes to `login.html` via `SpaForwardingController.java`.

---

## 6. Verification Method

To verify these findings:
1. Verify presence of images:
   - Check `src/main/resources/static/images/slice1-truck.jpg`
   - Check `src/main/resources/static/images/slice2-driver.jpg`
   - Check `src/main/resources/static/images/slice3-pickup.jpg`
   - Check corresponding files in `target/classes/static/images/`
2. Verify HTML references:
   - Inspect lines 60, 74, 89 in `src/main/resources/static/login.html`
3. Verify Spring Boot config:
   - Inspect `WebConfig.java` (resource handlers)
   - Inspect `SpaForwardingController.java` (forwarding rules)
