# Forensic Audit Report — Milestone 5 (Apex Transport UI)

**Work Product**: `d:\Apex Transport learnathon\Apex Transport\src\main\resources\static\login.html` & `target\classes\static\login.html`  
**Profile**: General Project / Integrity Forensics  
**Verdict**: **CLEAN**

---

## 1. Observation

Direct observations and evidence gathered:

1. **Target Files Existence & Line Count**:
   - `d:\Apex Transport learnathon\Apex Transport\src\main\resources\static\login.html`: 980 lines, 32,972 bytes.
   - `d:\Apex Transport learnathon\Apex Transport\target\classes\static\login.html`: 980 lines, 32,972 bytes.

2. **File Synchronization**:
   - Executed `fc.exe /b "d:\Apex Transport learnathon\Apex Transport\src\main\resources\static\login.html" "d:\Apex Transport learnathon\Apex Transport\target\classes\static\login.html"`
   - Output: `Comparing files ... FC: no differences encountered`. Both copies are binary identical and synchronized.

3. **Background Image Asset Verification**:
   - `slice1-truck.jpg`: Present in `src/.../images/` and `target/.../images/` (Size: 194,894 bytes). Referenced at line 62: `background-image: url('/images/slice1-truck.jpg');`.
   - `slice2-driver.jpg`: Present in `src/.../images/` and `target/.../images/` (Size: 342,537 bytes). Referenced at line 77: `background-image: url('/images/slice2-driver.jpg');`.
   - `slice3-pickup.jpg`: Present in `src/.../images/` and `target/.../images/` (Size: 217,234 bytes). Referenced at line 92: `background-image: url('/images/slice3-pickup.jpg');`.

4. **UI Layout and Component Audit against ORIGINAL_REQUEST.md**:
   - **Left-Side Hero (60% width)**: `.apx-hero-col` defined with `width: 60%`, containing diagonal clip-path slice overlays (`.apx-slice-1`, `.apx-slice-2`, `.apx-slice-3`), dark gradient overlay (`.apx-dark-overlay`), AI Powered pill badge (`.apx-badge`), white/green headline ("Moving India. Powering Progress."), subheading, 4 feature items with dark circular icon containers & green SVGs, and copyright footer.
   - **Right-Side Floating Card (~35% width, ~83% height)**: `.apx-card-col` positioned at `left: 61.5%`, `top: 8.5vh`, `right: 3.5%`, `width: 35%`, `height: 83vh`. Floating white card (`#ffffff`, border-radius 24px, subtle shadow) with background visible on all sides. Includes top-right segmented pill control (`[ ☼ Light | 🌐 EN ˅ ]`), green brand box, "Apex Transport" title, "NEXT-GEN AUTONOMOUS FREIGHT NETWORK" subtitle, email & password inputs with left SVG icons and eye toggle icon, remember me checkbox, forgot password link, full-width green submit button, divider, Google and GitHub social login buttons, and register mode toggle.

5. **Code Genuine Implementation Check**:
   - JavaScript contains interactive form handling (`handleManualLogin`, `handleRegister`, `switchAuthMode`, `togglePasswordVisibility`, `fillDemo`, `redirectRole`) calling `api.login()` and `api.register()`. No hardcoded test shortcuts, fake attestation strings, or stub returns found.

---

## 2. Logic Chain

1. **Completeness & Authenticity**:
   - Observation 1 & 4 confirm that `login.html` is a fully written, comprehensive single-page HTML file (980 lines) implementing the exact layout, CSS styling, components, and responsive grid required by `ORIGINAL_REQUEST.md`. No placeholder HTML comments or stub CSS classes exist.
2. **Asset Integrity**:
   - Observation 3 confirms that all three specified photographic slices (`slice1-truck.jpg`, `slice2-driver.jpg`, `slice3-pickup.jpg`) exist in the file system with non-zero file sizes and are authentically integrated into CSS background properties with diagonal polygon clipping paths.
3. **Deployment Synchronization**:
   - Observation 2 proves through binary comparison (`fc.exe /b`) that the source file (`src/main/resources/static/login.html`) and the Spring Boot compiled output target file (`target/classes/static/login.html`) are 100% synchronized with zero diffs.
4. **Prohibited Pattern Verification**:
   - Observation 5 confirms that the script logic interacts with live REST APIs and real DOM elements. There are no hardcoded pass/fail assertions, fake attestation tokens, or shortcut scripts.

---

## 3. Caveats

- Functional testing of Spring Boot HTTP endpoint serving was verified via static file deployment analysis and binary identity between source and target build directories; live HTTP browser rendering was not executed as UI components and CSS clip-paths are statically verified.

---

## 4. Conclusion

The work product `login.html` for Milestone 5 passes all integrity forensics checks with zero violations detected.

**Final Verdict**: **CLEAN**

---

## 5. Verification Method

To independently verify this audit:

1. **File Synchronization Check**:
   ```powershell
   fc.exe /b "d:\Apex Transport learnathon\Apex Transport\src\main\resources\static\login.html" "d:\Apex Transport learnathon\Apex Transport\target\classes\static\login.html"
   ```
   *Expected result*: `FC: no differences encountered`

2. **Image Asset Existence & Size Verification**:
   ```powershell
   Get-ChildItem "d:\Apex Transport learnathon\Apex Transport\src\main\resources\static\images\slice*.jpg" | Select-Object Name, Length
   ```
   *Expected result*: `slice1-truck.jpg` (194,894), `slice2-driver.jpg` (342,537), `slice3-pickup.jpg` (217,234).

3. **CSS Slice Reference Inspection**:
   ```powershell
   Select-String -Path "d:\Apex Transport learnathon\Apex Transport\src\main\resources\static\login.html" -Pattern "slice[1-3]-.*\.jpg"
   ```
   *Expected result*: 3 matching `background-image: url(...)` lines.
