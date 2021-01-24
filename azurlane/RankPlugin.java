package com.delitto.izumo.mirai.plugins.impl.azurlane;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.delitto.izumo.mirai.plugins.BaseCommandPlugin;
import com.delitto.izumo.mirai.plugins.api.AzurLaneApi;
import com.delitto.izumo.mirai.utils.Constants;
import com.delitto.izumo.mirai.utils.SendType;
import lombok.extern.log4j.Log4j2;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.utils.Msg;
import okhttp3.ResponseBody;
import onebot.OnebotBase;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import us.codecraft.xsoup.Xsoup;

import java.io.IOException;
import java.util.List;

import static com.delitto.izumo.mirai.utils.Constants.COMMAND_ERROR;
import static com.delitto.izumo.mirai.utils.Constants.TEMP_DIR;
import static com.delitto.izumo.mirai.utils.FileUtil.saveImage;

@Log4j2
/**
 * 节奏榜
 * @author Delitto
 *
 */
public class RankPlugin implements BaseCommandPlugin {
    @Override
    public Msg execute(String[] commands, Bot bot) {
        Msg msg = Msg.builder();
        try {
            String rankType = "pve";
            if(commands.length > 1){
                rankType = commands[1];
            }
            JSONObject rankTypeObj = Constants.rankTypeMap.getJSONObject(rankType.toLowerCase());
            if(rankTypeObj == null) {
                return COMMAND_ERROR;
            }
            String url = rankTypeObj.getString("url");
            JSONArray xpathArray = rankTypeObj.getJSONArray("xpath");
            Retrofit retrofit = new Retrofit.Builder().baseUrl("https://wiki.biligame.com/").build();
            AzurLaneApi api = retrofit.create(AzurLaneApi.class);
            Call<ResponseBody> call = api.getInfo(url);
            try {
                Response<ResponseBody> response = call.execute();
                if (response.isSuccessful()) {
                    Document doc = Jsoup.parse(response.body().string());
                    for(int i = 0; i<xpathArray.size() ; i++) {
                        Elements els = Xsoup.select(doc, xpathArray.getString(i)).getElements();
                        for (Element ele : els) {
                            call = api.getLink(ele.attr("src"));
                            response = call.execute();
                            String retName = saveImage(response, SendType.TEMP, TEMP_DIR);
                            if (StringUtils.isNotBlank(retName)) {
                                msg.image(retName);
                            }
                        }
                    }

                }
            } catch (IOException ioe) {
                log.error("请求失败", ioe);
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
