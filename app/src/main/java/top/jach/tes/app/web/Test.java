package top.jach.tes.app.web;

import top.jach.tes.app.web.dto.Microservice;
import top.jach.tes.app.web.service.impl.MaintainServiceImpl;

/**
 * @Author: zhoushiqi
 * @date: 2021/3/29
 * @description:
 */
public class Test {
    public static void main(String[] args) {
        MaintainServiceImpl mm = new MaintainServiceImpl();
        Microservice microservice = mm.findMSAllMetricsRes("x_25");
        System.out.println("bp");
    }
}
