import os
import sys
import re
from html.parser import HTMLParser

if sys.stdout.encoding.lower() != 'utf-8':
    sys.stdout.reconfigure(encoding='utf-8')

SRC_PATH = r"d:\TruckMate learnathon\TruckMate\src\main\resources\static\login.html"
TARGET_PATH = r"d:\TruckMate learnathon\TruckMate\target\classes\static\login.html"
STATIC_DIR = r"d:\TruckMate learnathon\TruckMate\src\main\resources\static"
TARGET_STATIC_DIR = r"d:\TruckMate learnathon\TruckMate\target\classes\static"

def check_byte_for_byte():
    print("--- [TEST 1] Byte-for-Byte Equality ---")
    if not os.path.exists(SRC_PATH):
        print(f"FAIL: Source file does not exist at {SRC_PATH}")
        return False
    if not os.path.exists(TARGET_PATH):
        print(f"FAIL: Target file does not exist at {TARGET_PATH}")
        return False
    
    with open(SRC_PATH, 'rb') as f1, open(TARGET_PATH, 'rb') as f2:
        src_bytes = f1.read()
        target_bytes = f2.read()
    
    if src_bytes == target_bytes:
        print(f"PASS: src ({len(src_bytes)} bytes) and target ({len(target_bytes)} bytes) are IDENTICAL byte-for-byte.")
        return True
    else:
        print(f"FAIL: Byte mismatch! src length: {len(src_bytes)}, target length: {len(target_bytes)}")
        return False

class CustomHTMLParser(HTMLParser):
    def __init__(self):
        super().__init__()
        self.errors = []
        self.tags_stack = []

    def handle_starttag(self, tag, attrs):
        void_tags = {'area', 'base', 'br', 'col', 'embed', 'hr', 'img', 'input', 'link', 'meta', 'param', 'source', 'track', 'wbr'}
        if tag not in void_tags:
            self.tags_stack.append(tag)

    def handle_endtag(self, tag):
        void_tags = {'area', 'base', 'br', 'col', 'embed', 'hr', 'img', 'input', 'link', 'meta', 'param', 'source', 'track', 'wbr'}
        if tag in void_tags:
            return
        if self.tags_stack and self.tags_stack[-1] == tag:
            self.tags_stack.pop()
        else:
            self.errors.append(f"Mismatched closing tag </{tag}>. Stack top: {self.tags_stack[-1] if self.tags_stack else 'Empty'}")

def check_html_syntax(content):
    print("\n--- [TEST 2] HTML Syntax Validity ---")
    parser = CustomHTMLParser()
    try:
        parser.feed(content)
        if parser.errors:
            print("FAIL: HTML syntax errors detected:")
            for err in parser.errors:
                print(f"  - {err}")
            return False
        if parser.tags_stack:
            print(f"FAIL: Unclosed tags remaining in stack: {parser.tags_stack}")
            return False
        print("PASS: HTML structure parsed cleanly with 0 syntax or stack errors.")
        return True
    except Exception as e:
        print(f"FAIL: Parser exception: {e}")
        return False

def check_image_paths(content):
    print("\n--- [TEST 3] Image Path Resolution ---")
    urls = re.findall(r"url\(['\"]?([^'\"]+)['\"]?\)", content)
    img_srcs = re.findall(r"<img[^>]+src=['\"]([^'\"]+)['\"]", content)
    all_paths = set(urls + img_srcs)
    
    print(f"Found image references: {all_paths}")
    all_ok = True
    for p in all_paths:
        rel_p = p.lstrip('/') if p.startswith('/') else p
        src_file = os.path.join(STATIC_DIR, rel_p)
        target_file = os.path.join(TARGET_STATIC_DIR, rel_p)
        
        src_exists = os.path.exists(src_file)
        target_exists = os.path.exists(target_file)
        
        if src_exists and target_exists:
            print(f"PASS: Image path '{p}' resolves in both src ({src_file}) and target ({target_file}).")
        else:
            print(f"FAIL: Image path '{p}' missing! src_exists={src_exists}, target_exists={target_exists}")
            all_ok = False
    return all_ok

def check_svg_icons(content):
    print("\n--- [TEST 4] DOM Structure Completeness for SVG Icons ---")
    svg_blocks = re.findall(r'<svg.*?</svg>', content, re.DOTALL)
    print(f"Total SVG icons found in DOM: {len(svg_blocks)}")
    
    if len(svg_blocks) < 13:
        print(f"FAIL: Expected at least 13 SVG icons, but found {len(svg_blocks)}")
        return False
    else:
        print(f"PASS: Found {len(svg_blocks)} SVG icons (>= 13 required icons present).")
        
    valid_svgs = True
    for idx, block in enumerate(svg_blocks, 1):
        has_shapes = any(shape in block for shape in ['<path', '<circle', '<rect', '<line', '<polygon', '<polyline'])
        has_viewbox_or_size = 'viewBox' in block or ('width' in block and 'height' in block)
        if not (has_shapes and has_viewbox_or_size):
            print(f"FAIL: SVG #{idx} is incomplete or missing vector shapes/viewBox!")
            valid_svgs = False
        else:
            print(f"  SVG #{idx}: Complete DOM structure with vector shapes & viewBox/dimensions.")
            
    return valid_svgs

def check_prefilled_values(content):
    print("\n--- [TEST 5] Pre-filled Input Values & Class Names ---")
    email_match = re.search(r'<input[^>]*id=["\']login-email["\'][^>]*>', content)
    pass_match = re.search(r'<input[^>]*id=["\']login-password["\'][^>]*>', content)
    
    ok = True
    if email_match:
        tag = email_match.group(0)
        if 'value="vikram@apextransport.com"' in tag and 'placeholder="vikram@apextransport.com"' in tag:
            print(f"PASS: #login-email has correct prefilled value and placeholder.")
        else:
            print(f"FAIL: #login-email tag mismatch: {tag}")
            ok = False
    else:
        print("FAIL: #login-email not found!")
        ok = False

    if pass_match:
        tag = pass_match.group(0)
        if 'value="vikram123"' in tag:
            print(f"PASS: #login-password has correct prefilled value.")
        else:
            print(f"FAIL: #login-password tag mismatch: {tag}")
            ok = False
    else:
        print("FAIL: #login-password not found!")
        ok = False
        
    return ok

def check_layout_dimensions_and_css(content):
    print("\n--- [TEST 6] Layout Dimensions & CSS Selector Integrity ---")
    ok = True
    
    # Check .apx-hero-col width ~60%
    if 'width: 60%;' in content and '.apx-hero-col' in content:
        print("PASS: .apx-hero-col width set to 60%.")
    else:
        print("FAIL: .apx-hero-col width 60% missing!")
        ok = False
        
    # Check .apx-card-col positioning and dimensions
    card_col_checks = [
        ('left: 61.5%', 'Card left position ~61.5%'),
        ('top: 8.5vh', 'Card top position ~8.5vh'),
        ('right: 3.5%', 'Card right position ~3.5%'),
        ('width: 35%', 'Card width ~35%'),
        ('height: 83vh', 'Card height ~83vh')
    ]
    for css_prop, desc in card_col_checks:
        if css_prop in content:
            print(f"PASS: {desc} ({css_prop}) found.")
        else:
            print(f"FAIL: {desc} ({css_prop}) missing!")
            ok = False

    # Check login card styling
    card_checks = [
        ('border-radius: 24px', 'Card border-radius 24px'),
        ('background: #ffffff', 'Card background pure white'),
        ('box-shadow:', 'Card box-shadow')
    ]
    for css_prop, desc in card_checks:
        if css_prop in content:
            print(f"PASS: {desc} ({css_prop}) found.")
        else:
            print(f"FAIL: {desc} ({css_prop}) missing!")
            ok = False
            
    # Check 3 slice CSS rules
    slices = ['.apx-slice-1', '.apx-slice-2', '.apx-slice-3', '.apx-dark-overlay']
    for s in slices:
        if s in content:
            print(f"PASS: Background slice selector '{s}' defined.")
        else:
            print(f"FAIL: Background slice selector '{s}' missing!")
            ok = False

    # Check requirement texts and elements
    text_checks = [
        ("⚡", "Pill bolt icon '⚡'"),
        ("AI POWERED", "Pill text 'AI POWERED'"),
        ("SMART LOGISTICS", "Pill text 'SMART LOGISTICS'"),
        ("Moving India.", "Headline 'Moving India.'"),
        ("Powering Progress.", "Headline accent 'Powering Progress.'"),
        ("AI-orchestrated freight network", "Subheading"),
        ("AI Logistics Telematics", "Feature 1 title"),
        ("Secure &amp; Compliant", "Feature 2 title"),
        ("Faster Deliveries", "Feature 3 title"),
        ("Nationwide Network", "Feature 4 title"),
        ("&copy; 2025 Apex Transport", "Copyright text"),
        ("Light", "Pill Light text"),
        ("EN", "Pill Language text"),
        ("Apex Transport", "Brand title"),
        ("NEXT-GEN AUTONOMOUS FREIGHT NETWORK", "Brand subtitle"),
        ("Welcome Back", "Welcome heading"),
        ("Sign in to your workspace", "Welcome subtext"),
        ("Remember me", "Remember me option"),
        ("Forgot password?", "Forgot password link"),
        ("Sign In to Workspace", "Submit button text"),
        ("or continue with", "Divider text"),
        ("Google", "Google social button"),
        ("GitHub", "GitHub social button"),
        ("New to Apex Transport?", "Auth switch footer text")
    ]
    
    for txt, desc in text_checks:
        if txt in content:
            print(f"PASS: Required text/element '{desc}' found.")
        else:
            print(f"FAIL: Required text/element '{desc}' missing!")
            ok = False

    return ok

def main():
    b_ok = check_byte_for_byte()
    
    with open(SRC_PATH, 'r', encoding='utf-8') as f:
        content = f.read()
        
    h_ok = check_html_syntax(content)
    i_ok = check_image_paths(content)
    s_ok = check_svg_icons(content)
    p_ok = check_prefilled_values(content)
    l_ok = check_layout_dimensions_and_css(content)
    
    print("\n================ SUMMARY ================")
    print(f"1. Byte-for-byte equality: {'PASS' if b_ok else 'FAIL'}")
    print(f"2. HTML Syntax validity: {'PASS' if h_ok else 'FAIL'}")
    print(f"3. Image path resolution: {'PASS' if i_ok else 'FAIL'}")
    print(f"4. SVG Icons DOM completeness: {'PASS' if s_ok else 'FAIL'}")
    print(f"5. Pre-filled input values: {'PASS' if p_ok else 'FAIL'}")
    print(f"6. Layout dimensions, CSS integrity & Text Content: {'PASS' if l_ok else 'FAIL'}")
    
    all_passed = b_ok and h_ok and i_ok and s_ok and p_ok and l_ok
    if all_passed:
        print("\nFINAL VERDICT: APPROVE")
        sys.exit(0)
    else:
        print("\nFINAL VERDICT: REJECT")
        sys.exit(1)

if __name__ == '__main__':
    main()
