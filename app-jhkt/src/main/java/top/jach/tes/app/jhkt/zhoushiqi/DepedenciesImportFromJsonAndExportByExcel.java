package top.jach.tes.app.jhkt.zhoushiqi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import top.jach.tes.plugin.tes.code.dependency.CSPair;
import top.jach.tes.plugin.tes.code.dependency.Direction;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhoushiqi
 * @date: 2020/12/21
 * @description: 从json中读出7个版本的依赖数据，然后以Excel形式导出
 */
public class DepedenciesImportFromJsonAndExportByExcel {
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
            JSONArray jsonArray = JSONObject.parseArray(((Map.Entry) map).getValue().toString());
            for (Object o : jsonArray) {
                JSONObject cs = (JSONObject) o;
                Direction req = null;
                Direction rsp = null;
                JSONObject reqJson = cs.getJSONObject("req");
                if (reqJson != null) {
                    req = new Direction();
                    req.setSrc_cs(reqJson.getString("src_cs"));
                    req.setDest_cs(reqJson.getString("dest_cs"));
                    req.setTopic(reqJson.getString("topic"));
                    req.setOneof(reqJson.getString("oneof"));
                    req.setVersion(reqJson.getString("version"));
                }
                JSONObject rspJson = cs.getJSONObject("rsp");
                if (rspJson != null) {
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
            allDependencies.put(((Map.Entry) map).getKey().toString(), dependencies);
        }

        HSSFWorkbook wb = new HSSFWorkbook();

        for (Map.Entry<String, List<CSPair>> entry : allDependencies.entrySet()) {
            HSSFSheet sheet = wb.createSheet(entry.getKey().substring(entry.getKey().length() - 6, entry.getKey().length()));

            HSSFRow head = sheet.createRow(0);
            head.createCell(0).setCellValue("src_cs");
            head.createCell(1).setCellValue("dest_cs");
            head.createCell(2).setCellValue("topic");
            head.createCell(3).setCellValue("oneof");

            head.createCell(4).setCellValue("src_cs");
            head.createCell(5).setCellValue("dest_cs");
            head.createCell(6).setCellValue("topic");
            head.createCell(7).setCellValue("oneof");

            int k = 1;
            for (CSPair csPair : entry.getValue()) {
                HSSFRow row = sheet.createRow(k);
                Direction req = csPair.getReq();

                row.createCell(0).setCellValue(req.getSrc_cs());
                row.createCell(1).setCellValue(req.getDest_cs());
                row.createCell(2).setCellValue(req.getTopic());
                row.createCell(3).setCellValue(req.getOneof());

                if (csPair.getRsp() != null) {
                    Direction rsp = csPair.getRsp();
                    row.createCell(4).setCellValue(rsp.getSrc_cs());
                    row.createCell(5).setCellValue(rsp.getDest_cs());
                    row.createCell(6).setCellValue(rsp.getTopic());
                    row.createCell(7).setCellValue(rsp.getOneof());
                }

                k++;
            }
        }

        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream("D:\\NJU\\GP\\Data\\dependencies\\DependenciesByVersions.xls");
            wb.write(fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
