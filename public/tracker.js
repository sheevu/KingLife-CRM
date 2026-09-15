/**
 * Healthy King LIFE - Anonymous Visitor Tracking & Identity Stitching SDK
 * Market: Lucknow, Uttar Pradesh
 */
(function() {
  'use strict';

  const STORAGE_KEY = 'hk_visitor_id';
  const SESSION_KEY = 'hk_session_start';
  const COOKIE_DAYS = 365;

  function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
    return null;
  }

  function setCookie(name, value, days) {
    const d = new Date();
    d.setTime(d.getTime() + (days * 24 * 60 * 60 * 1000));
    const expires = "expires=" + d.toUTCString();
    document.cookie = `${name}=${value};${expires};path=/;SameSite=Lax`;
  }

  function generateUUID() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
      const r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
      return v.toString(16);
    });
  }

  // Retrieve or assign persistent anonymous visitor_id
  let visitorId = null;
  try {
    visitorId = localStorage.getItem(STORAGE_KEY) || getCookie(STORAGE_KEY);
  } catch(e) {}

  if (!visitorId) {
    visitorId = generateUUID();
    try {
      localStorage.setItem(STORAGE_KEY, visitorId);
    } catch(e) {}
    setCookie(STORAGE_KEY, visitorId, COOKIE_DAYS);
  }

  // Session timer
  let sessionStart = Date.now();
  let timeOnSiteSeconds = 0;
  setInterval(() => {
    timeOnSiteSeconds += 15;
    sendEvent('heartbeat', {
      time_on_site_seconds: timeOnSiteSeconds,
      url: window.location.href
    });
  }, 15000);

  function getUtmParams() {
    const params = new URLSearchParams(window.location.search);
    return {
      utm_source: params.get('utm_source') || 'direct',
      utm_medium: params.get('utm_medium') || 'web',
      utm_campaign: params.get('utm_campaign') || 'lucknow_local',
      token: params.get('token') || params.get('wa_token') || null
    };
  }

  function sendEvent(eventType, payload) {
    const utm = getUtmParams();
    const eventBody = {
      visitor_id: visitorId,
      event_type: eventType,
      page_url: window.location.href,
      page_title: document.title,
      referrer: document.referrer || '',
      dwell_time_seconds: timeOnSiteSeconds,
      ...utm,
      ...payload
    };

    // Broadcast in window for UI components (React / Jetpack Compose WebView)
    if (window.dispatchEvent) {
      window.dispatchEvent(new CustomEvent('hk_visitor_event', { detail: eventBody }));
    }

    // Attempt Supabase Edge Function track-visitor if API configured
    if (window.SUPABASE_ANON_KEY && window.SUPABASE_URL) {
      fetch(`${window.SUPABASE_URL}/functions/v1/track-visitor`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'apikey': window.SUPABASE_ANON_KEY,
          'Authorization': `Bearer ${window.SUPABASE_ANON_KEY}`
        },
        body: JSON.stringify(eventBody)
      }).catch(err => console.debug('Track event sent:', eventBody));
    }
  }

  // 1. Initial Page View Tracking
  sendEvent('page_view', {});

  // 2. Global CTA & Link Click Listener
  document.addEventListener('click', function(e) {
    const target = e.target.closest('a, button, [data-track-cta]');
    if (!target) return;

    const ctaText = (target.innerText || target.getAttribute('aria-label') || '').trim();
    const href = target.getAttribute('href') || '';
    const isWhatsApp = href.includes('wa.me') || href.includes('whatsapp.com') || target.dataset.action === 'whatsapp';

    sendEvent(isWhatsApp ? 'whatsapp_cta_click' : 'cta_click', {
      cta_id: target.id || target.dataset.ctaId || 'cta_btn',
      cta_text: ctaText,
      cta_destination: href,
      is_whatsapp: isWhatsApp
    });
  }, true);

  // Global SDK API
  window.HealthyKingTracker = {
    getVisitorId: function() {
      return visitorId;
    },
    getTimeOnSite: function() {
      return timeOnSiteSeconds;
    },
    track: function(eventType, customData) {
      sendEvent(eventType, customData || {});
    },
    identify: function(phone, name, goal, extra) {
      // Merge anonymous visitor data into new lead record
      const identifyData = {
        phone: phone,
        name: name,
        goal: goal,
        event_type: 'identify',
        time_on_site_seconds: timeOnSiteSeconds,
        ...extra
      };
      sendEvent('identify', identifyData);
      console.log(`[HealthyKingTracker] Identified visitor ${visitorId} with phone ${phone}`);
    }
  };
})();
