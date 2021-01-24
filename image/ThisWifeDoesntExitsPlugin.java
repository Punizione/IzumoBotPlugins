package com.delitto.izumo.mirai.plugins.impl.image;

import com.delitto.izumo.mirai.plugins.BaseCommandPlugin;
import com.delitto.izumo.mirai.plugins.api.ThisWifeDoesntExitsApi;
import com.delitto.izumo.mirai.utils.FileUtil;
import com.delitto.izumo.mirai.utils.SendType;
import lombok.extern.log4j.Log4j2;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.utils.Msg;
import okhttp3.ResponseBody;
import onebot.OnebotBase;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import static com.delitto.izumo.mirai.utils.Constants.COMMAND_ERROR;
import static com.delitto.izumo.mirai.utils.Constants.WIFE_DIR;

@Log4j2
public class ThisWifeDoesntExitsPlugin implements BaseCommandPlugin {
    @Override
    public Msg execute(String[] commands, Bot bot) {
        Msg msg = Msg.builder();
        try {
            int rand = new Random().nextInt(100000);
            Retrofit retrofit = new Retrofit.Builder().baseUrl("https://www.thiswaifudoesnotexist.net/").build();
            ThisWifeDoesntExitsApi api = retrofit.create(ThisWifeDoesntExitsApi.class);
            Call<ResponseBody> call = api.getRandomWife(rand);
            try {
                Response<ResponseBody> response = call.execute();
                if (response.isSuccessful()) {

                    String copyName = FileUtil.saveImage(response, SendType.WIFE, WIFE_DIR);
                    if (StringUtils.isNotBlank(copyName)) {
                        msg.image(copyName);
                        log.debug("文件保存成功");
                    } else {
                        log.error("文件复制到酷Q目录失败");
                    }
                }
            } catch (IOException ioe) {
                log.error("文件保存失败", ioe);
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
