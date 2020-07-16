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

package org.openingo.data.redis.serializer.demo;

import org.junit.Test;
import org.openingo.data.redis.serializer.demo.entity.User;
import org.openingo.spring.extension.data.redis.serializer.FstRedisSerializer;

/**
 * FSTSerializerDemo
 *
 * @author Qicz
 */
public class FSTSerializerDemo {

    @Test
    public void test() {
        User user = new User();
        user.setAddr("addr");
        user.setName("name");
        user.setAge(12);

        FstRedisSerializer objectFstRedisSerializer = new FstRedisSerializer();
        byte[] serialize = objectFstRedisSerializer.serialize(user);

        User userCp = (User) objectFstRedisSerializer.deserialize(serialize);

        System.out.println(userCp);
    }
}
