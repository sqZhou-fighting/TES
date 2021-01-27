package top.jach.tes.app.jhkt.zhoushiqi;

import top.jach.tes.app.dev.DevApp;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhoushiqi
 * @date: 2020/12/9
 * @description: 就是各种测试的文件
 */
public class MyTest extends DevApp {
    public static void main(String[] args) {
        Map<String, String> testMap = new HashMap<>();
        String[] shas = {
                "e3569a26e68311d0fba0612c153606b46719e3ca",
                "cda48ff96d922e2c11553baa70dd2c1bd6348c4a",
                "f13efaa7f8de7c090ae2293b08035afdaf05795e",
                "e9e46cabeceb10f205c384e710169b1cd8062459",
                "c5e1b15b0356d1471ddd72c55bc6a680f8da7288",
                "9f6553381cc882e873163682251f97c6afec4966",
                "36078a3dd00a5a751d18c731014cd5374e840707",
                "c2514c019230d9f88f9b1d5fb6a65cc51ac45648",
                "913001b62bf6e37946394c8dfa7d210bf10a59c7",
                "aabf01a76b7aabd7f73cb8cf2252fe1cccaa0f43",
                "6ca3749dd40bcc5896814f44a5d6dc2f6db97f19"};

        for (String str : shas){
            testMap.put(str, str.substring(0,5));
        }

        System.out.println(testMap.get("aabf01a76b7aabd7f73cb8cf2252fe1cccaa0f43"));
        System.out.println(testMap.get("aabf01a76b7aabd7f73cb8cf2252fe1cccaa0f44"));
    }
}
