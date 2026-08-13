/**
 * APEX TRANSPORT - MULTI-THEME CONTROLLER
 * Modes:
 *  - 'light'   : Ice Blue Day (Very Light Bluish Canvas)
 *  - 'dark'    : Pitch Black OLED (True OLED Black & Neon Cyber Accents)
 *  - 'trucker' : Telemetry HUD Cockpit (Interactive Autonomous Freight HUD)
 */

(function () {
  const THEME_KEY = 'apex_theme_mode';

  function getPreferredTheme() {
    const saved = localStorage.getItem(THEME_KEY);
    if (saved === 'light' || saved === 'dark' || saved === 'trucker') {
      return saved;
    }
    return 'light';
  }

  function applyTheme(theme) {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem(THEME_KEY, theme);
    updateThemeButtons(theme);

    // Refresh map tiles when theme changes if Leaflet map is initialized
    if (window.radarMap && typeof window.radarMap.invalidateSize === 'function') {
      setTimeout(() => window.radarMap.invalidateSize(), 200);
    }
  }

  function updateThemeButtons(theme) {
    const buttons = document.querySelectorAll('.btn-theme-toggle, #theme-toggle-btn, .apex-theme-btn');
    buttons.forEach((btn) => {
      if (theme === 'dark') {
        btn.innerHTML = '🌙';
        btn.setAttribute('title', 'Current: Pitch Black OLED Mode (Click for Telemetry HUD Cockpit Mode)');
      } else if (theme === 'trucker') {
        btn.innerHTML = '🚛';
        btn.setAttribute('title', 'Current: Telemetry HUD Cockpit Mode (Click for Ice Blue Day Mode)');
      } else {
        btn.innerHTML = '☀️';
        btn.setAttribute('title', 'Current: Ice Blue Day Mode (Click for Pitch Black OLED Mode)');
      }
    });
  }

  window.toggleTheme = function () {
    const current = document.documentElement.getAttribute('data-theme') || 'light';
    let next = 'light';
    if (current === 'light') {
      next = 'dark';
    } else if (current === 'dark') {
      next = 'trucker';
    } else {
      next = 'light';
    }
    applyTheme(next);
  };

  window.setTheme = function (theme) {
    if (theme === 'light' || theme === 'dark' || theme === 'trucker') {
      applyTheme(theme);
    }
  };

  // Immediate execution
  const initialTheme = getPreferredTheme();
  applyTheme(initialTheme);

  // Hook DOMContentLoaded to bind event listeners
  window.addEventListener('DOMContentLoaded', () => {
    applyTheme(getPreferredTheme());
  });

  // Mobile Sidebar Drawer Logic
  window.toggleMobileSidebar = function(forceState) {
    const sidebar = document.querySelector('.sidebar');
    const backdrop = document.getElementById('mobileSidebarBackdrop');
    if (!sidebar) return;

    const isOpen = typeof forceState === 'boolean' ? !forceState : sidebar.classList.contains('mobile-open');

    if (!isOpen) {
      sidebar.classList.add('mobile-open');
      if (backdrop) backdrop.classList.add('show');
      document.body.classList.add('drawer-open');
    } else {
      sidebar.classList.remove('mobile-open');
      if (backdrop) backdrop.classList.remove('show');
      document.body.classList.remove('drawer-open');
    }
  };

  window.closeMobileSidebar = function() {
    window.toggleMobileSidebar(false);
  };

  // Close drawer on navigation item selection on mobile
  window.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.sidebar-item').forEach(item => {
      item.addEventListener('click', () => {
        if (window.innerWidth <= 768) {
          window.closeMobileSidebar();
        }
      });
    });
  });
})();
