package azaniam.co.za.my_first_agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController
public class ChatController {

    private final ChatClient chatClient;
    private final SowetoTools sowetoTools;

    public ChatController(ChatClient.Builder builder, SowetoTools sowetoTools) {
        this.sowetoTools = sowetoTools;
        this.chatClient = builder
                .defaultSystem("""
                    You are Thabo's day-one homie from Soweto, a real Amapiano DJ and hardcore fan.
                    Talk like a Soweto homie, not like a textbook.
                    Use Soweto slang naturally: sharp sharp, howzit, eish, lekker, chillas, my guy, yoh, ayoba, groove, kasi, mzansi.
                    Be warm, funny, hype, and a bit cheeky. Call him Thabo or my guy.
                    You LOVE Amapiano. Your favourite artists are Kabza De Small, DJ Maphorisa, Kelvin Momo, MFR Souls, Young Stunna, Daliwonga.
                    Keep answers SHORT and vibey - 2 to 4 sentences max, unless he asks for more.
                    Never say "As an AI language model".
                    """)
                .build();
    }

    @PostMapping("/chat")
    public String chatPost(@RequestBody String message) {
        return doChat(message);
    }

    @GetMapping("/chat")
    public String chatGet(@RequestParam String message) {
        return doChat(message);
    }

    private String doChat(String message) {
        String lower = message.toLowerCase();
        String extraInfo = "";

        // MANUAL TOOLS - we call them ourselves, no automatic function-calling
        if (lower.contains("weather")) {
            extraInfo = "\n[REAL TOOL DATA - USE THIS]: " + sowetoTools.getWeatherInSoweto();
        } else if (lower.contains("time") || lower.contains("what time")) {
            extraInfo = "\n[REAL TOOL DATA - USE THIS]: " + sowetoTools.getTimeInSoweto();
        } else if (lower.contains("recommend") || lower.contains("song") || lower.contains("track") || lower.contains("amapiano")) {
            extraInfo = "\n[REAL TOOL DATA - USE THIS]: " + sowetoTools.recommendAmapiano(message);
        }

        return chatClient.prompt()
                .user(message + extraInfo)
                .call()
                .content();
    }
}