package com.impler.less.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 通用线程池
 * 
 * @author Invalid
 * @date 2011-2-13 上午10:20:43
 * 
 */
public class CommonThreadPoolExecutor implements Executor {

    static Logger log = LoggerFactory.getLogger(CommonThreadPoolExecutor.class);

    protected int threadPriority = 5;

    protected boolean daemon = true;

    protected String namePrefix = "CommonThread-";

    protected int maxThreads = 20;

    protected int minSpareThreads = 4;

    protected int maxIdleTime = 60000;

    protected ThreadPoolExecutor executor = null;

    protected String name = "DefaultCommonThreadPool";

    public void start() {
        TaskQueue taskqueue = new TaskQueue();
        TaskThreadFactory tf = new TaskThreadFactory(this.namePrefix);
        this.executor = new ThreadPoolExecutor(getMinSpareThreads(), getMaxThreads(), this.maxIdleTime,
                TimeUnit.MILLISECONDS, taskqueue, tf);
        taskqueue.setParent(this.executor);
        log.info("CommonThreadPoolExecutor Start:" + name);
    }

    public void stop() {
        if (this.executor != null)
            this.executor.shutdown();
        this.executor = null;
        log.info("CommonThreadPoolExecutor Stop:" + name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
     * 
     * @author Invalid
     * 
     * @date 2011-2-13 上午10:21:54
     */
    public void execute(Runnable command) {
        if (this.executor != null)
            try {
                this.executor.execute(command);
            } catch (RejectedExecutionException rx) {
                if (!((TaskQueue) this.executor.getQueue()).force(command))
                    throw new RejectedExecutionException();
            }
        else
            throw new IllegalStateException("CommonThreadPoolExecutor not started.");
    }

    public int getThreadPriority() {
        return this.threadPriority;
    }

    public boolean isDaemon() {
        return this.daemon;
    }

    public String getNamePrefix() {
        return this.namePrefix;
    }

    public int getMaxIdleTime() {
        return this.maxIdleTime;
    }

    public int getMaxThreads() {
        return this.maxThreads;
    }

    public int getMinSpareThreads() {
        return this.minSpareThreads;
    }

    public String getName() {
        return this.name;
    }

    public void setThreadPriority(int threadPriority) {
        this.threadPriority = threadPriority;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    public void setMaxIdleTime(int maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public void setMinSpareThreads(int minSpareThreads) {
        this.minSpareThreads = minSpareThreads;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getActiveCount() {
        return this.executor != null ? this.executor.getActiveCount() : 0;
    }

    public long getCompletedTaskCount() {
        return this.executor != null ? this.executor.getCompletedTaskCount() : 0L;
    }

    public int getCorePoolSize() {
        return this.executor != null ? this.executor.getCorePoolSize() : 0;
    }

    public int getLargestPoolSize() {
        return this.executor != null ? this.executor.getLargestPoolSize() : 0;
    }

    public int getPoolSize() {
        return this.executor != null ? this.executor.getPoolSize() : 0;
    }

    class TaskThreadFactory implements ThreadFactory {

        final ThreadGroup group;
        final AtomicInteger threadNumber = new AtomicInteger(1);
        final String namePrefix;

        TaskThreadFactory(String namePrefix) {
            SecurityManager s = System.getSecurityManager();
            this.group = (s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup());
            this.namePrefix = namePrefix;
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement());
            t.setDaemon(CommonThreadPoolExecutor.this.daemon);
            t.setPriority(CommonThreadPoolExecutor.this.getThreadPriority());
            return t;
        }
    }

    class TaskQueue extends LinkedBlockingQueue<Runnable> {

        private static final long serialVersionUID = 602774934832011249L;

        ThreadPoolExecutor parent = null;

        public TaskQueue(int initialCapacity) {
            super(initialCapacity);
        }

        public TaskQueue() {
            super();
        }

        public void setParent(ThreadPoolExecutor tp) {
            this.parent = tp;
        }

        public boolean force(Runnable o) {
            if (this.parent.isShutdown())
                throw new RejectedExecutionException("Executor not running, can't force a command into the queue");
            return super.offer(o);
        }

        public boolean offer(Runnable o) {
            if (this.parent == null)
                return super.offer(o);

            if (this.parent.getPoolSize() == this.parent.getMaximumPoolSize())
                return super.offer(o);

            if (this.parent.getActiveCount() < this.parent.getPoolSize())
                return super.offer(o);

            if (this.parent.getPoolSize() < this.parent.getMaximumPoolSize())
                return false;

            return super.offer(o);
        }
    }

}
