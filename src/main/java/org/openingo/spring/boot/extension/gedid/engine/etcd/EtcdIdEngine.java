/*
 * MIT License
 *
 * Copyright (c) 2021 OpeningO Co.,Ltd.
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

package org.openingo.spring.boot.extension.gedid.engine.etcd;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.options.PutOption;
import lombok.extern.slf4j.Slf4j;
import org.openingo.spring.boot.exception.DidException;
import org.openingo.spring.boot.extension.gedid.engine.IDidEngine;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * EtcdIdEngine
 *
 * @author Qicz
 * @since 2021/7/2 23:45
 */
@Slf4j
public class EtcdIdEngine implements IDidEngine<Long> {

    private final Client etcdClient;
    private final KV kvClient;
    private Long startId;

    public EtcdIdEngine(Client etcdClient) {
        this.etcdClient = etcdClient;
        this.kvClient = etcdClient.getKVClient();
    }

    @Override
    public void follow(String businessName, Long startId) {
        this.startId = startId + 1L;
    }

    @Override
    public String getEmbellishedName(String businessName) {
        return String.format("%s-%s", GEDID, businessName);
    }

    @Override
    public Long getFixedStartId(Long startId) {
        if (Objects.isNull(startId)) {
            return 1L;
        }
        if (startId < 0) {
            startId = 1L;
        }
        return startId;
    }

    @Override
    public Long next(String businessName) {
        long id = -1L;
        try {
            PutOption putOption = PutOption.newBuilder().withPrevKV().build();
            PutResponse putResponse = kvClient.put(
                    this.toByteSequence(this.getEmbellishedName(businessName)),
                    this.toByteSequence(""),
                    putOption).get();
            id = putResponse.getPrevKv().getVersion() + this.startId;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
            throw new DidException(e.getLocalizedMessage());
        }
        return id;
    }

    @Override
    public String engineName() {
        return "etcd";
    }

    private ByteSequence toByteSequence(String data) {
        return ByteSequence.from(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * will auto call when destroy
     */
    private void close() {
        String name = this.engineName();
        log.info("the `{}` engine is closing...", name);
        this.etcdClient.close();
        log.info("the `{}` engine is closed", name);
    }
}
