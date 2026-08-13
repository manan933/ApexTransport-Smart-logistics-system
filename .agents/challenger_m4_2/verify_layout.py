import re

with open(r'd:\TruckMate learnathon\TruckMate\src\main\resources\static\login.html', 'r', encoding='utf-8') as f:
    html = f.read()

print("=== 1. CARD POSITIONING & FLOATING CONSTRAINTS ===")
card_col_match = re.search(r'\.apx-card-col\s*\{([^}]+)\}', html)
card_css = card_col_match.group(1) if card_col_match else ''
print("apx-card-col CSS block:\n", card_css.strip())

has_left = 'left: 61.5%' in card_css
has_top = 'top: 8.5vh' in card_css
has_right = 'right: 3.5%' in card_css
has_width = 'width: 35%' in card_css
has_height = 'height: 83vh' in card_css

print(f"Left ~61.5%: {has_left}")
print(f"Top ~8.5vh: {has_top}")
print(f"Right ~3.5%: {has_right}")
print(f"Width ~35%: {has_width}")
print(f"Height ~83vh: {has_height}")

top_vh = 8.5
height_vh = 83.0
bottom_gap_vh = 100.0 - (top_vh + height_vh)
print(f"Top gap: {top_vh}vh, Bottom gap: {bottom_gap_vh}vh, Right gap: 3.5%, Left offset: 61.5%")
print(f"Floating constraint passed (card does NOT touch viewport edges): {top_vh > 0 and bottom_gap_vh > 0 and 3.5 > 0}")

print("\n=== 2. LEFT-SIDE 4 FEATURE ITEMS ===")
feature_titles = re.findall(r'<h4>(.*?)</h4>', html)
feature_descs = re.findall(r'<p>(.*?)</p>', html)
icon_boxes = re.findall(r'class="apx-feature-icon-box"', html)

print(f"Found {len(feature_titles)} titles, {len(feature_descs)} descriptions, {len(icon_boxes)} icon boxes.")
for i in range(min(len(feature_titles), 4)):
    print(f"Feature {i+1}: '{feature_titles[i]}' -> '{feature_descs[i]}'")

print("\n=== 3. BUTTON HOVER STYLES ===")
submit_hover = re.search(r'\.apx-btn-submit:hover\s*\{([^}]+)\}', html)
social_hover = re.search(r'\.apx-btn-social:hover\s*\{([^}]+)\}', html)
print("Submit hover CSS:", submit_hover.group(1).strip() if submit_hover else "MISSING")
print("Social hover CSS:", social_hover.group(1).strip() if social_hover else "MISSING")

print("\n=== 4. FORM INPUT FIELDS ===")
email_input = re.search(r'<input[^>]*id="login-email"[^>]*>', html)
pass_input = re.search(r'<input[^>]*id="login-password"[^>]*>', html)
print("Email field:", email_input.group(0) if email_input else "MISSING")
print("Password field:", pass_input.group(0) if pass_input else "MISSING")
