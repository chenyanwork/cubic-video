package com.cubic.viedo.store;

import com.cubic.viedo.webscoket.DefaultWebConnection;
import com.cubic.viedo.webscoket.WebConnection;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.MoreExecutors;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName DefaultWebConnectionStore
 * @Author QIANGLU
 * @Date 2020/4/6 11:13 上午
 * @Version 1.0
 */
@Slf4j
@Service
public class DefaultWebConnectionStore implements WebConnectionStore {

    private final ConcurrentHashMap<Channel, WebConnection> CONNECTION = new ConcurrentHashMap<>();

    @Override
    public WebConnection register(String uid, Channel channel) {
        DefaultWebConnection connection = new DefaultWebConnection(uid, channel);
        WebConnection oldConnection = CONNECTION.putIfAbsent(channel, connection);
        if (oldConnection != null) {
            return oldConnection;
        }
        connection.init();
        connection.closeFuture().addListener(() -> CONNECTION.remove(channel, connection), MoreExecutors.directExecutor());
        return connection;
    }

    @Override
    public Optional<WebConnection> getConnection(Channel channel) {
        WebConnection connection = CONNECTION.get(channel);
        if (connection != null) {
            return Optional.of(connection);
        }
        return Optional.empty();
    }

    @Override
    public Map<Channel, WebConnection> getAllConnection() {
        return ImmutableMap.copyOf(CONNECTION);
    }
}
