package com.delitto.izumo.mirai.plugins.impl.twitter;

import com.delitto.izumo.mirai.bean.Twitter;
import com.delitto.izumo.mirai.plugins.BaseCommandPlugin;
import com.delitto.izumo.mirai.utils.api.TwitterApi;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotBase;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.delitto.izumo.mirai.utils.Constants.COMMAND_ERROR;
import static com.delitto.izumo.mirai.utils.api.TwitterApi.concatSide;
import static com.delitto.izumo.mirai.utils.api.TwitterApi.parseTawawa;

public class TawawaTwitterPlugin implements BaseCommandPlugin {
    @Override
    public Msg execute(String[] commands, Bot bot) {
        Msg msg = Msg.builder();
        try {
            List<Twitter> tweets = TwitterApi.getUserTimeLineFilterInRsshub("Strangestone", "月曜日のたわわ");
            for (Twitter tweet: tweets) {
                tweet.parseContent();
                String currentSide = parseTawawa(tweet);
                if(StringUtils.isBlank(currentSide)) {
                    continue;
                }
                ArrayList<String> imageUrls = tweet.getImageUrls();
                List<String> localImages = new ArrayList<>();
                if(imageUrls!=null) {
                    for(String url: imageUrls) {
                        String download = TwitterApi.downloadImage(url, concatSide(currentSide));
                        if(StringUtils.isNotBlank(download)) {
                            localImages.add(download);
                        }
                    }
                }
                String title = tweet.getTitle();
                msg.text(title);
                msg.text("\n");
                for(String img: localImages){
                    msg.image(img);
                }
                msg.text("\n");
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
