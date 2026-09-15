// ============================================================================
// Supabase Edge Function: whatsapp-webhook
// Handles Meta WhatsApp Cloud API Webhooks
// 1. GET: Webhook Verification handshake with Meta Graph API
// 2. POST: Message status delivery callbacks (sent, delivered, read, failed)
//          & Interactive button clicks ('Book Evaluation', 'Learn More')
// ============================================================================

import { serve } from "https://deno.land/std@0.168.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2.38.4";

const SUPABASE_URL = Deno.env.get("SUPABASE_URL") || "https://example.supabase.co";
const SUPABASE_SERVICE_ROLE_KEY = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY") || "";
const WHATSAPP_VERIFY_TOKEN = Deno.env.get("WHATSAPP_VERIFY_TOKEN") || "healthy_king_life_lucknow_secret";
const WHATSAPP_ACCESS_TOKEN = Deno.env.get("WHATSAPP_ACCESS_TOKEN") || "";
const WHATSAPP_PHONE_NUMBER_ID = Deno.env.get("WHATSAPP_PHONE_NUMBER_ID") || "";

const supabase = createClient(SUPABASE_URL, SUPABASE_SERVICE_ROLE_KEY);

serve(async (req: Request) => {
  const url = new URL(req.url);

  // 1. Meta Webhook Verification Handshake (GET)
  if (req.method === "GET") {
    const mode = url.searchParams.get("hub.mode");
    const token = url.searchParams.get("hub.verify_token");
    const challenge = url.searchParams.get("hub.challenge");

    if (mode === "subscribe" && token === WHATSAPP_VERIFY_TOKEN) {
      console.log("WhatsApp Webhook Verified Successfully");
      return new Response(challenge, { status: 200, headers: { "Content-Type": "text/plain" } });
    } else {
      console.error("WhatsApp Verification Failed: Invalid Token");
      return new Response("Forbidden", { status: 403 });
    }
  }

  // 2. Process WhatsApp Events (POST)
  if (req.method === "POST") {
    try {
      const body = await req.json();
      console.log("WhatsApp Webhook Payload Received:", JSON.stringify(body, null, 2));

      const entry = body.entry?.[0];
      const changes = entry?.changes?.[0];
      const value = changes?.value;

      if (!value) {
        return new Response(JSON.stringify({ status: "ignored_no_value" }), {
          headers: { "Content-Type": "application/json" },
        });
      }

      // -------------------------------------------------------------
      // 2A. Process Delivery Status Updates (sent, delivered, read, failed)
      // -------------------------------------------------------------
      if (value.statuses && value.statuses.length > 0) {
        for (const statusObj of value.statuses) {
          const wamid = statusObj.id;
          const status = statusObj.status; // 'sent' | 'delivered' | 'read' | 'failed'
          const recipientId = statusObj.recipient_id;
          const timestamp = new Date(parseInt(statusObj.timestamp, 10) * 1000).toISOString();

          console.log(`Updating WhatsApp Message ${wamid} to status: ${status} for ${recipientId}`);

          const updateData: Record<string, any> = {
            status: status,
            updated_at: new Date().toISOString()
          };

          if (status === "delivered") {
            updateData.delivered_at = timestamp;
          } else if (status === "read") {
            updateData.read_at = timestamp;
          } else if (status === "failed") {
            updateData.error_message = statusObj.errors?.[0]?.title || "WhatsApp dispatch failed";
          }

          // Update message record
          const { data: updatedMsg, error: updateError } = await supabase
            .from("whatsapp_messages")
            .update(updateData)
            .eq("wamid", wamid)
            .select()
            .single();

          if (updateError) {
            console.warn("Could not find message by wamid, fallback search by phone:", updateError.message);
            // Fallback match by phone number
            await supabase
              .from("whatsapp_messages")
              .update(updateData)
              .ilike("phone", `%${recipientId.slice(-10)}%`);
          }

          // Log delivery tracking event
          await supabase.from("events").insert({
            event_type: `whatsapp_${status}`,
            event_data: {
              wamid,
              status,
              recipient: recipientId,
              timestamp
            },
            source: "whatsapp_cloud_api"
          });
        }
      }

      // -------------------------------------------------------------
      // 2B. Process Inbound Messages & Interactive Button Responses
      // -------------------------------------------------------------
      if (value.messages && value.messages.length > 0) {
        for (const msg of value.messages) {
          const fromPhone = msg.from;
          const msgType = msg.type;
          const messageId = msg.id;

          let messageText = "";
          let selectedButtonId: string | null = null;
          let selectedButtonTitle: string | null = null;

          if (msgType === "text") {
            messageText = msg.text?.body || "";
          } else if (msgType === "interactive") {
            if (msg.interactive?.type === "button_reply") {
              selectedButtonId = msg.interactive.button_reply.id;
              selectedButtonTitle = msg.interactive.button_reply.title;
              messageText = `[Button Clicked: ${selectedButtonTitle}]`;
            }
          }

          // Save inbound message
          await supabase.from("whatsapp_messages").insert({
            wamid: messageId,
            phone: fromPhone,
            direction: "INBOUND",
            message_text: messageText,
            status: "delivered",
            selected_button_id: selectedButtonId,
            interactive_payload: msg.interactive || null,
            delivered_at: new Date().toISOString()
          });

          // Match or find lead by phone number
          const cleanPhone = fromPhone.slice(-10);
          const { data: matchedLead } = await supabase
            .from("leads")
            .select("*")
            .ilike("phone", `%${cleanPhone}%`)
            .maybeSingle();

          // If lead clicked an interactive button, trigger custom workflow
          if (selectedButtonId && matchedLead) {
            console.log(`Lead ${matchedLead.name} clicked interactive button: ${selectedButtonId}`);

            if (selectedButtonId === "book_eval") {
              // Move lead to 'Appointment Scheduled'
              await supabase
                .from("leads")
                .update({
                  stage: "Appointment Scheduled",
                  lead_score: matchedLead.lead_score + 25,
                  updated_at: new Date().toISOString()
                })
                .eq("id", matchedLead.id);

              // Auto-reply with booking calendar link & Gomti Nagar location
              await sendDirectWhatsAppText(
                fromPhone,
                `Dhanyawaad ${matchedLead.name} Ji! 🙏 Your VIP Evaluation slot is reserved at Healthy King LIFE Lucknow.\n\n📍 Center: Gomti Nagar Commercial Hub, Lucknow\n📅 Tomorrow 11:00 AM\n\nOur Senior Wellness Consultant Dr. Ananya Verma will be ready for your Body Composition Analysis.`
              );

            } else if (selectedButtonId === "learn_more") {
              await sendDirectWhatsAppText(
                fromPhone,
                `Healthy King LIFE Lucknow offers:\n1. Targeted Weight Loss (Average 4-8kg reduction)\n2. Clinical Indian Diet Counselling\n3. Metabolic Reset & Habit Coaching\n\nVisit healthykinglife.in or reply 'YES' to speak with a coach!`
              );
            }

            // Log event
            await supabase.from("events").insert({
              lead_id: matchedLead.id,
              event_type: "whatsapp_button_clicked",
              event_data: {
                button_id: selectedButtonId,
                button_title: selectedButtonTitle,
                phone: fromPhone
              },
              source: "whatsapp_interactive"
            });
          }
        }
      }

      return new Response(JSON.stringify({ success: true }), {
        status: 200,
        headers: { "Content-Type": "application/json" }
      });
    } catch (err: any) {
      console.error("Error processing WhatsApp Webhook:", err);
      return new Response(JSON.stringify({ error: err.message }), {
        status: 500,
        headers: { "Content-Type": "application/json" }
      });
    }
  }

  return new Response("Method Not Allowed", { status: 405 });
});

// Helper function to dispatch quick direct WhatsApp message via Meta Cloud API
async function sendDirectWhatsAppText(toPhone: string, textBody: string) {
  if (!WHATSAPP_ACCESS_TOKEN || !WHATSAPP_PHONE_NUMBER_ID) {
    console.log("Mock WhatsApp Send (Credentials not configured):", { toPhone, textBody });
    return;
  }

  const endpoint = `https://graph.facebook.com/v19.0/${WHATSAPP_PHONE_NUMBER_ID}/messages`;
  try {
    const res = await fetch(endpoint, {
      method: "POST",
      headers: {
        "Authorization": `Bearer ${WHATSAPP_ACCESS_TOKEN}`,
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        messaging_product: "whatsapp",
        recipient_type: "individual",
        to: toPhone.replace("+", "").replace(" ", ""),
        type: "text",
        text: { preview_url: true, body: textBody }
      })
    });
    const result = await res.json();
    console.log("Direct WhatsApp dispatch result:", result);
  } catch (e) {
    console.error("Failed to send direct WhatsApp message:", e);
  }
}
