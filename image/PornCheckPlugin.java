package com.delitto.izumo.mirai.plugins.impl.image;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.delitto.izumo.mirai.plugins.api.TencentVisionPornApi;
import com.delitto.izumo.mirai.utils.CommandUtil;
import lombok.extern.log4j.Log4j2;

import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import okhttp3.ResponseBody;
import onebot.OnebotBase;
import onebot.OnebotEvent;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.DigestUtils;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.delitto.izumo.mirai.utils.Constants.TOO_SEXY_IMAGE;

@Component
@Log4j2
public class PornCheckPlugin extends BotPlugin {
    @Value("${bot-config.tencent-sign.app-id}")
    private String APP_ID;

    @Value("${bot-config.tencent-sign.app-secert}")
    private String APP_SECRET;

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        int rand = new Random().nextInt(100);
        if(rand < 50) {

            Msg retMsg = Msg.builder();
            String msg = event.getRawMessage();
            List<OnebotBase.Message> sMsg = event.getMessageList();
            long groupId = event.getGroupId();
            long userId = event.getUserId();
            if(CommandUtil.isPureImage(sMsg)) {
                try {
                    String imageUrl = CommandUtil.getImageUrl(sMsg);
                    if(StringUtils.isBlank(imageUrl)) {
                        return MESSAGE_IGNORE;
                    }
                    Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.ai.qq.com/").build();
                    TencentVisionPornApi api = retrofit.create(TencentVisionPornApi.class);
                    Call<ResponseBody> call = api.getLink(imageUrl);
                    Response<ResponseBody> response = call.execute();
                    byte[] byteArray = response.body().bytes();
                    if(byteArray.length > 1024*1024) {
                        return MESSAGE_BLOCK;
                    }
                    String base64 = Base64Utils.encode(byteArray).toString().replaceAll("\r|\n", "");;
                    Map<String, String> paramMap = new HashMap<>();
                    paramMap.put("app_id", APP_ID);
                    paramMap.put("time_stamp", "" + (System.currentTimeMillis()/1000));
                    paramMap.put("nonce_str", RandomStringUtils.randomAlphanumeric(16));
                    paramMap.put("image", base64);
                    paramMap.put("sign", getTencentSign(paramMap, APP_SECRET));

                    call = api.check(paramMap);
                    response = call.execute();
                    if (response.isSuccessful()) {
                        JSONObject obj = JSONObject.parseObject(response.body().string());
                        if(obj.getInteger("ret") == 0) {
                            JSONArray array = obj.getJSONObject("data").getJSONArray("tag_list");
                            float hot = 0f;
                            float normal = 0f;
                            int porn = 0;
                            for(int i=0 ; i<array.size() ; i++) {
                                JSONObject element = array.getJSONObject(i);
                                if(element.getString("tag_name").equals("hot") ) {
                                    hot = element.getFloat("tag_confidence_f");
                                } else if(element.getString("tag_name").equals("normal") ) {
                                    normal = element.getFloat("tag_confidence_f");
                                } else if(element.getString("tag_name").equals("porn") ) {
                                    porn = element.getInteger("tag_confidence_f");
                                }
                            }
                            if(hot > normal || porn > 83) {
                                retMsg.text(TOO_SEXY_IMAGE.get(new Random().nextInt(TOO_SEXY_IMAGE.size())));
                                bot.sendGroupMsg(groupId, retMsg, false);
                            }
                        }
                    }
                    return MESSAGE_BLOCK;
                } catch (IndexOutOfBoundsException iobe) {
                    return MESSAGE_IGNORE;
                } catch (IOException ioe) {
                    log.error("请求异常", ioe);
                    return MESSAGE_BLOCK;
                }

            }
        }

        return MESSAGE_IGNORE;
    }

    /**
     * 获取加密sign签名
     * @param paramMap
     * @param secret
     * @return
     */
    public static String getTencentSign(Map<String, String> paramMap, String secret) {
        ArrayList<String> keyArray = new ArrayList<>(paramMap.keySet());
        Collections.sort(keyArray, new Comparator<String>(){
            @Override
            public int compare(String o1, String o2) {
                try {
                    String str1 = new String(o1.toString().getBytes("GB2312"), StandardCharsets.ISO_8859_1);
                    String str2 = new String(o2.toString().getBytes("GB2312"), StandardCharsets.ISO_8859_1);
                    return str1.compareTo(str2);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        StringBuilder sb = new StringBuilder();
        try {
            for (String key : keyArray) {
                sb.append(key).append("=").append(URLEncoder.encode(paramMap.get(key), "utf-8")).append("&");
            }
            sb.append("app_key=").append(secret);
            return DigestUtils.md5DigestAsHex(sb.toString().getBytes()).toUpperCase();
        } catch (UnsupportedEncodingException e ) {
            log.error("sign签名加密失败", e);
        }
        return null;
    }

}
