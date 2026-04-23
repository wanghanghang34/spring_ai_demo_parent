package com.xuxi.learningspringaiexample.tool;

import com.xuxi.learningcommon.pojo.UserPojo;
import org.springframework.ai.tool.annotation.Tool;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class QueryDataBaseNsp {

    @Tool(description = "获取NSP系统用户数据")
    public List<UserPojo> getNspUserData() {
        return getUserPojo();
    }

    /**
     * 手动构建用户数据 UserPojo
     */
    public List<UserPojo> getUserPojo() {
        List<UserPojo> userList = new ArrayList<>();

        UserPojo user1 = new UserPojo();
        user1.setId(1L);
        user1.setName("张三");
        user1.setAge(18);
        user1.setEmail("zhangsan@example.com");
        user1.setPhone("13800001111");
        user1.setGender("男");
        user1.setBirthday(LocalDate.of(2006, 5, 15));
        user1.setAddress("北京市朝阳区");
        userList.add(user1);

        UserPojo user2 = new UserPojo();
        user2.setId(2L);
        user2.setName("李四");
        user2.setAge(25);
        user2.setEmail("lisi@example.com");
        user2.setPhone("13800002222");
        user2.setGender("女");
        user2.setBirthday(LocalDate.of(1999, 8, 20));
        user2.setAddress("上海市浦东新区");
        userList.add(user2);

        UserPojo user3 = new UserPojo();
        user3.setId(3L);
        user3.setName("王五");
        user3.setAge(30);
        user3.setEmail("wangwu@example.com");
        user3.setPhone("13800003333");
        user3.setGender("男");
        user3.setBirthday(LocalDate.of(1994, 3, 10));
        user3.setAddress("广州市天河区");
        userList.add(user3);

        UserPojo user4 = new UserPojo();
        user4.setId(4L);
        user4.setName("赵六");
        user4.setAge(22);
        user4.setEmail("zhaoliu@example.com");
        user4.setPhone("13800004444");
        user4.setGender("女");
        user4.setBirthday(LocalDate.of(2002, 11, 25));
        user4.setAddress("深圳市南山区");
        userList.add(user4);

        UserPojo user5 = new UserPojo();
        user5.setId(5L);
        user5.setName("孙七");
        user5.setAge(28);
        user5.setEmail("sunqi@example.com");
        user5.setPhone("13800005555");
        user5.setGender("男");
        user5.setBirthday(LocalDate.of(1996, 7, 8));
        user5.setAddress("杭州市西湖区");
        userList.add(user5);

        return userList;
    }
}
