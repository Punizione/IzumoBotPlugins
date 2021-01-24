package com.delitto.izumo.mirai.plugins.impl.arknights;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.delitto.izumo.mirai.plugins.BaseCommandPlugin;
import com.delitto.izumo.mirai.utils.Constants;
import com.delitto.izumo.mirai.utils.FileUtil;
import com.delitto.izumo.mirai.utils.ImageProcessUtil;
import com.delitto.izumo.mirai.utils.SendType;
import lombok.extern.log4j.Log4j2;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotBase;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.delitto.izumo.mirai.utils.Constants.COMMAND_ERROR;


@Log4j2
public class GachaPlugin implements BaseCommandPlugin {
    @Override
    public Msg execute(String[] commands, Bot bot) {
        Msg msg = Msg.builder();
        String pathRoot = FileUtil.getPathRoot(Constants.IMAGE_DIR, Constants.ARKNIGHTS_DIR);
        String tempPathRoot = FileUtil.getPathRoot(Constants.IMAGE_DIR, Constants.TEMP_DIR);
        try {
            List<String> operators = new ArrayList<String>();
            for (int i=0;i<10;i++) {
                int random = new Random().nextInt(10000);
                JSONArray levelArray = Constants.arknightsGacha.getJSONArray("3");
                if(random < 200) {
                    levelArray = Constants.arknightsGacha.getJSONArray("6");
                    if(random < 100) {
                        levelArray = Constants.arknightsGacha.getJSONArray("speical-6");
                    }
                } else if(random <1000) {
                    levelArray = Constants.arknightsGacha.getJSONArray("5");
                    if(random < 600) {
                        levelArray = Constants.arknightsGacha.getJSONArray("speical-5");
                    }
                } else if(random <6000) {
                    levelArray = Constants.arknightsGacha.getJSONArray("4");
                    if(random < 3500) {
                        levelArray = Constants.arknightsGacha.getJSONArray("speical-4");
                    }
                }

                JSONObject operator = levelArray.getJSONObject(new Random().nextInt(levelArray.size()));
                operators.add(pathRoot + operator.getString("No") + "_1.png");
            }
            String base = pathRoot + Constants.ARKNGHTS_GACHA_DEFAULT_ENDPOINT;
            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + ".png";
            String gachaResult = ImageProcessUtil.arknightsGacha(base, operators, tempPathRoot + fileName);
            if(StringUtils.isNotBlank(gachaResult)) {
                String sendGacha = FileUtil.copyFile2Mirai(tempPathRoot, fileName, SendType.TEMP);
                if(StringUtils.isNotBlank(sendGacha)) {
                    msg.text("本次十连寻访结果:");
                    msg.image(sendGacha);
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
        return true;
    }
}
