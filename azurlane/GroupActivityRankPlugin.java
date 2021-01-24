package com.delitto.izumo.mirai.plugins.impl.azurlane;


import com.alibaba.fastjson.JSONObject;
import com.delitto.izumo.mirai.plugins.BaseCommandPlugin;
import com.delitto.izumo.mirai.utils.CommandUtil;
import com.delitto.izumo.mirai.utils.Constants;
import com.delitto.izumo.mirai.utils.FileUtil;
import com.delitto.izumo.mirai.utils.SortedPair;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotBase;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.delitto.izumo.mirai.utils.Constants.COMMAND_ERROR;

public class GroupActivityRankPlugin implements BaseCommandPlugin {

    @Override
    public Msg execute(List<OnebotBase.Message> commands) {
        Msg msg = Msg.builder();
        JSONObject rankObj = Constants.groupRank;
        if(commands.size() == 3 ) {
            Long qq = CommandUtil.getAtedQQ(commands);
            if (qq != 0l) {
                String numberPick = commands.get(2).getDataMap().get("text");
                int pick = 0;
                try{
                    pick = Integer.parseInt(numberPick);
                } catch (NumberFormatException nfe) {
                    msg = COMMAND_ERROR;
                }
                rankObj.put(String.valueOf(qq), pick);
                FileUtil.writeJson(rankObj, "groupRank");
                msg.text("添加成功!当前排名:\n");
                List<SortedPair> rankMap = new ArrayList<>();
                for(String qqKey : rankObj.keySet()) {
                    rankMap.add(new SortedPair(Long.parseLong(qqKey), rankObj.getInteger(qqKey)));
                }
                Collections.sort(rankMap);
                int index = 1;
                for(SortedPair pair: rankMap){
                    msg.text(index + ". " ).at(pair.getQq()) .text(" 数值差距:" + pair.getPick() + "\n");
                    index ++;
                }
            } else {
                msg = COMMAND_ERROR;
            }
        } else if(commands.size() == 1) {
            List<SortedPair> rankMap = new ArrayList<>();
            for(String qqKey : rankObj.keySet()) {
                rankMap.add(new SortedPair(Long.parseLong(qqKey), rankObj.getInteger(qqKey)));
            }
            Collections.sort(rankMap);
            int index = 1;
            msg.text("当前排名:\n");
            for(SortedPair pair: rankMap){
                msg.text(index + ". " ).at(pair.getQq()) .text(" 数值差距:" + pair.getPick() + "\n");
                index ++;
            }
        }
        return msg;
    }

    @Override
    public Msg execute(String[] commands, Bot bot) {
        return null;
    }

    @Override
    public boolean needAtUser() {
        return false;
    }
}
