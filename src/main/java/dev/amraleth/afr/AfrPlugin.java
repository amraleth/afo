package dev.amraleth.afr;

import dev.amraleth.afr.listener.FishingListener;
import lombok.Getter;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class of the Amraleth's Fishing Revamp plugin
 *
 * @author amraleth
 */
public class AfrPlugin extends JavaPlugin {
    public static final Logger LOGGER;
    public static final String VERSION;
    public static final Boolean IS_DEBUG;

    public static final String NAMESPACE = "afr";

    public static final MiniMessage MINI_MESSAGE = MiniMessage.builder()
            .postProcessor(comp -> comp.decoration(TextDecoration.ITALIC, false))
            .build();

    static {
        LOGGER = LoggerFactory.getLogger(AfrPlugin.class);
        VERSION = "1.0-alpha";
        //IS_DEBUG = System.getenv("AFR_DEBUG") != null;
        IS_DEBUG = true;
    }


    @Getter
    private PluginManager pluginManager;

    @Override
    public void onEnable() {
        sendDebugMessage("Running in debug mode.");

        LOGGER.info("Registering events.");
        this.pluginManager = getServer().getPluginManager();
        this.pluginManager.registerEvents(new FishingListener(this), this);
    }

    /**
     * Sends a message to the console that is only enabled when the debug mode is toggled on
     *
     * @param message      The message to send
     * @param replacements Optional replacements
     */
    public static void sendDebugMessage(@NotNull String message, @Nullable Object... replacements) {
        if (IS_DEBUG) {
            LOGGER.info(message, replacements);
        }
    }
}
