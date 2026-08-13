/**
 * Apex Transport Audio Alerts & Live Clock HUD Engine
 * Synthesizes crystal-clear cyber-logistics audio chimes using Web Audio API
 */
const AudioAlerts = {
  audioCtx: null,

  initAudio() {
    if (!this.audioCtx) {
      const AudioContext = window.AudioContext || window.webkitAudioContext;
      if (AudioContext) {
        this.audioCtx = new AudioContext();
      }
    }
    if (this.audioCtx && this.audioCtx.state === 'suspended') {
      this.audioCtx.resume();
    }
  },

  playChime(type = 'notification') {
    try {
      this.initAudio();
      if (!this.audioCtx) return;

      const now = this.audioCtx.currentTime;
      const osc1 = this.audioCtx.createOscillator();
      const osc2 = this.audioCtx.createOscillator();
      const gain = this.audioCtx.createGain();

      gain.connect(this.audioCtx.destination);
      osc1.connect(gain);
      osc2.connect(gain);

      if (type === 'success' || type === 'delivered') {
        // High harmonic ascending chime (C5 -> E5 -> G5)
        osc1.type = 'sine';
        osc2.type = 'triangle';
        osc1.frequency.setValueAtTime(523.25, now); // C5
        osc1.frequency.exponentialRampToValueAtTime(659.25, now + 0.12); // E5
        osc1.frequency.exponentialRampToValueAtTime(783.99, now + 0.25); // G5

        osc2.frequency.setValueAtTime(1046.50, now); // C6
        osc2.frequency.exponentialRampToValueAtTime(1318.51, now + 0.25); // E6

        gain.gain.setValueAtTime(0.2, now);
        gain.gain.exponentialRampToValueAtTime(0.001, now + 0.6);

        osc1.start(now);
        osc2.start(now);
        osc1.stop(now + 0.65);
        osc2.stop(now + 0.65);

      } else if (type === 'transit' || type === 'accepted') {
        // Futuristic double pulse
        osc1.type = 'sine';
        osc1.frequency.setValueAtTime(587.33, now); // D5
        osc1.frequency.exponentialRampToValueAtTime(880.00, now + 0.15); // A5

        gain.gain.setValueAtTime(0.18, now);
        gain.gain.exponentialRampToValueAtTime(0.001, now + 0.45);

        osc1.start(now);
        osc1.stop(now + 0.5);

      } else {
        // Gentle modern notification ping
        osc1.type = 'sine';
        osc1.frequency.setValueAtTime(659.25, now); // E5
        osc1.frequency.exponentialRampToValueAtTime(987.77, now + 0.1); // B5

        gain.gain.setValueAtTime(0.15, now);
        gain.gain.exponentialRampToValueAtTime(0.001, now + 0.35);

        osc1.start(now);
        osc1.stop(now + 0.4);
      }
    } catch (e) {
      console.warn('Audio alert unavailable:', e);
    }
  },

  showNotification(title, message, type = 'notification') {
    this.playChime(type);

    let container = document.getElementById('audio-notif-container');
    if (!container) {
      container = document.createElement('div');
      container.id = 'audio-notif-container';
      container.style.cssText = `
        position: fixed;
        bottom: 24px;
        right: 24px;
        z-index: 99999;
        display: flex;
        flex-direction: column;
        gap: 12px;
        max-width: 380px;
        pointer-events: none;
      `;
      document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = 'card';
    toast.style.cssText = `
      background: var(--bg-surface-elevated);
      backdrop-filter: blur(16px);
      -webkit-backdrop-filter: blur(16px);
      border: 1px solid var(--border-emerald);
      box-shadow: var(--shadow-lg);
      border-radius: var(--radius-lg);
      padding: 16px;
      margin: 0;
      color: var(--text-primary);
      display: flex;
      gap: 12px;
      align-items: flex-start;
      pointer-events: auto;
      animation: slideInUp 0.3s cubic-bezier(0.16, 1, 0.3, 1);
    `;

    const icon = type === 'delivered' || type === 'success' ? '✅' : (type === 'transit' ? '🚚' : '🔔');
    toast.innerHTML = `
      <div style="font-size: 1.5rem;">${icon}</div>
      <div style="flex: 1;">
        <div style="font-weight: 700; font-size: 0.95rem; margin-bottom: 2px; color: var(--emerald-light);">${title}</div>
        <div style="font-size: 0.85rem; color: var(--text-secondary); line-height: 1.4;">${message}</div>
      </div>
      <button style="background:none; border:none; color:var(--text-muted); font-size:16px; cursor:pointer; padding:0;" onclick="this.parentElement.remove()">✕</button>
    `;

    container.appendChild(toast);

    setTimeout(() => {
      if (toast.parentElement) {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(10px)';
        toast.style.transition = 'all 0.3s ease';
        setTimeout(() => toast.remove(), 300);
      }
    }, 6000);
  },

  startLiveClock() {
    function update() {
      const now = new Date();
      const timeStr = now.toLocaleTimeString('en-IN', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false
      });
      const dateStr = now.toLocaleDateString('en-IN', {
        weekday: 'short',
        day: '2-digit',
        month: 'short'
      });

      document.querySelectorAll('.live-hud-clock-time').forEach(el => {
        el.textContent = timeStr;
      });
      document.querySelectorAll('.live-hud-clock-date').forEach(el => {
        el.textContent = dateStr;
      });
    }

    update();
    setInterval(update, 1000);
  }
};

// Enable audio context on first user interaction
document.addEventListener('click', () => AudioAlerts.initAudio(), { once: true });
document.addEventListener('DOMContentLoaded', () => AudioAlerts.startLiveClock());
