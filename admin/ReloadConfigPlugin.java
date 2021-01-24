package com.delitto.izumo.mirai.plugins.impl.admin;


import com.alibaba.fastjson.JSONObject;
import com.delitto.izumo.mirai.plugins.BaseCommandPlugin;
import lombok.extern.log4j.Log4j2;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotBase;

import java.lang.reflect.Field;
import java.util.List;

import static com.delitto.izumo.mirai.utils.Constants.COMMAND_ERROR;
import static com.delitto.izumo.mirai.utils.FileUtil.loadJson;

@Log4j2
public class ReloadConfigPlugin implements BaseCommandPlugin {
    @Override
    public Msg execute(String[] commands, Bot bot) {
        Msg msg = Msg.builder();
        try {
            String configName = commands[1];
            JSONObject newObj = loadJson(configName + ".json");
            if(newObj == null) {
                msg.text("[" + configName + ".json]配置重载失败");
            } else {
                try {
                    Class clazz = Class.forName("com.delitto.izumo.mirai.utils.Constants");
                    Field field = clazz.getField(configName);
                    JSONObject obj = (JSONObject) field.get(null);
                    if(obj != null) {
                        obj.clear();
                        obj.putAll(newObj);
                    } else {
                        obj = newObj;
                    }
                    msg.text("[" + configName + ".json]配置重载成功");
                } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ne) {
                    msg.text("[" + configName + ".json]配置重载失败: " + ne);
                }
            }
        } catch (IndexOutOfBoundsException iobe) {
            return COMMAND_ERROR;
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
