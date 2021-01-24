package com.delitto.izumo.mirai.plugins.impl.azurlane;

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
 * 舰娘信息查询
 * @author Delitto
 *
 */
public class ShipInfoPlugin implements BaseCommandPlugin {
    @Override
    public Msg execute(String[] commands, Bot bot) {
        Msg msg = Msg.builder();
        try {
            String shipGirlName = commands[1];
            if (Constants.shipGirlMap.containsKey(shipGirlName)) {
                shipGirlName = Constants.shipGirlMap.getString(shipGirlName);
            }
            Retrofit retrofit = new Retrofit.Builder().baseUrl("https://wiki.biligame.com/").build();
            AzurLaneApi api = retrofit.create(AzurLaneApi.class);
            Call<ResponseBody> call = api.getInfo(shipGirlName);
            try {
                Response<ResponseBody> response = call.execute();
                if (response.isSuccessful()) {
                    Document doc = Jsoup.parse(response.body().string());

                    Elements els = Xsoup.select(doc, "//*[@id=\"mw-content-text\"]/div/div[6]/div[1]/table[1]/tbody/tr[2]/td[1]/img").getElements();
                    if (els.size() > 0) {
                        msg.text("查询到[" + shipGirlName + "]的信息如下:\n");
                        for (Element ele : els) {
                            call = api.getLink(ele.attr("src"));
                            response = call.execute();
                            String retName = saveImage(response, SendType.TEMP, TEMP_DIR);
                            if (StringUtils.isNotBlank(retName)) {
                                msg.image(retName);
                            }
                        }
                        msg.text("\n");
                        String shipType = Xsoup.select(doc, "//*[@id=\"mw-content-text\"]/div/div[6]/div[1]/table[1]/tbody/tr[2]/td[5]/a").getElements().attr("title");
                        String camp = Xsoup.select(doc, "//*[@id=\"mw-content-text\"]/div/div[6]/div[1]/table[1]/tbody/tr[3]/td[4]/a").getElements().attr("title");
                        String rareType = Xsoup.select(doc, "//*[@id=\"mw-content-text\"]/div/div[6]/div[1]/table[1]/tbody/tr[3]/td[2]").getElements().text();
                        String buildTime = Xsoup.select(doc, "//*[@id=\"mw-content-text\"]/div/div[6]/div[1]/table[1]/tbody/tr[4]/td[2]").getElements().text();
                        String dropNormal = Xsoup.select(doc, "//*[@id=\"mw-content-text\"]/div/div[6]/div[1]/table[1]/tbody/tr[5]/td[2]").getElements().text();
                        String dropSpeaial = Xsoup.select(doc, "//*[@id=\"mw-content-text\"]/div/div[6]/div[1]/table[1]/tbody/tr[6]/td[2]").getElements().text();

                        msg.text("类型:").text(shipType).text("\n");
                        msg.text("阵营:").text(camp).text("\n");
                        msg.text("稀有度:").text(rareType).text("\n");
                        msg.text("建造时间:").text(buildTime).text("\n");
                        msg.text("普通掉落点:").text(dropNormal).text("\n");
                        msg.text("活动掉落点:").text(dropSpeaial);
                    } else {
                        msg.text("查询不到[" + shipGirlName + "]的推荐配装呢");
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
        return true;
    }

}
