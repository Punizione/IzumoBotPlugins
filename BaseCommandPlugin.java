package com.delitto.izumo.mirai.plugins;

import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotBase;

import java.util.List;

public interface BaseCommandPlugin {
    public Msg execute(String[] commands, Bot bot);
    Msg execute(List<OnebotBase.Message> commands);
    public boolean needAtUser();
}
