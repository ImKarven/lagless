package me.karven.lagless;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

@NullMarked
public class LaglessPaperCommand {
    private static final Component RELOAD_CONFIG_SUCCESS_MESSAGE = Component.text("Config reloaded successfully").color(NamedTextColor.GREEN);
    private static final Component RELOAD_CONFIG_FAILED_MESSAGE = Component.text("Failed to reload config").color(NamedTextColor.RED);

    private static final Builder RELOAD_COMMAND =
            Builder.builder("reload")
                    .executes(context -> LaglessPaperMain.instance().reloadConfigAsync()
                            .whenComplete((result, exception) -> {
                                final CommandSender executor = getExecutorElseSender(context.getSource());
                                if (exception != null) {
                                    executor.sendMessage(RELOAD_CONFIG_FAILED_MESSAGE);
                                    return;
                                }

                                switch (result) {
                                    case FAILED -> executor.sendMessage(RELOAD_CONFIG_FAILED_MESSAGE);

                                    case SUCCESS -> executor.sendMessage(RELOAD_CONFIG_SUCCESS_MESSAGE);
                                }
                            }));

    private static final Builder ROOT_COMMAND =
            Builder.builder("lagless")
                    .requires(source -> getExecutorElseSender(source).isOp())
                    .next(RELOAD_COMMAND);

    public static LiteralArgumentBuilder<CommandSourceStack> getRootCommand() {
        return ROOT_COMMAND.build();
    }


    static class Builder {
        private final String label;
        private @Nullable Predicate<CommandSourceStack> requires;
        private @Nullable Command<CommandSourceStack> executes;
        private @Nullable Builder next;

        static Builder builder(final String label) {
            return new Builder(label);
        }

        private Builder(final String label) {
            this.label = label;
        }

        @Nullable Command<CommandSourceStack> executes() {
            return this.executes;
        }

        Builder executes(final Consumer<CommandContext<CommandSourceStack>> executes) {
            this.executes = context -> {
                executes.accept(context);
                return Command.SINGLE_SUCCESS;
            };
            return this;
        }

        @Nullable Predicate<CommandSourceStack> requires() {
            return this.requires;
        }

        Builder requires(final Predicate<CommandSourceStack> requires) {
            this.requires = requires;
            return this;
        }

        @Nullable Builder next() {
            return this.next;
        }

        Builder next(final Builder next) {
            this.next = next;
            return this;
        }

        LiteralArgumentBuilder<CommandSourceStack> build() {
            final LiteralArgumentBuilder<CommandSourceStack> builtCommand = Commands.literal(label);
            if (requires != null) builtCommand.requires(requires);
            if (executes != null) builtCommand.executes(executes);
            if (next != null) builtCommand.then(next.build());
            return builtCommand;
        }
    }

    private static CommandSender getExecutorElseSender(final CommandSourceStack source) {
        if (source.getExecutor() == null) return source.getSender();
        return source.getExecutor();
    }
}
