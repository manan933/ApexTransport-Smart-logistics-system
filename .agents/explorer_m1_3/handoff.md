# Milestone 1 — UI Design Specification & Handoff Report

**Agent**: Explorer 3  
**Target File**: `d:\Apex Transport learnathon\Apex Transport\.agents\explorer_m1_3\handoff.md`  
**Date**: 2026-08-10  
**Status**: Completed  

---

## 1. Observation

Direct specifications extracted from `d:\Apex Transport learnathon\Apex Transport\.agents\ORIGINAL_REQUEST.md`:

1. **Overall Layout**:
   - Desktop 16:9 full-screen landing page at `d:\Apex Transport learnathon\Apex Transport\src\main\resources\static\login.html` (and copied to `target/classes/static/login.html`).
   - Left Side (~60% width): Full-height cinematic logistics photography with overlay and structured branding/feature content.
   - Right Side (~35-37% width, 75-80% height): Large floating white login card.

2. **Left Side Content Requirements**:
   - **Pill text**: `"⚡ AI POWERED  •  SMART LOGISTICS"` in dark translucent glass pill (Lines 35).
   - **Headline**: `"Moving India."` (white) `"Powering Progress."` (Apex Green accent `#00A86B` / `#10B981`) (Line 36).
   - **Subheading**: `"AI-orchestrated freight network delivering intelligence, efficiency and trust."` (Line 37).
   - **4 Feature Items** with circular dark translucent containers & green icons (Lines 38-42):
     1. *AI Logistics Telematics* — Real-time insights & predictive freight intelligence
     2. *Secure & Compliant* — Enterprise-grade security with end-to-end encryption
     3. *Faster Deliveries* — Optimized routes, smarter dispatch, greater reliability
     4. *Nationwide Network* — Strong, scalable & autonomous freight infrastructure
   - **Copyright**: `"© 2025 Apex Transport. All rights reserved."` (Line 43).

3. **Login Card Requirements**:
   - Position: `61-63%` from left, `8-10%` from top, `3-5%` from right. Card height `75-80%`, card width `35-37%`. Card must float over background with clearance on top, bottom, right (Lines 18-20, 46, 58-59).
   - Style: Border radius `24px`, pure white background (`#FFFFFF`), subtle floating shadow (Line 47).
   - Top-Right Controls INSIDE card: `[ ☼ Light | 🌐 EN ˅ ]` in rounded segmented control pill (Line 48).
   - Branding: Rounded green logo box with white truck icon, `"Apex Transport"` title, `"NEXT-GEN AUTONOMOUS FREIGHT NETWORK"` subtitle (Line 49).
   - Welcome text: `"Welcome Back"` / `"Sign in to your workspace"` (Line 50).
   - Inputs: Email field with ✉ icon (`vikram@apextransport.com`), Password field with 🔒 icon & eye toggle icon (Line 51).
   - Form Row: `☐ Remember me` checkbox / `Forgot password?` link in green (Line 52).
   - Primary Action: Full-width green button `"Sign In to Workspace →"` (Line 53).
   - Divider: `"or continue with"` centered with side lines (Line 54).
   - Social Auth: Google & GitHub equal-width white buttons with SVG logos (Line 55).
   - Bottom Link: `"New to Apex Transport? Create an account"` (green link) (Line 56).

---

## 2. Logic Chain

From the observations above, we establish the complete design tokens, layout geometry, typography hierarchy, and SVG icon vector set required for code implementation.

### A. Viewport & Card Layout Geometry
- Viewport Aspect Ratio: `16:9` fixed height full-screen wrapper (`w-screen h-screen min-h-[700px] overflow-hidden flex relative`).
- Left Content Section: Occupies `w-[60%]` or `lg:w-[58%]`, `h-full`, positioned `relative z-10`, `flex flex-col justify-between p-12 lg:p-16`.
- Login Card Floating Container:
  - `position: absolute; left: 62%; top: 9%; right: 4%; width: 36%; height: 78%; min-height: 580px; max-width: 500px;`
  - `border-radius: 24px; background: #FFFFFF; z-index: 20;`
  - `box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25), 0 10px 15px -3px rgba(0, 0, 0, 0.1);`
  - `border: 1px solid rgba(226, 232, 240, 0.8);`
  - `padding: 2.25rem 2.5rem; display: flex; flex-direction: column; justify-content: space-between;`

### B. Color Tokens Palette
| Token Name | Hex / RGBA Code | CSS / Tailwind Variable | Usage |
|---|---|---|---|
| Apex Green Primary | `#00A86B` | `--apex-green` / `bg-[#00A86B]` | Primary button, branding box, active links |
| Apex Green Hover | `#00965E` | `--apex-green-hover` / `hover:bg-[#00965E]` | Primary button hover state |
| Apex Green Accent | `#10B981` | `--apex-green-light` / `text-[#10B981]` | Headline accent text & subtitle text |
| Card Background | `#FFFFFF` | `--bg-card` / `bg-white` | Login card container |
| Dark Glass Pill | `rgba(15, 23, 42, 0.65)` | `--glass-pill` / `bg-slate-900/65` | AI pill tag background |
| Dark Glass Border | `rgba(255, 255, 255, 0.15)`| `--glass-border` / `border-white/15` | AI pill tag border |
| Feature Circle Bg | `rgba(15, 23, 42, 0.60)` | `--feature-circle-bg` | Left feature icon container background |
| Feature Circle Border| `rgba(0, 168, 107, 0.3)` | `--feature-circle-border` | Left feature icon container border |
| Text Primary Dark | `#0F172A` | `--text-dark` / `text-slate-900` | Headings, card title, welcome back |
| Text Secondary | `#334155` | `--text-body` / `text-slate-700` | Input text, input labels |
| Text Muted | `#64748B` | `--text-muted` / `text-slate-500` | Card subtitle, remember me label |
| Text Subtle | `#94A3B8` | `--text-subtle` / `text-slate-400` | Field icons, placeholders, feature subtitles |
| Text White | `#FFFFFF` | `--text-white` / `text-white` | Left side headline & feature titles |
| Input Background | `#F8FAFC` | `--bg-input` / `bg-slate-50` | Input field fill |
| Input Border | `#E2E8F0` | `--border-input` / `border-slate-200` | Input field border |

### C. Typography Token Scale
| Style | Font Family | Size | Weight | Line Height | Tracking |
|---|---|---|---|---|---|
| Left Headline | Inter / System | `44px` (`text-4xl`/`text-5xl`) | `800` (ExtraBold)| `1.15` | `-0.02em` |
| Left Subheading | Inter / System | `17px` (`text-base`/`text-lg`) | `400` (Normal) | `1.6` | `0` |
| Feature Title | Inter / System | `15px` (`text-sm`) | `600` (SemiBold)| `1.3` | `0` |
| Feature Subtitle| Inter / System | `13px` (`text-xs`) | `400` (Normal) | `1.4` | `0` |
| Card Brand Title| Inter / System | `24px` (`text-2xl`) | `800` (ExtraBold)| `1.2` | `-0.01em` |
| Card Subtitle | Inter / System | `10px` (`text-[10px]`) | `700` (Bold) | `1.2` | `0.1em` uppercase |
| Welcome Title | Inter / System | `22px` (`text-xl`) | `700` (Bold) | `1.3` | `0` |
| Field Label | Inter / System | `13px` (`text-xs`) | `600` (SemiBold)| `1.3` | `0` |
| Button Text | Inter / System | `15px` (`text-sm`) | `600` (SemiBold)| `1.4` | `0` |
| Pill Tag Text | Inter / System | `12px` (`text-[12px]`) | `600` (SemiBold)| `1.0` | `0.05em` |

### D. Detailed Vector Icon Specifications (Inline SVG)

1. **Truck Icon (Brand Logo Box)**:
   ```html
   <svg class="w-6 h-6 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
     <rect x="1" y="3" width="15" height="13" rx="2"></rect>
     <polygon points="16 8 20 8 23 11 23 16 16 16 16 8"></polygon>
     <circle cx="5.5" cy="18.5" r="2.5"></circle>
     <circle cx="18.5" cy="18.5" r="2.5"></circle>
   </svg>
   ```

2. **Email Icon (Input Left)**:
   ```html
   <svg class="w-5 h-5 text-slate-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
     <rect x="2" y="4" width="20" height="16" rx="2"></rect>
     <path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"></path>
   </svg>
   ```

3. **Password Lock Icon (Input Left)**:
   ```html
   <svg class="w-5 h-5 text-slate-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
     <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
     <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
   </svg>
   ```

4. **Eye Toggle Icon (Password Visibility)**:
   ```html
   <svg class="w-5 h-5 text-slate-400 hover:text-slate-600 cursor-pointer" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
     <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7z"></path>
     <circle cx="12" cy="12" r="3"></circle>
   </svg>
   ```

5. **Language Globe Icon (Top Segmented Control)**:
   ```html
   <svg class="w-3.5 h-3.5 text-slate-500 mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
     <circle cx="12" cy="12" r="10"></circle>
     <line x1="2" y1="12" x2="22" y2="12"></line>
     <path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"></path>
   </svg>
   ```

6. **Sun Icon (Theme Control)**:
   ```html
   <svg class="w-3.5 h-3.5 text-amber-500 mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
     <circle cx="12" cy="12" r="5"></circle>
     <line x1="12" y1="1" x2="12" y2="3"></line>
     <line x1="12" y1="21" x2="12" y2="23"></line>
     <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"></line>
     <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"></line>
     <line x1="1" y1="12" x2="3" y2="12"></line>
     <line x1="21" y1="12" x2="23" y2="12"></line>
     <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"></line>
     <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"></line>
   </svg>
   ```

7. **Google Logo (Social Auth Button)**:
   ```html
   <svg class="w-4 h-4 mr-2 inline-block" viewBox="0 0 24 24">
     <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
     <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
     <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z"/>
     <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z"/>
   </svg>
   ```

8. **GitHub Logo (Social Auth Button)**:
   ```html
   <svg class="w-4 h-4 mr-2 inline-block text-slate-900" viewBox="0 0 24 24" fill="currentColor">
     <path fill-rule="evenodd" clip-rule="evenodd" d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.53 1.032 1.53 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z"/>
   </svg>
   ```

9. **Right Arrow Icon (Primary Button)**:
   ```html
   <svg class="w-4 h-4 ml-2 inline-block stroke-current" viewBox="0 0 24 24" fill="none" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
     <line x1="5" y1="12" x2="19" y2="12"></line>
     <polyline points="12 5 19 12 12 19"></polyline>
   </svg>
   ```

10. **Telematics / CPU Icon (Feature 1)**:
   ```html
   <svg class="w-5 h-5 text-[#00A86B]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
     <rect x="4" y="4" width="16" height="16" rx="2" ry="2"></rect>
     <rect x="9" y="9" width="6" height="6"></rect>
     <line x1="9" y1="1" x2="9" y2="4"></line>
     <line x1="15" y1="1" x2="15" y2="4"></line>
     <line x1="9" y1="20" x2="9" y2="23"></line>
     <line x1="15" y1="20" x2="15" y2="23"></line>
     <line x1="20" y1="9" x2="23" y2="9"></line>
     <line x1="20" y1="15" x2="23" y2="15"></line>
     <line x1="1" y1="9" x2="4" y2="9"></line>
     <line x1="1" y1="15" x2="4" y2="15"></line>
   </svg>
   ```

11. **Secure & Compliant Icon (Feature 2)**:
   ```html
   <svg class="w-5 h-5 text-[#00A86B]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
     <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
     <polyline points="9 12 11 14 15 10"></polyline>
   </svg>
   ```

12. **Faster Deliveries Icon (Feature 3)**:
   ```html
   <svg class="w-5 h-5 text-[#00A86B]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
     <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"></polygon>
   </svg>
   ```

13. **Nationwide Network Icon (Feature 4)**:
   ```html
   <svg class="w-5 h-5 text-[#00A86B]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
     <circle cx="12" cy="12" r="10"></circle>
     <path d="M2 12h20"></path>
     <path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"></path>
   </svg>
   ```

---

## 3. Caveats

1. **Read-Only Scope**: Explorer 3 performed read-only analysis and design token synthesis. No source code in `src/` or `target/` was modified during this phase.
2. **Screen Aspect Ratio**: Coordinates (`61-63%` left, `8-10%` top, `3-5%` right, `35-37%` width, `75-80%` height) are calibrated for 16:9 desktop viewport resolutions (e.g. 1920x1080, 1440x900, 1536x864). Responsive breakpoints (e.g. tablet/mobile flex column fallback) should preserve readability if tested on smaller screens.
3. **Collage Layering**: Explorer 2 handles the background sloped diagonal collage rendering. Explorer 3's floating card sits on `z-index: 20` above all background elements (`z-index: 0` to `10`).

---

## 4. Conclusion

All visual details, layout specifications, design tokens, glassmorphism parameters, and exact inline SVG icons for both the left-side branding content and the right-side floating login card have been fully documented and formatted. The specification provides an exact, production-ready blueprint for implementing `login.html`.

---

## 5. Verification Method

To verify these design tokens and visual elements during or after implementation:

1. **Card Geometry Verification**:
   - Check computed style of login card in browser DevTools:
     - `left`: ~62% (range 61-63%)
     - `top`: ~9% (range 8-10%)
     - `right`: ~4% (range 3-5%)
     - `width`: ~36% (range 35-37%)
     - `height`: ~78% (range 75-80%)
     - `border-radius`: `24px`
   - Verify visible background around top, right, and bottom of the card.

2. **Color Token & Accent Checks**:
   - Primary action button background: `#00A86B` (Apex Green).
   - "Powering Progress." text color: `#00A86B` or `#10B981`.
   - Feature list icon colors: `#00A86B`.
   - Glassmorphism pill background: `rgba(15, 23, 42, 0.65)` with backdrop blur `12px`.

3. **Component & Icon Inventory**:
   - Confirm presence of all 13 SVG icons (truck logo, email, lock, eye, globe, sun, Google, GitHub, right arrow, 4 feature icons).
   - Verify segmented control `[ ☼ Light | 🌐 EN ˅ ]` inside top-right of login card.
   - Verify email pre-filled value `vikram@apextransport.com`.
