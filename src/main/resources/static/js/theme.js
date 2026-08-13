/**
 * APEX TRANSPORT - MULTI-THEME ENGINE (DAY HIGHWAY / NIGHT DISPATCH / INDUSTRIAL TRUCKER)
 * Modes:
 *  - 'light'   : Day Highway (Clean Emerald & White)
 *  - 'dark'    : Night Dispatch (Midnight Cyber Navy)
 *  - 'trucker' : Industrial Heavy Freight (Safety Amber & Steel Metallic)
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
        btn.innerHTML = '🌙 <span class="theme-label">Night Dispatch</span>';
        btn.setAttribute('title', 'Current: Night Mode (Click for Industrial Trucker Mode)');
      } else if (theme === 'trucker') {
        btn.innerHTML = '🚚 <span class="theme-label">Industrial Trucker</span>';
        btn.setAttribute('title', 'Current: Industrial Heavy Freight (Click for Day Mode)');
      } else {
        btn.innerHTML = '☀️ <span class="theme-label">Day Highway</span>';
        btn.setAttribute('title', 'Current: Day Highway Mode (Click for Night Dispatch Mode)');
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
})();
