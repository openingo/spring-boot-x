/*
 * MIT License
 *
 * Copyright (c) 2020 OpeningO Co.,Ltd.
 *
 *    https://openingo.org
 *    contactus(at)openingo.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package orgg.openingo.x.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openingo.jdkits.http.RespData;
import org.openingo.jdkits.sys.SystemClockKit;
import org.openingo.spring.exception.ServiceException;
import org.openingo.spring.extension.data.redis.StringKeyRedisTemplateX;
import org.openingo.spring.extension.data.redis.naming.IKeyNamingPolicy;
import org.openingo.spring.extension.data.redis.naming.KeyNamingKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.SortParameters;
import org.springframework.data.redis.core.query.DefaultStringSortQuery;
import org.springframework.data.redis.core.query.SortQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import orgg.openingo.x.entity.User;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UserController
 *
 * @author Qicz
 */
@RestController
@SuppressWarnings("all")
public class UserController {

    @Autowired
    StringKeyRedisTemplateX stringKeyRedisTemplateX;

    @Autowired
    IKeyNamingPolicy keyNamingPolicy;

    @GetMapping("/user")
    public String user(@RequestBody User user) {
        return "ok";
    }

    @GetMapping("/userex")
    public String userex(@RequestBody User user) {
        throw new ServiceException("123111zczc", "user ex exception");
    }

    @GetMapping("/user1/{name}")
    public String user1(@PathVariable("name") String name) {
        return "ok";
    }

    @GetMapping("/save")
    public String save() {
        try {
            KeyNamingKit.set("openingo");
            stringKeyRedisTemplateX.set("name", "Qicz");
            return "ok";
        } finally {
            KeyNamingKit.remove();
        }
    }

    @GetMapping("/saveex")
    public String saveex() {
        try {
            KeyNamingKit.set("openingo");
            stringKeyRedisTemplateX.setEnableTransactionSupport(true);
            stringKeyRedisTemplateX.multi();
            stringKeyRedisTemplateX.set("name", "Qicz");
            stringKeyRedisTemplateX.expire("name", 48*3600);
            List exec = stringKeyRedisTemplateX.exec();
            System.out.println("exec=========="+exec);
            return "ok";
        } finally {
            KeyNamingKit.remove();
        }
    }
    @GetMapping("/saveexe")
    public String saveexe() {
        try {
            KeyNamingKit.set("openingo");
//            Object exec = stringKeyRedisTemplateX.execute(new SessionCallbackX() {
//                @Override
//                public void execute() {
//                    stringKeyRedisTemplateX.set("name", "Qicz" + SystemClockKit.now());
//                    stringKeyRedisTemplateX.expire("name", 48 * 3600);
//                }
//            });

            stringKeyRedisTemplateX.set("name", "Qicz" + SystemClockKit.now());
            stringKeyRedisTemplateX.expire("name", 48 * 3600);
            return "ok" + stringKeyRedisTemplateX.get("name");
        } finally {
            KeyNamingKit.remove();
        }
    }

    @GetMapping("/json")
    public RespData json(){
        return RespData.success(new HashMap<String, Object>(){{
            put("name","qicz");
            put("age", 18);
        }});
    }

    @GetMapping("/ex")
    public Map ex() {
        //Map map = null;//new HashMap();
        //map.put("a", "A");
        throw new ServiceException("123111zczc", "testing exception");
        //return map;
    }

    @GetMapping("/file")
    public String file(MultipartFile file) {
        return "ok";
    }

    @GetMapping("/non")
    public String non() {
        new NonController().ok();
        return "ok";
    }

    @GetMapping("/ex1")
    public RespData ex1(){
        throw new IndexOutOfBoundsException("IndexOutOfBoundsException message");
    }

    @GetMapping("/ex2")
    public RespData ex2() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.readValue("[{asd}]", Map.class);
        return RespData.success();
    }

    @GetMapping("/sort")
    public void sort() {
        DefaultStringSortQuery defaultStringSortQuery = new DefaultStringSortQuery(this.keyNamingPolicy, new SortQuery<String>() {
            @Override
            public String getKey() {
                return "qiczkey";
            }

            @Override
            public SortParameters.Order getOrder() {
                return SortParameters.Order.ASC;
            }

            @Override
            public Boolean isAlphabetic() {
                return true;
            }

            @Override
            public SortParameters.Range getLimit() {
                return new SortParameters.Range(0, 1);
            }

            @Override
            public String getBy() {
                return "qiczkey";
            }

            @Override
            public List<String> getGetPattern() {
                return Arrays.asList("qiczkey", "qiccc");
            }
        });
        System.out.println(defaultStringSortQuery);
    }

    @GetMapping("/resp")
    public RespData resp() {
        return RespData.success(Arrays.asList("d1","d2","d3"));
    }
}
