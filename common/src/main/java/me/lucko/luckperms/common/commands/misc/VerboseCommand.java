/*
 * Copyright (c) 2016 Lucko (Luck) <luck@lucko.me>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.luckperms.common.commands.misc;

import com.google.common.collect.ImmutableList;

import me.lucko.luckperms.common.LuckPermsPlugin;
import me.lucko.luckperms.common.commands.Arg;
import me.lucko.luckperms.common.commands.CommandResult;
import me.lucko.luckperms.common.commands.SingleCommand;
import me.lucko.luckperms.common.commands.sender.Sender;
import me.lucko.luckperms.common.constants.Message;
import me.lucko.luckperms.common.constants.Permission;
import me.lucko.luckperms.common.utils.Predicates;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VerboseCommand extends SingleCommand {
    public VerboseCommand() {
        super("Verbose", "Enable verbose permission check output", "/%s verbose <true|false> [filters...]", Permission.VERBOSE, Predicates.is(0),
                Arg.list(
                        Arg.create("true|false|record|paste", true, "whether to enable/disable logging, or start/stop recording"),
                        Arg.create("filters...", false, "the name of the user / start of the node to filter by")
                )
        );
    }

    @Override
    public CommandResult execute(LuckPermsPlugin plugin, Sender sender, List<String> args, String label) {
        if (args.isEmpty()) {
            sendUsage(sender, label);
            return CommandResult.INVALID_ARGS;
        }

        String mode = args.get(0).toLowerCase();

        if (mode.equals("on") || mode.equals("true")) {
            List<String> filters = new ArrayList<>();
            if (args.size() != 1) {
                filters.addAll(args.subList(1, args.size()));
            }


            plugin.getDebugHandler().register(sender, filters);
            if (!filters.isEmpty()) {
                Message.VERBOSE_ON_QUERY.send(sender, filters.stream().collect(Collectors.joining("&7, &f")));
            } else {
                Message.VERBOSE_ON.send(sender);
            }

            return CommandResult.SUCCESS;
        }

        if (mode.equals("off") || mode.equals("false")) {
            plugin.getDebugHandler().unregister(sender.getUuid());
            Message.VERBOSE_OFF.send(sender);
            return CommandResult.SUCCESS;
        }

        if (mode.equals("record")) {
            List<String> filters = new ArrayList<>();
            if (args.size() != 1) {
                filters.addAll(args.subList(1, args.size()));
            }

            plugin.getDebugHandler().setPastedFilters(ImmutableList.copyOf(filters));
            plugin.getDebugHandler().setPasted(true);

            if (!filters.isEmpty()) {
                Message.VERBOSE_RECORDING_ON_QUERY.send(sender, filters.stream().collect(Collectors.joining("&7, &f")));
            } else {
                Message.VERBOSE_RECORDING_ON.send(sender);
            }

            return CommandResult.SUCCESS;
        }

        if (mode.equals("paste")) {
            plugin.getDebugHandler().setPasted(false);
            Message.VERBOSE_RECORDING_UPLOAD_START.send(sender);

            String url = plugin.getDebugHandler().uploadPastedData();
            if (url == null) {
                url = "null";
            }

            Message.VERBOSE_RECORDING_URL.send(sender, url);
            return CommandResult.SUCCESS;
        }

        sendUsage(sender, label);
        return CommandResult.INVALID_ARGS;
    }
}
