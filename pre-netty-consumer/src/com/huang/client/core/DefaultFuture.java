package com.huang.client.core;

import com.huang.client.param.ClientRequest;
import com.huang.client.param.Response;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//DefaultFuture 是一个异步响应管理类，用于处理客户端请求和服务端响应的对应关系。
//它的主要功能是：
//在客户端发送请求时，创建一个 DefaultFuture 实例，并将其与请求的唯一 ID 关联。
//在服务端返回响应时，通过 ID 找到对应的 DefaultFuture 实例，将响应注入其中，并唤醒等待的线程。
//支持主线程等待响应结果的机制，避免忙等待。
public class DefaultFuture {
    public final static ConcurrentHashMap<Long, DefaultFuture>allDefaultFuture=new ConcurrentHashMap<Long, DefaultFuture>();
    //全局存储 DefaultFuture 实例的映射表，用于通过请求 ID 查找对应的 DefaultFuture 实例。
    //使用 ConcurrentHashMap 确保线程安全，支持多线程并发操作。
    final Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    //提供线程间的同步机制，用于阻塞和唤醒等待响应的线程。
    //lock 是显式锁（ReentrantLock），提供比 synchronized 更细粒度的控制。
    //condition 是条件变量，允许线程在等待某一条件满足时挂起，并在条件满足后被唤醒。
    private Response response;
    //存储服务端返回的响应对象。
    //当响应到达时，通过 setResponse() 方法设置该字段。

    private long timeout=2*60*1000l;
    private long startTime = System.currentTimeMillis();

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getStartTime() {
        return startTime;
    }

    public DefaultFuture(ClientRequest request) {
        allDefaultFuture.put(request.getId(),this);
    }
    //创建一个 DefaultFuture 实例，并将其与请求的 ID 关联存储在全局映射表 allDefaultFuture 中。
    //这样可以通过请求的 id 找到对应的 DefaultFuture。
    //主线程获取数据，首先要等待结果
    public Response get(){
        lock.lock();//加锁：确保线程安全，防止多个线程同时访问 response。
        try{
            while (!done()){
                condition.await();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return this.response;
    }
    public Response get(long time){//主线程调用此方法以阻塞等待服务端响应。
        lock.lock();//加锁：确保线程安全，防止多个线程同时访问 response。
        try{
            while (!done()){
                condition.await(time, TimeUnit.SECONDS);//如果响应尚未到达（done() 返回 false），线程会挂起等待。
                if ((System.currentTimeMillis()-startTime)>time){
                    System.out.println("请求超时");
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return this.response;
    }
    public static void receive(Response response){//将服务端返回的 response 注入到对应的 DefaultFuture 实例中，并唤醒等待线程。
        DefaultFuture df = allDefaultFuture.get(response.getId());
        //从 allDefaultFuture 中获取与 response.getId() 对应的 DefaultFuture。
        if(df != null){
            Lock lock = df.lock;//加锁：确保线程安全。
            lock.lock();
            try {
                df.setResponse(response);//设置响应：调用 setResponse() 将 response 存储到 DefaultFuture。
                df.condition.signal();//唤醒线程：调用 condition.signal() 唤醒在 get() 方法中等待的线程。
                allDefaultFuture.remove(df);//移除映射：响应处理完成后，从 allDefaultFuture 中移除该 DefaultFuture。
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }
    }
    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
    private boolean done(){//检查响应是否已经到达。如果 response 不为空，则表示响应已经到达。
        if(this.response!=null){
            return true;
        }
        return false;
    }
    //此线程的作用是定期检查 allDefaultFuture 中的 DefaultFuture 对象是否超时，并对超时的请求执行处理逻辑。
    static class FutureThread extends Thread{
        @Override
        public void run() {
            Set<Long>ids = allDefaultFuture.keySet();//从全局变量 allDefaultFuture 中获取所有键（请求 ID）。
            for (Long id : ids){
                DefaultFuture df = allDefaultFuture.get(id);//根据当前的 id 从 allDefaultFuture 中获取对应的 DefaultFuture 对象。
                if(df == null){
                    allDefaultFuture.remove(df);
                }else {
                    //如果链路超时
                    if(df.getTimeout()<(System.currentTimeMillis()-df.getStartTime())){
                        Response resp = new Response();//创建一个新的 Response 对象，用于表示超时的响应结果。
                        resp.setId(id);
                        resp.setCode("3333333");
                        resp.setMsg("链路请求超时");
                        receive(resp);//调用一个方法 receive，将超时的响应结果处理或分发。
                    }
                }
            }
        }
        static {
            FutureThread futureThread = new FutureThread();//创建并启动 FutureThread 守护线程。
            futureThread.setDaemon(true);//将线程设置为守护线程（Daemon Thread）。
            futureThread.start();
        }
    }
}
