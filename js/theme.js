/**
 * Apex Transport - Multi-Theme Controller
 * Modes:
 *  - 'dark'        : Night Mode (Deep Obsidian / Midnight Navy - Default)
 *  - 'light'       : Day Mode (Clean White & Slate Gray)
 *  - 'neubrutalism': Neo-Brutalist Multi-Color Paper Mode
 */

(function () {
  const THEME_KEY = 'apex_theme_mode';

  function getPreferredTheme() {
    const saved = localStorage.getItem(THEME_KEY);
    if (saved === 'light' || saved === 'dark' || saved === 'neubrutalism') {
      return saved;
    }
    // Default to premium light mode (Apex Narrative)
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
    const buttons = document.querySelectorAll('.btn-theme-toggle, #theme-toggle-btn');
    buttons.forEach((btn) => {
      if (theme === 'dark') {
        btn.innerHTML = '🌙 <span class="theme-label">Night</span>';
        btn.setAttribute('title', 'Current: Night Mode (Click for Neo-Brutalist Mode)');
      } else if (theme === 'light') {
        btn.innerHTML = '✨ <span class="theme-label">Narrative</span>';
        btn.setAttribute('title', 'Current: Apex Narrative Mode (Click for Night Mode)');
      } else {
        btn.innerHTML = '🎨 <span class="theme-label">Neo-Brutal</span>';
        btn.setAttribute('title', 'Current: Neo-Brutalist (Click for Apex Narrative Mode)');
      }
    });
  }

  window.toggleTheme = function () {
    const current = document.documentElement.getAttribute('data-theme') || 'light';
    let next = 'light';
    if (current === 'light') {
      next = 'dark';
    } else if (current === 'dark') {
      next = 'neubrutalism';
    } else {
      next = 'light';
    }
    applyTheme(next);
  };

  // Immediate execution to prevent flash of dark mode
  const initialTheme = getPreferredTheme();
  applyTheme(initialTheme);

  // Hook DOMContentLoaded to bind event listeners
  window.addEventListener('DOMContentLoaded', () => {
    applyTheme(getPreferredTheme());
  });
})();
