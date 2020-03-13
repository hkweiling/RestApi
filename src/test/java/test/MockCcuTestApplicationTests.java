package test;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MockCcuTestApplicationTests {
    @Value("$mock.appid")
    private String mockAppId;
    @Value("$mock.appkey")
    private String mockAppKey;

    @Test
    void contextLoads() {
       System.out.println(mockAppId+","+mockAppKey);
    }

}
