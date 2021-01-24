package com.delitto.izumo.mirai.plugins.impl.image;

import com.delitto.izumo.mirai.bean.PixivIllustDetail;
import com.delitto.izumo.mirai.plugins.BaseCommandPlugin;
import com.delitto.izumo.mirai.utils.Constants;
import com.delitto.izumo.mirai.utils.FileUtil;
import com.delitto.izumo.mirai.utils.ImageProcessUtil;
import com.delitto.izumo.mirai.utils.SendType;
import com.delitto.izumo.mirai.utils.api.PixivApi;
import lombok.extern.log4j.Log4j2;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotBase;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static com.delitto.izumo.mirai.utils.Constants.COMMAND_ERROR;
import static com.delitto.izumo.mirai.utils.FileUtil.getMiraiImagePath;

@Log4j2
public class ShadowTankPlugin implements BaseCommandPlugin {
    @Override
    public Msg execute(String[] commands, Bot bot) {
        Msg msg = Msg.builder();
        try {
            String keyword = "";
            if(commands.length > 1) {
                keyword = commands[1];
            }
            if(StringUtils.isBlank(keyword)) {
                keyword = "アズールレーン";
            }
            List<PixivIllustDetail> pixivIllustDetailList = PixivApi.getSearchR18(keyword, 1);
            if (pixivIllustDetailList != null && pixivIllustDetailList.size() > 0) {
                PixivIllustDetail illustDetail = pixivIllustDetailList.get(0);
                String endPoint = Constants.SHADOW_TANK_DEFAULT_ENDPOINT;
                if(illustDetail.getFilePath()!=null && illustDetail.getFilePath().size() >0) {
                    String imageInTop = illustDetail.getFilePath().get(0);
                    File pathRoot = new File(ResourceUtils.getURL("classpath:").getPath());
                    if (!pathRoot.exists()) {
                        pathRoot = new File("");
                    }
                    String filePath = pathRoot.getAbsolutePath().replace("%20", " ").replace('/', '\\')
                            + "\\"
                            + Constants.STATIC_DIR
                            + File.separator
                            + Constants.IMAGE_DIR + File.separator + Constants.TEMP_DIR + File.separator;
                    String utilsFilePath = pathRoot.getAbsolutePath().replace("%20", " ").replace('/', '\\')
                            + "\\"
                            + Constants.STATIC_DIR
                            + File.separator
                            + Constants.IMAGE_DIR + File.separator + Constants.UTILS_DIR + File.separator;
                    String pixivFilePath = getMiraiImagePath() + File.separator;
                    String shadow = ImageProcessUtil.shadowTank(utilsFilePath + endPoint, imageInTop,  filePath);
                    if(StringUtils.isNotBlank(shadow)) {
                        String sendShadow = FileUtil.copyFile2Mirai(filePath, shadow, SendType.TEMP);
                        if(StringUtils.isNotBlank(sendShadow)) {
                            msg.image(sendShadow);
                            msg.text("\n获取坦克原图请私聊发送:");
                            msg.text("\n~pixiv ");
                            msg.text(illustDetail.getId());
                        }
                    }
                }
            }
        } catch (IndexOutOfBoundsException | FileNotFoundException iobe) {
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
