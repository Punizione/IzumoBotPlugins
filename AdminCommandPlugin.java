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

/**
 * bot管理员专用指令逻辑
 */
@Component
@Log4j2
public class AdminCommandPlugin extends BotPlugin {
    @Value("${bot-config.admin}")
    private long adminQQ;

    @Value("${bot-config.command-prefix}")
    private String commandPrefix;

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {
        long userId = event.getUserId();
        String msg = event.getRawMessage();
        if(userId == adminQQ) {
            if(StringUtils.isNotBlank(msg)) {
                if(msg.startsWith(commandPrefix)) {
                    String[] commands = CommandUtil.split(msg);
                    log.info("split commands:[{}]" ,String.join(",", commands));
                    if(commands != null) {
                        try {
                            BaseCommandPlugin plugin = PluginFactory.getPlugin(commands[0], 1);
                            if(plugin != null) {
                                log.info("get command form admin, msg=" + msg);
                                Msg retMsg = plugin.execute(commands, bot);
                                bot.sendPrivateMsg(userId, retMsg, false);
                            } else {
                                return MESSAGE_IGNORE;
                            }
                        } catch (Exception e) {
                            log.error(e);
                        }
                        return MESSAGE_IGNORE;
                    } else {
                        return MESSAGE_IGNORE;
                    }
                } else {
                    return MESSAGE_IGNORE;
                }
            }
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        String msg = event.getRawMessage();
        long groupId = event.getGroupId();
        long userId = event.getUserId();
        if(userId == adminQQ) {
            if(StringUtils.isNotBlank(msg)) {
                if(msg.startsWith(commandPrefix)) {

                    String[] commands = CommandUtil.split(msg);
                    if(commands != null) {
                        try {
                            BaseCommandPlugin plugin = PluginFactory.getPlugin(commands[0], 1);
                            if(plugin != null) {
                                log.info("get command form admin, msg=" + msg);
                                Msg retMsg = plugin.execute(commands, bot);
                                bot.sendGroupMsg(groupId, retMsg, false);
                            }
                        } catch (Exception e) {
                            log.error(e);
                        }
                        return MESSAGE_IGNORE;
                    } else {
                        return MESSAGE_IGNORE;
                    }
                } else {
                    return MESSAGE_IGNORE;
                }
            }
        }
        return MESSAGE_IGNORE;
    }
}
