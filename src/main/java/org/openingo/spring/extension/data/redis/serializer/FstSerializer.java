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

package org.openingo.spring.extension.data.redis.serializer;

import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import org.openingo.jdkits.validate.ValidateKit;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * FstSerializer
 *
 * @author Qicz
 */
public class FstSerializer<T> implements ISerializer<T> {

    @Override
    public byte[] serialize(T t) throws SerializationException {
        try {
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            FSTObjectOutput fstOut = new FSTObjectOutput(bytesOut);
            fstOut.writeObject(t);
            fstOut.flush();
            return bytesOut.toByteArray();
        } catch (Exception e) {
            throw new SerializationException(e.toString(), e);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if(ValidateKit.isNull(bytes)) {
            return null;
        }

        try {
            FSTObjectInput fstInput = new FSTObjectInput(new ByteArrayInputStream(bytes));
            return (T)fstInput.readObject();
        } catch (Exception e) {
            throw new SerializationException(e.toString(), e);
        }
    }
}
