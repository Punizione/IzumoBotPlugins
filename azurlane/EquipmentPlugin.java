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

import static com.delitto.izumo.mirai.utils.Constants.*;
import static com.delitto.izumo.mirai.utils.FileUtil.saveImage;

@Log4j2
/**
 * 舰娘装备推荐
 * @author Delitto
 *
 */
public class EquipmentPlugin implements BaseCommandPlugin {

    @Override
    public boolean needAtUser() {
        return true;
    }

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
                    Elements els = Xsoup.select(doc, "//*[@class=\"pverecommend1\"]//img").getElements();
                    if (els.size() > 0) {
                        msg.text("查询到[" + shipGirlName + "]的推荐配装如下:\n");
                        for (Element ele : els) {
                            call = api.getLink(ele.attr("src").replace("/30px-", "/45px-"));
                            response = call.execute();
                            String retName = saveImage(response, SendType.TEMP, TEMP_DIR);
                            if (StringUtils.isNotBlank(retName)) {
                                msg.image(retName);
                            }
                        }
                        msg.text("\n");
                        StringBuilder equipInfo1 = new StringBuilder("");
                        els = Xsoup.select(doc, "//*[@class=\"pverecommend1\"]//tr[2]/td[1]").getElements();
                        els.forEach(element -> {
                            equipInfo1.append(element.text().replace("\n", ""));
                        });
                        msg.text(equipInfo1.toString());
                        msg.text("\n");

                        els = Xsoup.select(doc, "//*[@class=\"pverecommend2\"]//img").getElements();
                        if (els.size() > 0) {
                            for (Element ele : els) {
                                call = api.getLink(ele.attr("src").replace("/30px-", "/45px-"));
                                response = call.execute();
                                String retName = saveImage(response, SendType.TEMP, TEMP_DIR);
                                if (StringUtils.isNotBlank(retName)) {
                                    msg.image(retName);
                                }
                            }
                            msg.text("\n");
                        }
                        StringBuilder equipInfo2 = new StringBuilder("");
                        els = Xsoup.select(doc, "//*[@class=\"pverecommend2\"]//tr[2]/td[1]").getElements();
                        els.forEach(element -> {
                            equipInfo2.append(element.text().replace("\n", ""));
                        });
                        msg.text(equipInfo2.toString());
                    } else {
                        msg.text("查询不到[" + shipGirlName + "]的推荐配装呢");
                    }
                } else {
                    msg.text("查询不到[" + shipGirlName + "]的推荐配装呢");
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
}
