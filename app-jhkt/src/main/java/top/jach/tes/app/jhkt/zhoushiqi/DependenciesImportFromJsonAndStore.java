package top.jach.tes.app.jhkt.zhoushiqi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import top.jach.tes.app.dev.DevApp;
import top.jach.tes.app.mock.InfoTool;
import top.jach.tes.plugin.jhkt.InfoNameConstant;
import top.jach.tes.plugin.tes.code.dependency.CSPair;
import top.jach.tes.plugin.tes.code.dependency.DependenciesInfo;
import top.jach.tes.plugin.tes.code.dependency.Direction;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhoushiqi
 * @date: 2020/11/26
 * @description: 将脱敏的依赖数据存入数据库
 */
public class DependenciesImportFromJsonAndStore extends DevApp {
    public static void main(String[] args) {
        String json = "";
        InputStream inputStream;
        try {
            inputStream = new FileInputStream("D:\\NJU\\GP\\Data\\dependencies\\DesensitizedAutoMatchedDependenciesByVersionsFinal.json");
            json = IOUtils.toString(inputStream, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, List<CSPair>> allDependencies = new HashMap<>();
        Map maps = (Map) JSON.parse(json);
        for (Object map : maps.entrySet()) {
            List<CSPair> dependencies = new ArrayList<>();
            String tmp = ((Map.Entry)map).getValue().toString();
            JSONArray jsonArray = JSONObject.parseArray(((Map.Entry)map).getValue().toString());
            for (Object o : jsonArray){
                JSONObject cs = (JSONObject) o;
                Direction req = null;
                Direction rsp = null;
                JSONObject reqJson = cs.getJSONObject("req");
                if (reqJson!=null){
                    req = new Direction();
                    req.setSrc_cs(reqJson.getString("src_cs"));
                    req.setDest_cs(reqJson.getString("dest_cs"));
                    req.setTopic(reqJson.getString("topic"));
                    req.setOneof(reqJson.getString("oneof"));
                    req.setVersion(reqJson.getString("version"));
                }
                JSONObject rspJson = cs.getJSONObject("rsp");
                if (rspJson!=null){
                    rsp = new Direction();
                    rsp.setSrc_cs(rspJson.getString("src_cs"));
                    rsp.setDest_cs(rspJson.getString("dest_cs"));
                    rsp.setTopic(rspJson.getString("topic"));
                    rsp.setOneof(rspJson.getString("oneof"));
                    rsp.setVersion(rspJson.getString("version"));
                }
                CSPair csPair = new CSPair(req, rsp);
                dependencies.add(csPair);
            }
            allDependencies.put(((Map.Entry)map).getKey().toString(), dependencies);
        }

        System.out.println("用来打断点的");
        // 存入数据库
        for (Map.Entry<String, List<CSPair>> entry : allDependencies.entrySet()){
            DependenciesInfo dependenciesInfo = DependenciesInfo.createInfoByDependencies(entry.getKey(),
                    entry.getValue());
            dependenciesInfo.setName(InfoNameConstant.DependenciesForVersion);
            InfoTool.saveInfos(dependenciesInfo);
        }
    }
}
