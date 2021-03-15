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

package org.openingo.spring.websocket;

import lombok.extern.slf4j.Slf4j;
import org.openingo.jdkits.sys.SystemClockKit;
import org.openingo.jdkits.validate.ValidateKit;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * WebsocketServerEndpoint
 *
 * @author Qicz
 */
@Slf4j
@ServerEndpoint("/instant-ws/{connId}")
public class WebsocketServerEndpoint {

    private static final Long EXPIRE_TIME = 24 * 60 * 60 * 1000L;

    Integer connId;
    Session session;
    private long connTime;
    private long lastActiveTime;

    private void fixLastActiveTime() {
        this.lastActiveTime = SystemClockKit.now();
    }

    @OnOpen
    private void onOpen(Session session, @PathParam("connId") Integer dataId) {
        this.connId = dataId;
        this.session = session;
        this.connTime = SystemClockKit.now();
        this.lastActiveTime = SystemClockKit.now();
        session.setMaxIdleTimeout(EXPIRE_TIME);
        InstantManager.manager.addConnection(dataId, this);
    }

    @OnMessage
    private void onMessage(String message, Session session) {
        this.fixLastActiveTime();
    }

    @OnError
    private void onError(Session session, Throwable e) {
        log.error("WebsocketServerEndpoint has error, connId = {}, leisure time {} ms.", this.connId, SystemClockKit.now() - this.lastActiveTime);
        log.error("", e);
    }

    @OnClose
    private void onClose() throws IOException {
        log.info("WebsocketServerEndpoint onClose, connId = {}, live time {} ms.", this.connId, SystemClockKit.now() - this.connTime);
        if (ValidateKit.isNotNull(this.connId)) {
            InstantManager.manager.removeConnection(this.connId, this);
        }
    }

    RemoteEndpoint.Basic getBasicRemote() {
        this.fixLastActiveTime();
        return this.session.getBasicRemote();
    }

    RemoteEndpoint.Async getAsyncRemote() {
        this.fixLastActiveTime();
        return session.getAsyncRemote();
    }
}
