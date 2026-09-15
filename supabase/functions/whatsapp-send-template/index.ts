// ============================================================================
// Supabase Edge Function: whatsapp-send-template
// Dispatches approved WhatsApp Cloud API message templates with interactive buttons
// ('Book Evaluation', 'Learn More') to leads and customers in Lucknow.
// ============================================================================

import { serve } from "https://deno.land/std@0.168.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2.38.4";

const SUPABASE_URL = Deno.env.get("SUPABASE_URL") || "https://example.supabase.co";
const SUPABASE_SERVICE_ROLE_KEY = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY") || "";
const WHATSAPP_ACCESS_TOKEN = Deno.env.get("WHATSAPP_ACCESS_TOKEN") || "";
const WHATSAPP_PHONE_NUMBER_ID = Deno.env.get("WHATSAPP_PHONE_NUMBER_ID") || "";

const supabase = createClient(SUPABASE_URL, SUPABASE_SERVICE_ROLE_KEY);

interface SendTemplateRequest {
  to: string; // e.g. "+919415012345"
  template_name?: string; // e.g. "welcome_wellness_lucknow"
  lead_id?: string;
  customer_name: string;
  wellness_goal?: string;
  locality?: string;
  tracking_token?: string;
}

serve(async (req: Request) => {
  // CORS Headers
  const corsHeaders = {
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
  };

  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders });
  }

  try {
    const payload: SendTemplateRequest = await req.json();
    const { to, template_name = "welcome_wellness_lucknow", lead_id, customer_name, wellness_goal = "Weight Management", locality = "Gomti Nagar" } = payload;

    if (!to || !customer_name) {
      return new Response(JSON.stringify({ error: "Missing required fields: to, customer_name" }), {
        status: 400,
        headers: { ...corsHeaders, "Content-Type": "application/json" }
      });
    }

    const cleanPhone = to.replace(/[\s\+\-]/g, "");
    const trackingToken = payload.tracking_token || `wa_tk_${Math.random().toString(36).substring(2, 9)}`;

    // 1. Fetch Template from Supabase Database
    const { data: templateData } = await supabase
      .from("whatsapp_templates")
      .select("*")
      .eq("template_name", template_name)
      .maybeSingle();

    const buttons = templateData?.interactive_buttons || [
      { id: "book_eval", title: "Book Evaluation" },
      { id: "learn_more", title: "Learn More" }
    ];

    const bodyText = `Namaste ${customer_name} Ji! 🙏 Welcome to Healthy King LIFE Weight Loss Wellness Center, Lucknow. We received your request for ${wellness_goal} at our ${locality} center.\n\nPlease select your preferred next step:`;

    let metaWamid: string = `wamid.HB_${Date.now()}_${Math.random().toString(36).substring(2, 6)}`;
    let apiStatus = "sent";

    // 2. Dispatch via Meta WhatsApp Cloud API if credentials are present
    if (WHATSAPP_ACCESS_TOKEN && WHATSAPP_PHONE_NUMBER_ID) {
      const metaUrl = `https://graph.facebook.com/v19.0/${WHATSAPP_PHONE_NUMBER_ID}/messages`;
      
      const whatsappPayload = {
        messaging_product: "whatsapp",
        recipient_type: "individual",
        to: cleanPhone,
        type: "interactive",
        interactive: {
          type: "button",
          header: {
            type: "text",
            text: "Healthy King LIFE Lucknow"
          },
          body: {
            text: bodyText
          },
          footer: {
            text: "Gomti Nagar Commercial Hub, Lucknow"
          },
          action: {
            buttons: buttons.slice(0, 3).map((b: any) => ({
              type: "reply",
              reply: {
                id: b.id,
                title: b.title.substring(0, 20) // WhatsApp limit is 20 chars per button
              }
            }))
          }
        }
      };

      const response = await fetch(metaUrl, {
        method: "POST",
        headers: {
          "Authorization": `Bearer ${WHATSAPP_ACCESS_TOKEN}`,
          "Content-Type": "application/json"
        },
        body: JSON.stringify(whatsappPayload)
      });

      const responseData = await response.json();
      if (response.ok && responseData.messages?.[0]?.id) {
        metaWamid = responseData.messages[0].id;
        apiStatus = "sent";
      } else {
        console.error("Meta Graph API error:", responseData);
        apiStatus = "failed";
      }
    } else {
      console.log("Mock WhatsApp dispatch (using local simulator):", { cleanPhone, bodyText, buttons });
    }

    // 3. Persist Message into Supabase Database
    const { data: insertedMsg, error: insertError } = await supabase
      .from("whatsapp_messages")
      .insert({
        wamid: metaWamid,
        lead_id: lead_id || null,
        phone: to,
        direction: "OUTBOUND",
        message_text: bodyText,
        template_name: template_name,
        status: apiStatus,
        tracking_token: trackingToken,
        interactive_type: "BUTTON",
        interactive_payload: { buttons }
      })
      .select()
      .single();

    if (insertError) {
      console.error("Error inserting WhatsApp message:", insertError);
    }

    // 4. Log Event
    await supabase.from("events").insert({
      lead_id: lead_id || null,
      event_type: "whatsapp_message_dispatched",
      event_data: {
        wamid: metaWamid,
        phone: to,
        template_name,
        tracking_token: trackingToken,
        buttons: buttons.map((b: any) => b.title)
      },
      source: "supabase_edge_function"
    });

    return new Response(
      JSON.stringify({
        success: true,
        wamid: metaWamid,
        tracking_token: trackingToken,
        message: bodyText,
        buttons: buttons,
        status: apiStatus
      }),
      {
        status: 200,
        headers: { ...corsHeaders, "Content-Type": "application/json" }
      }
    );
  } catch (err: any) {
    console.error("Internal Error in whatsapp-send-template:", err);
    return new Response(JSON.stringify({ error: err.message }), {
      status: 500,
      headers: { ...corsHeaders, "Content-Type": "application/json" }
    });
  }
});
