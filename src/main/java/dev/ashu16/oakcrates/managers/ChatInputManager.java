package dev.ashu16.oakcrates.managers;

import dev.ashu16.oakcrates.OakCrates;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ChatInputManager {

    private final OakCrates plugin;
    private final Map<UUID, ChatInputRequest> pendingInputs;

    public ChatInputManager(OakCrates plugin) {
        this.plugin = plugin;
        this.pendingInputs = new HashMap<>();
    }

    public void requestInput(Player player, String prompt, Consumer<String> callback) {
        requestInput(player, prompt, callback, null);
    }

    public void requestInput(Player player, String prompt, Consumer<String> callback, Runnable onCancel) {
        UUID uuid = player.getUniqueId();
        
        ChatInputRequest request = new ChatInputRequest(callback, onCancel);
        pendingInputs.put(uuid, request);
        
        player.closeInventory();
        
        if (prompt != null && !prompt.isEmpty()) {
            player.sendMessage(dev.ashu16.oakcrates.utils.ColorUtil.colorize(prompt));
        }
        player.sendMessage(dev.ashu16.oakcrates.utils.ColorUtil.colorize("&7Type '&ccancel&7' to cancel."));
    }

    public boolean hasPendingInput(Player player) {
        return pendingInputs.containsKey(player.getUniqueId());
    }

    public boolean handleInput(Player player, String message) {
        UUID uuid = player.getUniqueId();
        
        ChatInputRequest request = pendingInputs.remove(uuid);
        if (request == null) {
            return false;
        }
        
        if (message.equalsIgnoreCase("cancel")) {
            if (request.getOnCancel() != null) {
                request.getOnCancel().run();
            }
            player.sendMessage(dev.ashu16.oakcrates.utils.ColorUtil.colorize(
                    dev.ashu16.oakcrates.utils.MessageUtil.getMessage("chat-input-cancelled")));
            return true;
        }
        
        request.getCallback().accept(message);
        return true;
    }

    public void cancelInput(Player player) {
        UUID uuid = player.getUniqueId();
        ChatInputRequest request = pendingInputs.remove(uuid);
        
        if (request != null && request.getOnCancel() != null) {
            request.getOnCancel().run();
        }
    }

    public void clearAll() {
        pendingInputs.clear();
    }

    private static class ChatInputRequest {
        private final Consumer<String> callback;
        private final Runnable onCancel;

        public ChatInputRequest(Consumer<String> callback, Runnable onCancel) {
            this.callback = callback;
            this.onCancel = onCancel;
        }

        public Consumer<String> getCallback() {
            return callback;
        }

        public Runnable getOnCancel() {
            return onCancel;
        }
    }
}
