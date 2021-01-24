package com.delitto.izumo.mirai.plugins.impl.admin;

import com.delitto.izumo.mirai.plugins.BaseCommandPlugin;
import com.delitto.izumo.mirai.utils.ApplicationContextUtil;

import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotBase;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import java.util.List;

import static com.delitto.izumo.mirai.utils.Constants.COMMAND_ERROR;

public class CleanCoolQCachePlugin implements BaseCommandPlugin {
    @Override
    public Msg execute(String[] commands, Bot bot) {
        Msg msg = Msg.builder();
        try {
            String dataDir = "image";
            if(commands.length > 1) {
                dataDir = commands[1];
            }
            if(StringUtils.isBlank(dataDir)) {
                dataDir = "image";
            }
            Environment enviroment = ApplicationContextUtil.get(Environment.class);
            String currentBot = enviroment.getProperty("bot-config.qq");
            /**
             * @TODO fix cache clear
             */
//            CoolQ cqInstance = CQGlobal.robots.get(Long.parseLong(currentBot));
//            cqInstance.cleanDataDir(dataDir);
            msg.text("清除成功");
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
