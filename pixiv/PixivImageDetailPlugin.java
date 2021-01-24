package com.delitto.izumo.mirai.plugins.impl.pixiv;

import com.delitto.izumo.mirai.bean.PixivIllustDetail;
import com.delitto.izumo.mirai.plugins.BaseCommandPlugin;
import com.delitto.izumo.mirai.utils.api.PixivApi;
import lombok.extern.log4j.Log4j2;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotBase;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.delitto.izumo.mirai.utils.Constants.COMMAND_ERROR;

@Log4j2
public class PixivImageDetailPlugin implements BaseCommandPlugin {
    @Override
    public Msg execute(String[] commands, Bot bot) {
        Msg msg = Msg.builder();
        try {
            String pid = commands[1];
            if(StringUtils.isNumeric(pid)) {
                PixivIllustDetail detail = PixivApi.getDetailByPid(pid);
                msg = detail.toMsg();
            } else {
                msg = COMMAND_ERROR;
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
