package com.delitto.izumo.mirai.plugins.impl.pixiv;

import com.delitto.izumo.mirai.plugins.BaseCommandPlugin;
import com.delitto.izumo.mirai.utils.api.PixivApi;
import lombok.extern.log4j.Log4j2;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotBase;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.delitto.izumo.mirai.utils.Constants.*;

@Log4j2
public class SeTuPlugin implements BaseCommandPlugin {
    @Override
    public Msg execute(String[] commands, Bot bot) {
        Msg msg = Msg.builder();
        try {
            int needCount = 1;
            if(commands.length >1) {
                try{
                    needCount = Integer.parseInt(commands[1]);
                    if(needCount > MAX_IMAGE_REQUEST_COUNT) {
                        return TOO_MANY_IMAGE_REQUEST;
                    }
                } catch (NumberFormatException nfe) {
                    return COMMAND_ERROR;
                }
            }
            ArrayList<String> imageName = PixivApi.randomSeTu(needCount);
            for(String name: imageName) {
                if (StringUtils.isNotBlank(name)) {
                    msg.image(name);
                    log.debug("文件保存成功");
                } else {
                    log.error("文件复制到酷Q目录失败");
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
