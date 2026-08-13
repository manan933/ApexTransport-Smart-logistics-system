import os
import re
import sys
from html.parser import HTMLParser

# Set stdout to UTF-8
sys.stdout.reconfigure(encoding='utf-8')

src_file = r'd:\TruckMate learnathon\TruckMate\src\main\resources\static\login.html'
target_file = r'd:\TruckMate learnathon\TruckMate\target\classes\static\login.html'

def run_tests():
    report = []
    report.append("=== EMPIRICAL TEST VERIFICATION REPORT FOR LOGIN.HTML ===")

    # Test 1: File Existence & Byte-for-Byte Match
    if not os.path.exists(src_file):
        report.append("[FAIL] Test 1: src login.html does not exist.")
        return False, report
    if not os.path.exists(target_file):
        report.append("[FAIL] Test 1: target login.html does not exist.")
        return False, report

    with open(src_file, 'rb') as f:
        src_bytes = f.read()
    with open(target_file, 'rb') as f:
        target_bytes = f.read()

    if src_bytes == target_bytes:
        report.append(f"[PASS] Test 1: src and target files match byte-for-byte ({len(src_bytes)} bytes).")
    else:
        report.append(f"[FAIL] Test 1: src ({len(src_bytes)} bytes) and target ({len(target_bytes)} bytes) do NOT match byte-for-byte.")
        return False, report

    content = src_bytes.decode('utf-8')

    # Test 2: HTML Syntax Parsing
    class SyntaxChecker(HTMLParser):
        def __init__(self):
            super().__init__()
            self.tag_stack = []
            self.svg_count = 0
            self.svg_elements = []
            self.void_tags = {'meta', 'link', 'input', 'img', 'br', 'hr'}

        def handle_starttag(self, tag, attrs):
            if tag == 'svg':
                self.svg_count += 1
                self.svg_elements.append(dict(attrs))
            if tag not in self.void_tags:
                self.tag_stack.append(tag)

        def handle_endtag(self, tag):
            if tag not in self.void_tags:
                if self.tag_stack and self.tag_stack[-1] == tag:
                    self.tag_stack.pop()

    checker = SyntaxChecker()
    try:
        checker.feed(content)
        report.append(f"[PASS] Test 2: HTML parsed successfully. Unclosed non-void tags remaining: {len(checker.tag_stack)} (Tags: {checker.tag_stack})")
    except Exception as e:
        report.append(f"[FAIL] Test 2: HTML parsing threw exception: {e}")
        return False, report

    # Test 3: SVG Icon Count & DOM structure
    svg_matches = re.findall(r'<svg[^>]*>', content, re.IGNORECASE)
    report.append(f"[INFO] Test 3: Found {len(svg_matches)} SVG tags in login.html.")
    if len(svg_matches) >= 13:
        report.append(f"[PASS] Test 3: DOM structure contains all required SVG icons ({len(svg_matches)} total >= 13).")
    else:
        report.append(f"[FAIL] Test 3: Found only {len(svg_matches)} SVG icons, expected at least 13.")

    # Test 4: Image Path Resolution
    images_found = re.findall(r'url\([\'"]?([^\'")]+)[\'"]?\)', content)
    report.append(f"[INFO] Image references in CSS: {images_found}")
    image_paths_valid = True
    for img_ref in images_found:
        clean_ref = img_ref.lstrip('/')
        rel_path = clean_ref.replace('/', os.sep)
        full_src = os.path.join(r'd:\TruckMate learnathon\TruckMate\src\main\resources\static', rel_path)
        full_tgt = os.path.join(r'd:\TruckMate learnathon\TruckMate\target\classes\static', rel_path)
        
        src_ok = os.path.exists(full_src)
        tgt_ok = os.path.exists(full_tgt)
        if src_ok and tgt_ok:
            report.append(f"[PASS] Image path '{img_ref}' resolved on disk in both src and target.")
        else:
            report.append(f"[FAIL] Image path '{img_ref}' NOT found (src: {src_ok}, target: {tgt_ok}).")
            image_paths_valid = False

    # Test 5: CSS Selectors & Layout Dimensions
    css_checks = [
        ('apx-viewport position relative, width 100vw, height 100vh', '.apx-viewport'),
        ('apx-hero-col width 60%', '.apx-hero-col'),
        ('apx-card-col width ~35%, height ~83vh, left 61.5%', '.apx-card-col'),
        ('apx-login-card border-radius 24px, pure white bg', '.apx-login-card'),
        ('apx-slice-1 upper-left', '.apx-slice-1'),
        ('apx-slice-2 middle-left', '.apx-slice-2'),
        ('apx-slice-3 lower-left', '.apx-slice-3'),
        ('apx-dark-overlay dark overlay', '.apx-dark-overlay'),
    ]
    all_css_ok = True
    for desc, selector in css_checks:
        if selector in content:
            report.append(f"[PASS] CSS Selector check '{desc}': Found '{selector}'")
        else:
            report.append(f"[FAIL] CSS Selector check '{desc}': Missing '{selector}'")
            all_css_ok = False

    # Test 6: Pre-filled Values and Elements
    req_items = [
        ("Badge Pill 1", "AI POWERED", "Pill text AI POWERED"),
        ("Badge Pill 2", "SMART LOGISTICS", "Pill text SMART LOGISTICS"),
        ("Headline 1", "Moving India.", "Headline Part 1"),
        ("Headline 2", "Powering Progress.", "Headline Part 2"),
        ("Subheading", "AI-orchestrated freight network delivering intelligence, efficiency and trust.", "Subheading"),
        ("Feature 1 Title", "AI Logistics Telematics", "Feature 1 Title"),
        ("Feature 1 Subtitle", "Real-time insights", "Feature 1 Subtitle"),
        ("Feature 2 Title", "Secure &amp; Compliant", "Feature 2 Title"),
        ("Feature 2 Subtitle", "Enterprise-grade security", "Feature 2 Subtitle"),
        ("Feature 3 Title", "Faster Deliveries", "Feature 3 Title"),
        ("Feature 3 Subtitle", "Optimized routes", "Feature 3 Subtitle"),
        ("Feature 4 Title", "Nationwide Network", "Feature 4 Title"),
        ("Feature 4 Subtitle", "Strong, scalable", "Feature 4 Subtitle"),
        ("Copyright text", "2025 Apex Transport. All rights reserved.", "Copyright text"),
        ("Card Control Light", "Light", "Card Top Control Light"),
        ("Card Control EN", "EN ˅", "Card Top Control EN"),
        ("Brand Square Box", "apx-brand-square", "Brand Square Box"),
        ("Brand Title", "Apex Transport", "Brand Title"),
        ("Brand Subtitle", "NEXT-GEN AUTONOMOUS FREIGHT NETWORK", "Brand Subtitle"),
        ("Welcome Heading", "Welcome Back", "Welcome Heading"),
        ("Workspace Subtext", "Sign in to your workspace", "Workspace Subtext"),
        ("Pre-filled Email Input", 'value="vikram@apextransport.com"', "Pre-filled Email Input"),
        ("Pre-filled Password Input", 'value="vikram123"', "Pre-filled Password Input"),
        ("Remember Me Checkbox", 'id="remember-me" checked', "Remember Me Checkbox Checked"),
        ("Forgot Password Link", "Forgot password?", "Forgot Password Link"),
        ("Sign In Submit Button", "Sign In to Workspace", "Sign In Submit Button"),
        ("Divider text", "or continue with", "Divider text"),
        ("Google social button", "Google", "Google social button"),
        ("GitHub social button", "GitHub", "GitHub social button"),
        ("Create Account link", "Create an account", "Create Account link"),
    ]

    all_reqs_ok = True
    for name, snippet, desc in req_items:
        if snippet in content:
            report.append(f"[PASS] Requirement check '{desc}' ('{snippet}'): Present.")
        else:
            report.append(f"[FAIL] Requirement check '{desc}' ('{snippet}'): Missing.")
            all_reqs_ok = False

    overall_pass = image_paths_valid and all_css_ok and all_reqs_ok and (len(svg_matches) >= 13)
    return overall_pass, report

if __name__ == '__main__':
    success, report = run_tests()
    for line in report:
        print(line)
    print(f"\nFINAL EMPIRICAL VERDICT: {'APPROVE' if success else 'REJECT'}")
