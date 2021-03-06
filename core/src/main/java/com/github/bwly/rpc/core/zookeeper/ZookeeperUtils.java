package com.github.bwly.rpc.core.zookeeper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ZookeeperUtils {
    public static final String ZK_REGISTER_ROOT_PATH = "/my-rpc";
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();

    private static CuratorFramework zkClient;

    private static String host = "127.0.0.1";
    private static int port = 2181;
    private static int baseSleepTime = 1000;
    private static int maxRetries = 3;
    private static String namespace = "simple-rpc";

    static {
        init();
    }

    private static void init() {
        String connectString = host + ":" + port;
        ZookeeperUtils.zkClient = CuratorFrameworkFactory.builder()
                .namespace(namespace)
                .connectString(connectString)
                .retryPolicy(new ExponentialBackoffRetry(baseSleepTime, maxRetries))
                .build();

        ZookeeperUtils.zkClient.start();
        try {
            // wait 30s until connect to the zookeeper
            if (!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("Time out waiting to connect to ZK!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void createPersistentNode(String servicePath) {
        String path = ZK_REGISTER_ROOT_PATH + "/" + servicePath;
        try {
            if (REGISTERED_PATH_SET.contains(path) || zkClient.checkExists().forPath(path) != null) {
                log.info("The node already exists. The node is:[{}]", path);
            } else {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                log.info("The node was created successfully. The node is:[{}]", path);
            }
            REGISTERED_PATH_SET.add(path);
        } catch (Exception e) {
            log.error("create persistent node for path [{}] fail", path);
        }
    }

    public static List<String> getChildrenNodes(String rpcServiceName) {
        if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName)) {
            return SERVICE_ADDRESS_MAP.get(rpcServiceName);
        }
        List<String> result = null;
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        try {
            result = zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, result);
            registerWatcher(rpcServiceName);
        } catch (Exception e) {
            log.error("get children nodes for path [{}] fail", servicePath);
        }
        return result;
    }

    public static void clearRegistry(InetSocketAddress inetSocketAddress) {
        REGISTERED_PATH_SET.stream().parallel().forEach(p -> {
            try {
                if (p.endsWith(inetSocketAddress.toString())) {
                    zkClient.delete().forPath(p);
                }
            } catch (Exception e) {
                log.error("clear registry for path [{}] fail", p);
            }
        });
        log.info("All registered services on the server are cleared:[{}]", REGISTERED_PATH_SET.toString());
    }

    /**
     * ??????zookeeper????????????????????????????????????????????????????????????????????????????????????
     * @param rpcServiceName
     * @throws Exception
     */
    private static void registerWatcher(String rpcServiceName) throws Exception {
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        CuratorCache cache = CuratorCache.builder(zkClient, servicePath).build();

        CuratorCacheListener curatorCacheListener = (CuratorCacheListener.Type type, ChildData oldData, ChildData data) -> {
            List<String> serviceAddresses = null;
            log.debug("type:[{}], oldData:[{}], data:[{}]", type, oldData, data);
            try {
                serviceAddresses = zkClient.getChildren().forPath(servicePath);
            } catch (Exception e) {
                log.error("get children nodes for path [{}] fail", servicePath);
            }
            SERVICE_ADDRESS_MAP.put(rpcServiceName, serviceAddresses);
        };
        cache.listenable().addListener(curatorCacheListener);
        cache.start();
    }
}
