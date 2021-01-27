package top.jach.tes.app.jhkt.zhoushiqi;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import top.jach.tes.app.dev.DevApp;
import top.jach.tes.app.mock.Environment;
import top.jach.tes.core.api.domain.action.DefaultInputInfos;
import top.jach.tes.core.api.domain.action.InputInfos;
import top.jach.tes.core.api.domain.context.Context;
import top.jach.tes.core.api.domain.info.Info;
import top.jach.tes.core.api.domain.info.InfoProfile;
import top.jach.tes.core.api.exception.ActionExecuteFailedException;
import top.jach.tes.core.impl.domain.action.SaveInfoAction;
import top.jach.tes.plugin.tes.data.DataDir;

import java.io.File;
import java.io.IOException;

/**
 * @Author: zhoushiqi
 * @date: 2021/1/20
 * @description: 将bug数据导入数据库
 */
public class ImportBugDts extends DevApp {
    public static void main(String[] args) throws ActionExecuteFailedException {
        Context context = Environment.contextFactory.createContext(Environment.defaultProject);
        SaveInfoAction saveInfoAction = new SaveInfoAction();
        File dataFile = new File("D:\\NJU\\data\\data_v20201210001\\BugDts_468006014954817536.json");

        try {
            JSONObject data = JSONObject.parseObject(FileUtils.readFileToString(dataFile, "utf8"));
            Class infoClass = Class.forName(data.getString("infoClass"));
            Info info = (Info) data.toJavaObject(infoClass);
            context.InfoRepositoryFactory().getRepository(info.getInfoClass()).deleteByInfoId(info.getId());
            InputInfos tmp = new DefaultInputInfos();
            tmp.put(String.valueOf(tmp.size()), info);
            saveInfoAction.execute(tmp, context);
        } catch (IOException e) {
            throw new ActionExecuteFailedException(e);
        } catch (ClassNotFoundException e) {
            throw new ActionExecuteFailedException(e);
        }
    }
}
