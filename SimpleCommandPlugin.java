package com.delitto.izumo.mirai.plugins;

import com.delitto.izumo.mirai.utils.CommandUtil;
import com.delitto.izumo.mirai.utils.PluginFactory;
import lombok.extern.log4j.Log4j2;

import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class SimpleCommandPlugin extends BotPlugin {

        @Value("${bot-config.command-prefix}")
        private String commandPrefix;

        @Override
        public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {
            long userId = event.getUserId();
            String msg = event.getRawMessage();
            if(StringUtils.isNotBlank(msg)) {
                if(msg.startsWith(commandPrefix)) {
                    String[] commands = CommandUtil.split(msg);
                    if(commands != null) {
                        try {
                            BaseCommandPlugin plugin = PluginFactory.getPlugin(commands[0], 0);
                            if(plugin != null) {
                                log.info("get command form "+ userId +", msg=" + msg);
                                Msg retMsg = plugin.execute(commands, bot);
                                bot.sendPrivateMsg(userId, retMsg, false);
                            }
                        } catch (Exception e) {
                            log.error(e);
                        }
                        return MESSAGE_BLOCK;
                    } else {
                        return MESSAGE_IGNORE;
                    }
                } else {
                    return MESSAGE_IGNORE;
                }
            }
            return MESSAGE_IGNORE;
        }

        @Override
        public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
            String msg = event.getRawMessage();
            long groupId = event.getGroupId();
            long userId = event.getUserId();
            if(StringUtils.isNotBlank(msg)) {
                if(msg.startsWith(commandPrefix)) {
                    String[] commands = CommandUtil.split(msg);
                    if(commands != null) {
                        try {
                            BaseCommandPlugin plugin = PluginFactory.getPlugin(commands[0], 0);
                            if(plugin != null) {
                                log.info("get command form "+ userId +", msg=" + msg);
                                Msg retMsg = plugin.execute(commands, bot);
                                if(plugin.needAtUser()) {
                                    retMsg.getMessageChain().add(0, Msg.builder().at(userId).build().get(0));
                                }
                                bot.sendGroupMsg(groupId, retMsg, false);
                            }
                        } catch (Exception e) {
                            log.error(e);
                        }
                        return MESSAGE_BLOCK;
                    } else {
                        return MESSAGE_IGNORE;
                    }
                } else {
                    return MESSAGE_IGNORE;
                }
            }
            return MESSAGE_IGNORE;
        }
}
