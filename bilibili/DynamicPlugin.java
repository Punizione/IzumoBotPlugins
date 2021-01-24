package com.delitto.izumo.mirai.plugins.impl.bilibili;


import com.delitto.izumo.mirai.bean.BilibiliDynamic;
import com.delitto.izumo.mirai.plugins.BaseCommandPlugin;
import com.delitto.izumo.mirai.plugins.api.BilibiliApi;
import com.delitto.izumo.mirai.utils.Constants;
import com.delitto.izumo.mirai.utils.FileUtil;
import com.delitto.izumo.mirai.utils.SendType;

import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.utils.Msg;
import okhttp3.ResponseBody;
import onebot.OnebotBase;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.util.List;

public class DynamicPlugin implements BaseCommandPlugin {
    @Override
    public Msg execute(String[] commands, Bot bot) {
        Msg msg = Msg.builder();
        Retrofit bilibiliRetrofit = new Retrofit.Builder().baseUrl("https://www.bilibili.com/").build();
        BilibiliApi api = bilibiliRetrofit.create(BilibiliApi.class);
        Call<ResponseBody> call = api.getUserDynamic("233114659", 1);
        List<BilibiliDynamic> dynamicList = com.delitto.izumo.mirai.utils.api.BilibiliApi.getDynamicByUIDInRsshub("233114659", 1);
        for(BilibiliDynamic dynamic: dynamicList) {
            dynamic.parseContent();
            msg.text(dynamic.getText());
            msg.text("\n");
            try{
                for(String url: dynamic.getImageUrls()) {
                    call = api.getLink(url);
                    Response<ResponseBody> response = call.execute();
                    msg.image(FileUtil.saveImage(response, SendType.TEMP, Constants.TEMP_DIR));
                }
            } catch (Exception e) {

            }
            msg.text("\n");
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
