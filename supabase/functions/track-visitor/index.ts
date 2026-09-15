// ============================================================================
// Supabase Edge Function: track-visitor
// Handles Anonymous Visitor Tracking & Identity Stitching:
// - Assigns / validates persistent visitor_id
// - Tracks page views, time on site, and CTA button clicks
// - Automatically stitches anonymous activity into newly created leads
// ============================================================================

import { serve } from "https://deno.land/std@0.168.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2.38.4";

const SUPABASE_URL = Deno.env.get("SUPABASE_URL") || "https://example.supabase.co";
const SUPABASE_SERVICE_ROLE_KEY = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY") || "";

const supabase = createClient(SUPABASE_URL, SUPABASE_SERVICE_ROLE_KEY);

interface VisitorTrackPayload {
  visitor_id?: string;
  cookie_id?: string;
  event_type: "page_view" | "heartbeat" | "cta_click" | "form_submit" | "identify";
  page_url: string;
  page_title?: string;
  referrer?: string;
  dwell_time_seconds?: number;
  cta_id?: string;
  cta_text?: string;
  utm_source?: string;
  utm_campaign?: string;
  phone?: string;
  lead_id?: string;
  name?: string;
  goal?: string;
}

serve(async (req: Request) => {
  const corsHeaders = {
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
  };

  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders });
  }

  try {
    const data: VisitorTrackPayload = await req.json();
    let visitorId = data.visitor_id;

    // 1. Ensure or Upsert Visitor record
    if (!visitorId) {
      const { data: newVisitor, error: visErr } = await supabase
        .from("visitors")
        .insert({
          cookie_id: data.cookie_id || `hk_vis_${Math.random().toString(36).substring(2, 10)}`,
          landing_page: data.page_url,
          utm_source: data.utm_source || "direct",
          utm_campaign: data.utm_campaign || "organic",
          city: "Lucknow"
        })
        .select("visitor_id")
        .single();

      if (newVisitor) {
        visitorId = newVisitor.visitor_id;
      }
    } else {
      // Update last_visit
      await supabase
        .from("visitors")
        .update({
          last_visit: new Date().toISOString(),
          number_of_visits: supabase.rpc("increment", { row_id: visitorId })
        })
        .eq("visitor_id", visitorId);
    }

    // 2. Track Event Type
    if (data.event_type === "page_view") {
      await supabase.from("visitor_page_views").insert({
        visitor_id: visitorId,
        page_url: data.page_url,
        page_title: data.page_title || "",
        referrer: data.referrer || "",
        dwell_time_seconds: data.dwell_time_seconds || 0,
        utm_source: data.utm_source,
        utm_campaign: data.utm_campaign
      });
    } else if (data.event_type === "cta_click") {
      await supabase.from("visitor_cta_clicks").insert({
        visitor_id: visitorId,
        lead_id: data.lead_id || null,
        cta_id: data.cta_id || "general_button",
        cta_text: data.cta_text || "Click",
        page_url: data.page_url
      });
    }

    // 3. Identity Stitching when phone or lead_id is provided
    let mergeResult = null;
    if (data.phone && visitorId) {
      let targetLeadId = data.lead_id;

      // Find or create lead if not already provided
      if (!targetLeadId) {
        const { data: leadMatch } = await supabase
          .from("leads")
          .select("id")
          .ilike("phone", `%${data.phone.slice(-10)}%`)
          .maybeSingle();

        if (leadMatch) {
          targetLeadId = leadMatch.id;
        } else {
          // Create new lead record
          const { data: newLead } = await supabase
            .from("leads")
            .insert({
              visitor_id: visitorId,
              name: data.name || "Lucknow Visitor",
              phone: data.phone,
              goal: data.goal || "Weight Management",
              stage: "Lead Captured",
              lead_score: 30,
              locality: "Gomti Nagar"
            })
            .select("id")
            .single();

          if (newLead) targetLeadId = newLead.id;
        }
      }

      if (targetLeadId) {
        // Call the database function to stitch all historical visitor events to this lead
        const { data: stitchData, error: stitchErr } = await supabase.rpc("merge_visitor_data", {
          p_visitor_id: visitorId,
          p_lead_id: targetLeadId,
          p_phone: data.phone
        });

        mergeResult = stitchData;
        console.log("Visitor data stitched successfully:", stitchData);
      }
    }

    return new Response(
      JSON.stringify({
        success: true,
        visitor_id: visitorId,
        merged: !!mergeResult,
        merge_details: mergeResult
      }),
      {
        status: 200,
        headers: { ...corsHeaders, "Content-Type": "application/json" }
      }
    );
  } catch (err: any) {
    console.error("Error tracking visitor:", err);
    return new Response(JSON.stringify({ error: err.message }), {
      status: 500,
      headers: { ...corsHeaders, "Content-Type": "application/json" }
    });
  }
});
