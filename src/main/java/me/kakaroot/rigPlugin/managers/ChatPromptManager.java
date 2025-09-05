package me.kakaroot.rigPlugin.managers;

import me.kakaroot.rigPlugin.RigPlugin;
import org.bukkit.Bukkit;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import java.util.function.Consumer;

public class ChatPromptManager {
    private final RigPlugin plugin;
    private final ConversationFactory factory;

    public ChatPromptManager(RigPlugin plugin) {
        this.plugin = plugin;
        this.factory = new ConversationFactory(plugin)
                .withModality(true)
                .withLocalEcho(false)
                .withEscapeSequence("cancel")
                .thatExcludesNonPlayersWithMessage("Players only!");
    }
    
    public void promptPlayer(Player player, String question, Consumer<String> callback) {
        Conversation convo = factory.withFirstPrompt(new StringPrompt() {
            @Override
            public String getPromptText(ConversationContext context) {
                return question + " (type 'cancel' to quit)";
            }

            @Override
            public Prompt acceptInput(ConversationContext context, String input) {
                if (input.equalsIgnoreCase("cancel")) {
                    if (context.getForWhom() instanceof Player p) {
                        MsgManager.send(p,"&fInput Cancelled.");
                    } else {
                        context.getForWhom().sendRawMessage("Input Cancelled.");
                    }
                    return Prompt.END_OF_CONVERSATION;
                }

                Bukkit.getScheduler().runTask(plugin, () -> callback.accept(input));
                return Prompt.END_OF_CONVERSATION;
            }
        }).buildConversation(player);
        convo.begin();
    }
}

