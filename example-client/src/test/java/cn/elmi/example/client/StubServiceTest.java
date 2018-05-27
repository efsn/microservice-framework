package cn.elmi.example.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@SpringBootTest
public class StubServiceTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private BlockingStubService service;

    /**
     * 根据票号查询
     */
    @Test
    public void test_1() {
        String message = "hello";
        service.say(message);
    }

}
