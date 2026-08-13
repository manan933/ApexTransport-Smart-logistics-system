/**
 * APEX TRANSPORT - GLOBAL MULTI-LINGUAL SYSTEM (ENGLISH / HINGLISH)
 * Seamless real-time bilingual switching across the entire platform
 */

const APEX_TRANSLATIONS = {
  en: {
    // Branding & Header
    "brand_name": "Apex Transport",
    "brand_tagline": "Next-Gen Autonomous Freight Network",
    "tag_transporter": "ENTERPRISE SHIPPER DESK",
    "tag_driver": "PILOT FLIGHT DECK",
    "tag_profile": "IDENTITY & FLEET REGISTRY",
    "nav_overview": "Overview",
    "nav_dispatch": "Dispatch Load",
    "nav_my_orders": "Consignments",
    "nav_available_jobs": "Available Loads",
    "nav_current_job": "Current Transit",
    "nav_completed": "Delivered Trips",
    "nav_profile": "Profile & Fleet",
    "nav_logs": "Audit Trail",
    "nav_logout": "Logout",
    "nav_admin_panel": "Command Console",
    
    // Auth & Landing
    "hero_title": "Apex Transport",
    "hero_subtitle": "Enterprise AI-orchestrated intercity freight & cold-chain telematics network.",
    "btn_get_started": "Get Started",
    "btn_how_it_works": "What is Apex Transport?",
    "tab_sign_in": "Sign In",
    "tab_register": "Register Account",
    "label_email": "Official Email Address",
    "label_password": "Secure Password",
    "placeholder_email": "e.g. name@company.com",
    "placeholder_password": "••••••••••••",
    "role_transporter": "Shipper / Transporter",
    "role_driver": "Fleet Driver",
    "role_admin": "System Admin",
    "btn_login": "Access Workspace",
    "btn_register_submit": "Create Apex Account",
    "register_success": "Account created successfully! Logging in...",
    "login_success": "Welcome back! Redirecting...",

    // Tabs & Navigation
    "tab_active_orders": "Fleet Command",
    "tab_activity_logs": "Activity & Telematics",
    "tab_active_job": "Active Transit Mission",
    "tab_available_loads": "Available Freight Loads",
    "btn_refresh": "Refresh",
    "btn_post_load": "Post New Consignment",
    "btn_accept_job": "Accept Mission",
    "btn_view_map": "Open in Google Maps App",
    "btn_start_transit": "Start Highway Transit",
    "btn_mark_delivered": "Deliver & Upload POD",
    "msg_no_active_job": "No Active Consignment Assigned",
    
    // KPI Cards
    "kpi_active_loads": "Active Shipments",
    "kpi_pending_assignment": "Pending Driver Match",
    "kpi_completed_deliveries": "Successful Deliveries",
    "kpi_freight_spend": "Total Freight Escrow",
    "map_radar_title": "National Logistics Telematics Radar",

    // How it works section
    "how_title": "Autonomous Freight Architecture",
    "how_desc": "Engineered for maximum payload efficiency, transparent pricing, and sub-second driver dispatch across India.",
    "step1_title": "1. Multi-Attribute Load Dispatch",
    "step1_desc": "Shippers specify cargo volume, cold chain targets, package counts, GST e-way bills, and geo-fenced routes with instantaneous algorithmic pricing.",
    "step2_title": "2. High-Priority Driver Match",
    "step2_desc": "Nearby verified fleet carriers receive instantaneous load alerts and can claim consignments on a strict First-Come, First-Serve basis.",
    "step3_title": "3. Real-Time Telematics & Navigation",
    "step3_desc": "One-click handoff to Google Maps with automated origin-destination routing, GPS telemetry tracking, and live route status.",
    "step4_title": "4. Digital Proof of Delivery & Rating",
    "step4_desc": "Mandatory photo POD verification, instant payment clearing, and two-way 5-star performance rating between shippers and drivers.",

    // Dashboard & Consignments
    "dash_open_loads": "Open Consignments",
    "dash_active_transit": "Active In-Transit",
    "dash_completed_trips": "Completed Deliveries",
    "dash_total_spend": "Total Freight Value",
    "dash_driver_rating": "Driver Rating",
    "dash_shipper_rating": "Shipper Trust Score",
    "btn_new_consignment": "+ Post New Consignment",
    "open_in_gmaps": "🗺️ Open in Google Maps",
    "gmaps_tooltip": "Redirects to Google Maps with pre-filled pickup & drop locations",

    // Consignment Posting Form & Columns
    "modal_post_title": "Post New Consignment Load",
    "sec_route": "Route & Schedule",
    "sec_cargo": "Cargo Specifications",
    "sec_handling": "Handling & Cold-Chain Flags",
    "sec_contact": "Pickup Point Contact",
    "sec_docs": "Documentation & Invoices",
    "sec_notes": "Driver Instructions & Fair Pricing",
    "label_pickup": "Route (Origin → Destination)",
    "label_weight": "Cargo Specifications",
    "col_driver": "Assigned Driver",
    "label_freight_price": "Escrow Value",
    
    "lbl_pickup_hub": "Pickup Hub / City",
    "lbl_drop_hub": "Destination Drop City",
    "lbl_pickup_date": "Pickup Date",
    "lbl_time_slot": "Preferred Time Window",
    "lbl_goods_type": "Commodity / Goods Type",
    "lbl_goods_desc": "Detailed Goods Description",
    "lbl_weight": "Total Cargo Weight (KG)",
    "lbl_package_count": "Package / Pallet Count",
    "lbl_dimensions": "Dimensions (L x W x H)",
    "lbl_unit": "Unit",
    "lbl_vehicle_type": "Required Vehicle Type",
    "lbl_driver_notes": "Special Instructions for Driver",
    "lbl_invoice_file": "Attach GST Invoice / E-Way Bill",
    "lbl_cargo_photos": "Cargo Photos (Up to 5 files)",
    "lbl_snap_camera": "📸 Snap Photo with Camera",

    "flag_fragile": "Fragile Cargo",
    "flag_hazardous": "Hazardous / Hazmat",
    "flag_temp": "Cold Chain Required",
    "flag_target_temp": "Target Temperature (°C)",
    "flag_stackable": "Stackable Load",
    "chk_use_my_details": "Use my registered company phone & contact",
    "lbl_contact_name": "Site Contact Person Name",
    "lbl_contact_phone": "Site Contact Phone Number",

    "lbl_est_distance": "Calculated Road Distance",
    "lbl_est_fare": "System Calculated Fare",
    "lbl_pay_method": "Payment Settlement Method",
    "btn_dispatch_consignment": "🚀 Dispatch Consignment Now",

    // Driver Current Job & POD
    "current_job_title": "Active Shipment in Transit",
    "no_active_job": "No active shipment claimed. Check available loads board.",
    "btn_start_transit": "🚚 Start Transit & Enable GPS",
    "btn_complete_delivery": "✅ Complete Delivery & Upload POD",
    "pod_modal_title": "Mandatory Proof of Delivery (POD)",
    "pod_instructions": "Upload a clear photo of the signed delivery challan / physical gate receipt to finalize delivery.",
    "btn_submit_pod": "Verify & Submit POD",
    "pod_required_alert": "Proof of delivery image is mandatory to complete shipment.",

    // Ratings & Reviews
    "rate_driver_title": "Rate Driver & Clear Payment",
    "rate_shipper_title": "Rate Shipper Experience",
    "rating_stars": "Overall Rating (1 to 5 Stars)",
    "feedback_notes": "Feedback Comments / Experience Summary",
    "btn_submit_rating": "Submit Rating & Settle",
    "btn_view_pod": "📄 View Proof of Delivery (POD)",
    
    // Status Badges
    "status_pending": "OPEN FOR CLAIM",
    "status_posted": "AWAITING DRIVER",
    "status_assigned": "DRIVER ASSIGNED",
    "status_in_transit": "IN TRANSIT",
    "status_completed": "DELIVERED",
    "status_cancelled": "CANCELLED",

    // Audit & Common
    "audit_title": "Activity & Telematics Audit Trail",
    "audit_time": "Timestamp",
    "audit_user": "Operator",
    "audit_action": "Action Event",
    "audit_details": "Details",
    "toast_copied": "Link copied to clipboard!",
    "toast_error": "An error occurred. Please try again."
  },

  hinglish: {
    // Branding & Header
    "brand_name": "Apex Transport",
    "brand_tagline": "Next-Gen Intelligent Freight Network",
    "tag_transporter": "TRANSPORTER / SHIPPER DESK",
    "tag_driver": "PILOT DRIVER DESK",
    "tag_profile": "PROFILE AUR GAADI DETAILS",
    "nav_overview": "Overview (Overview)",
    "nav_dispatch": "Dispatch Karein",
    "nav_my_orders": "Mera Maal / Loads",
    "nav_available_jobs": "Available Loads (Maal)",
    "nav_current_job": "Chalu Safar (Current Transit)",
    "nav_completed": "Pura Safar (Delivered)",
    "nav_profile": "Mera Profile aur Gaadi",
    "nav_logs": "Activity Logs",
    "nav_logout": "Logout Karein",
    "nav_admin_panel": "Control Panel",
    
    // Auth & Landing
    "hero_title": "Apex Transport",
    "hero_subtitle": "Desh ka sabse tezz AI-driven transport aur cold-chain freight network.",
    "btn_get_started": "Shuru Karein (Get Started)",
    "btn_how_it_works": "Apex Kaise Kaam Karta Hai?",
    "tab_sign_in": "Sign In Karein",
    "tab_register": "Naya Account Banayein",
    "label_email": "Aapka Email Address",
    "label_password": "Password",
    "placeholder_email": "jaise: name@company.com",
    "placeholder_password": "••••••••••••",
    "role_transporter": "Transporter / Shipper",
    "role_driver": "Truck Driver / Malik",
    "role_admin": "Admin",
    "btn_login": "Login Karein",
    "btn_register_submit": "Account Banayein",
    "register_success": "Account ban gaya! Login ho raha hai...",
    "login_success": "Swagat hai! Dashboard khul raha hai...",

    // Tabs & Navigation
    "tab_active_orders": "Fleet Command (Maal Status)",
    "tab_activity_logs": "Activity & GPS Logs",
    "tab_active_job": "Chalu Safar & Mission",
    "tab_available_loads": "Available Loads (Maal)",
    "btn_refresh": "Taaza Karein (Refresh)",
    "btn_post_load": "Naya Maal Post Karein",
    "btn_accept_job": "Load Accept Karein",
    "btn_view_map": "Google Maps Mein Kholein",
    "btn_start_transit": "Safar Shuru Karein (Transit)",
    "btn_mark_delivered": "Delivery Pura & POD Upload",
    "msg_no_active_job": "Abhi koi shipment assign nahi hai",
    
    // KPI Cards
    "kpi_active_loads": "Raste Mein (Active)",
    "kpi_pending_assignment": "Driver Ki Talash (Pending)",
    "kpi_completed_deliveries": "Pura Safar (Delivered)",
    "kpi_freight_spend": "Total Bhaada (Escrow)",
    "map_radar_title": "National Logistics Live Radar",

    // How it works section
    "how_title": "Apex Transport Ka System Kaise Chalta Hai?",
    "how_desc": "Fastest booking, transparent bhaada, direct Google Maps navigation aur pakka digital payment system.",
    "step1_title": "1. Maal Aur Route Ki Details Daalein",
    "step1_desc": "Pickup-Drop city, vajan (weight), cold chain temperature, GST invoice aur contact details daalein. System turant sahi bhaada calculate karega.",
    "step2_title": "2. Driver Turant Load Claim Karega",
    "step2_desc": "Verified drivers ko direct phone par alert aayega aur jo driver pehle accept karega usko load assign ho jayega.",
    "step3_title": "3. Live GPS Aur Google Maps Direction",
    "step3_desc": "1-Click se seedha Google Maps app khulega jisme pickup aur drop point pehle se set honge. Live transit track hota rahega.",
    "step4_title": "4. Digital POD Upload Aur Rating System",
    "step4_desc": "Delivery spot par driver signed receipt / POD photo upload karega, transporter verify karke pay karega aur dono ek dusre ko ⭐ rating denge.",

    // Dashboard & Consignments
    "dash_open_loads": "Khule Loads (Pending)",
    "dash_active_transit": "Raste Mein (In Transit)",
    "dash_completed_trips": "Pahunch Gaye (Completed)",
    "dash_total_spend": "Kul Bhaada (Total Freight)",
    "dash_driver_rating": "Driver Ki Rating",
    "dash_shipper_rating": "Shipper Ki Rating",
    "btn_new_consignment": "+ Naya Maal Post Karein",
    "open_in_gmaps": "🗺️ Google Maps Mein Kholein",
    "gmaps_tooltip": "Direct Google Maps app khulega pickup aur drop route ke saath",

    // Consignment Posting Form & Columns
    "modal_post_title": "Naya Maal / Consignment Post Karein",
    "sec_route": "Route Aur Time Slot",
    "sec_cargo": "Maal Ki Jankari (Cargo)",
    "sec_handling": "Handling & Cold-Chain Toggles",
    "sec_contact": "Pickup Point Contact Details",
    "sec_docs": "Bills Aur Documents",
    "sec_notes": "Driver Ke Liye Hidayat Aur Bhaada",
    "label_pickup": "Route (Kahan Se → Kahan Tak)",
    "label_weight": "Maal Ki Details & Vajan",
    "col_driver": "Driver",
    "label_freight_price": "Bhaada (Escrow)",
    
    "lbl_pickup_hub": "Pickup City / Location",
    "lbl_drop_hub": "Drop City / Location",
    "lbl_pickup_date": "Pickup Ki Tareekh (Date)",
    "lbl_time_slot": "Preferred Time Window",
    "lbl_goods_type": "Maal Ka Type (Goods Category)",
    "lbl_goods_desc": "Maal Ka Pura Vivaran (Description)",
    "lbl_weight": "Kul Vajan (Weight in KG)",
    "lbl_package_count": "Total Packages / Boxes",
    "lbl_dimensions": "Dimensions (L x W x H)",
    "lbl_unit": "Unit",
    "lbl_vehicle_type": "Zaroori Gaadi Ka Type",
    "lbl_driver_notes": "Driver Ke Liye Khaas Notes",
    "lbl_invoice_file": "GST Invoice / E-Way Bill Upload Karein",
    "lbl_cargo_photos": "Maal Ki Photos (Max 5 Files)",
    "lbl_snap_camera": "📸 Camera Se Photo Kheechein",

    "flag_fragile": "Nazuk Maal (Fragile)",
    "flag_hazardous": "Khatarnak / Hazardous (Hazmat)",
    "flag_temp": "Refrigerated (Cold Chain)",
    "flag_target_temp": "Target Temperature (°C)",
    "flag_stackable": "Upar Maal Rakh Sakte Hain (Stackable)",
    "chk_use_my_details": "Mera hi registered phone aur contact use karein",
    "lbl_contact_name": "Loading Point Contact Person Ka Naam",
    "lbl_contact_phone": "Loading Point Contact Phone Number",

    "lbl_est_distance": "Duri (Estimated Road Distance)",
    "lbl_est_fare": "Andazit Bhaada (Calculated Fare)",
    "lbl_pay_method": "Payment Ka Tareeqa (Method)",
    "btn_dispatch_consignment": "🚀 Maal Post Karein (Dispatch)",

    // Driver Current Job & POD
    "current_job_title": "Chalu Shipment / Safar",
    "no_active_job": "Abhi koi chalu safar nahi hai. Available loads board dekhein.",
    "btn_start_transit": "🚚 Safar Shuru Karein (Start Transit)",
    "btn_complete_delivery": "✅ Delivery Pura Karein & POD Upload Karein",
    "pod_modal_title": "Proof of Delivery (POD) Photo Zaroori Hai",
    "pod_instructions": "Delivery pura karne ke liye signed challan / receipt ki photo upload karein ya camera se kheechein.",
    "btn_submit_pod": "Verify & Submit POD",
    "pod_required_alert": "Delivery complete karne ke liye POD photo lagana anivarya hai.",

    // Ratings & Reviews
    "rate_driver_title": "Driver Ko Rate Karein Aur Pay Karein",
    "rate_shipper_title": "Shipper Ko Rate Karein",
    "rating_stars": "Rating Sitare (1 se 5 Stars)",
    "feedback_notes": "Aapka Anubhav / Comments",
    "btn_submit_rating": "Rating Submit Karein & Settle",
    "btn_view_pod": "📄 Proof of Delivery (POD) Dekhein",
    
    // Status Badges
    "status_pending": "KHALI (OPEN FOR CLAIM)",
    "status_posted": "DRIVER KI TALASH",
    "status_assigned": "DRIVER MIL GAYA",
    "status_in_transit": "RASTE MEIN (IN TRANSIT)",
    "status_completed": "PAHUNCH GAYA (DELIVERED)",
    "status_cancelled": "CANCELLED",

    // Audit & Common
    "audit_title": "Activity & Security Audit Trail",
    "audit_time": "Samay (Time)",
    "audit_user": "User / Driver",
    "audit_action": "Action",
    "audit_details": "Details",
    "toast_copied": "Link copy ho gaya!",
    "toast_error": "Kuch gadbad hui. Kripya dobara koshish karein."
  }
};

const I18n = {
  currentLang: localStorage.getItem('apex_lang') || 'en',

  getLang() {
    return this.currentLang;
  },

  setLang(lang) {
    if (lang !== 'en' && lang !== 'hinglish') lang = 'en';
    this.currentLang = lang;
    localStorage.setItem('apex_lang', lang);
    this.applyTranslations();
    document.dispatchEvent(new CustomEvent('apex:langchange', { detail: { lang } }));
  },

  toggleLang() {
    const next = this.currentLang === 'en' ? 'hinglish' : 'en';
    this.setLang(next);
  },

  t(key, fallback = '') {
    if (!key) return '';
    const lKey = key.toLowerCase();
    const dict = APEX_TRANSLATIONS[this.currentLang] || APEX_TRANSLATIONS.en;
    if (dict && (dict[key] !== undefined || dict[lKey] !== undefined)) {
      return dict[key] !== undefined ? dict[key] : dict[lKey];
    }
    if (APEX_TRANSLATIONS.en && (APEX_TRANSLATIONS.en[key] !== undefined || APEX_TRANSLATIONS.en[lKey] !== undefined)) {
      return APEX_TRANSLATIONS.en[key] !== undefined ? APEX_TRANSLATIONS.en[key] : APEX_TRANSLATIONS.en[lKey];
    }
    return fallback;
  },

  applyTranslations(root = document) {
    // 1. Text elements
    root.querySelectorAll('[data-i18n]').forEach(el => {
      const key = el.getAttribute('data-i18n');
      const translation = this.t(key);
      if (translation) {
        if (el.tagName === 'INPUT' && (el.type === 'button' || el.type === 'submit')) {
          el.value = translation;
        } else {
          el.textContent = translation;
        }
      }
    });

    // 2. Placeholders
    root.querySelectorAll('[data-i18n-placeholder]').forEach(el => {
      const key = el.getAttribute('data-i18n-placeholder');
      const translation = this.t(key);
      if (translation) el.setAttribute('placeholder', translation);
    });

    // 3. Titles / tooltips
    root.querySelectorAll('[data-i18n-title]').forEach(el => {
      const key = el.getAttribute('data-i18n-title');
      const translation = this.t(key);
      if (translation) el.setAttribute('title', translation);
    });

    // Update toggle active pill styling across page
    document.querySelectorAll('.apex-lang-toggle-btn').forEach(btn => {
      const targetLang = btn.getAttribute('data-lang');
      if (targetLang === this.currentLang) {
        btn.classList.add('active');
      } else {
        btn.classList.remove('active');
      }
    });
  },

  renderNavbarToggle() {
    const isHinglish = this.currentLang === 'hinglish';
    return `
      <div class="apex-lang-toggle-wrap" title="Switch language / Bhasha badlein">
        <button type="button" class="apex-lang-toggle-btn ${!isHinglish ? 'active' : ''}" data-lang="en" onclick="I18n.setLang('en')">
          EN
        </button>
        <button type="button" class="apex-lang-toggle-btn ${isHinglish ? 'active' : ''}" data-lang="hinglish" onclick="I18n.setLang('hinglish')">
          🇮🇳 Hinglish
        </button>
      </div>
    `;
  },

  getGoogleMapsUrl(pickup, drop, pLat, pLng, dLat, dLng) {
    if (pLat && pLng && dLat && dLng) {
      return `https://www.google.com/maps/dir/?api=1&origin=${pLat},${pLng}&destination=${dLat},${dLng}&travelmode=driving`;
    }
    return `https://www.google.com/maps/dir/?api=1&origin=${encodeURIComponent(pickup || '')}&destination=${encodeURIComponent(drop || '')}&travelmode=driving`;
  }
};

// Global init on DOM load
document.addEventListener('DOMContentLoaded', () => {
  // Inject navbar toggle if container exists
  document.querySelectorAll('.navbar-lang-container').forEach(container => {
    container.innerHTML = I18n.renderNavbarToggle();
  });

  I18n.applyTranslations();
});

window.I18n = I18n;
