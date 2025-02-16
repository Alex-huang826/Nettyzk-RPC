//服务节点监听:监听 Zookeeper 中服务节点的变化（新增、移除、更新）。
//动态更新客户端的服务节点列表。负载均衡:按权重分配服务节点（高权重节点被多次添加和连接）。
//动态连接池管理:清理旧的服务连接。根据最新服务列表重新建立 Netty 长连接，并将连接添加到连接池。
package com.huang.client.core;

import com.huang.client.zk.ZookeeperFactory;
import io.netty.channel.ChannelFuture;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;

import java.util.HashSet;
import java.util.List;

public class ServerWatcher implements CuratorWatcher {//实现了 CuratorWatcher 接口，专用于监听 Zookeeper 节点事件。
    @Override
    public void process(WatchedEvent event) throws Exception {//触发条件: 当 Zookeeper 监控的节点发生变化（如子节点新增、删除或数据更新）时调用。
        CuratorFramework client = ZookeeperFactory.create();//使用 ZookeeperFactory 创建 Zookeeper 客户端 client，用于与 Zookeeper 通信。
        String path = event.getPath();
        client.getChildren().usingWatcher(this).forPath(path);//为 path 的子节点重新注册当前监听器（this），确保对后续事件的监听不会中断（Zookeeper 的监听是一次性的）。
        List<String> serverPaths = client.getChildren().forPath(path);//获取路径 path 下的所有子节点列表 serverPaths。
        ChannelManager.realServerPath.clear();//清空 ChannelManager.realServerPath，以便更新为最新的服务节点信息。
        for(String serverPath : serverPaths) {
            String[] str = serverPath.split("#");
            int weight = Integer.valueOf(str[2]);
            if(weight>0){
                for(int w=0;w<=weight;w++){
                    ChannelManager.realServerPath.add(str[0] + "#" + str[1]);
                }
            }
            ChannelManager.realServerPath.add(str[0] + "#" + str[1]);
        }
        ChannelManager.clear();
        for(String realServer :ChannelManager.realServerPath){
            String[] str = realServer.split("#");
            try {
                int weight = Integer.valueOf(str[2]);
                if(weight>0){
                    for(int w=0;w<=weight;w++){
                        ChannelFuture channelFuture = TcpClient.b.connect(str[0],Integer.valueOf(str[1]));
                        ChannelManager.add(channelFuture);
                    }
                }

            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
