package com.delitto.izumo.mirai.plugins.impl.admin;

import com.alibaba.fastjson.JSONArray;
import com.delitto.izumo.mirai.plugins.BaseCommandPlugin;
import com.delitto.izumo.mirai.utils.Constants;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotBase;

import java.util.List;

public class BordercastPlugin implements BaseCommandPlugin {
    @Override
    public Msg execute(String[] commands, Bot bot) {
        Msg msg = Msg.builder();

        if(commands.length>1){
            Msg retMsg = Msg.builder();
            retMsg.text(commands[1]);
            boolean isEnable = Constants.groupSubscription.getJSONObject("bordercastGroup").getBoolean("enable");
            if(isEnable) {
                JSONArray groupArray = Constants.groupSubscription.getJSONObject("bordercastGroup").getJSONObject("data").getJSONArray("group");
                JSONArray simpleArray = Constants.groupSubscription.getJSONObject("bordercastGroup").getJSONObject("data").getJSONArray("simple");

                for(int i=0;i<groupArray.size();i++) {
                    long groupAccount = groupArray.getLong(i);
                    bot.sendGroupMsg(groupAccount, retMsg, false);
                }
                for(int i=0;i<simpleArray.size();i++) {
                    long simpleAccount = simpleArray.getLong(i);
                    bot.sendPrivateMsg(simpleAccount, retMsg, false);
                }
                msg.text("已经向所有订阅发送公告");
            } else {
                msg.text("订阅公告功能未启用");
            }

        }
        return msg;
    }

    @Override
    public Msg execute(List<OnebotBase.Message> commands) {
        return null;
    }

    @Override
    public boolean needAtUser() {
        return false;
    }
}
