package cn.unbug.autosign.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @PackageName: cn.unbug.autosign.thread
 * @ClassName: ThreadTransaction
 * @Description: []
 * @Author: zhangtao
 * @Date: 2022/07/28 21:09:44
 * @Version: V1.0
 **/
@Slf4j
@Component
public class ThreadTransaction {

    private DataSourceTransactionManager txManager;

    private DefaultTransactionDefinition transactionDefinition;

    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    public void setTxManager(DataSourceTransactionManager txManager) {
        this.txManager = txManager;
    }

    @Autowired
    public void setThreadPoolTaskExecutor(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    @Autowired
    public void setTransactionDefinition(DefaultTransactionDefinition transactionDefinition) {
        transactionDefinition.setIsolationLevel(DefaultTransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionDefinition.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.transactionDefinition = transactionDefinition;
    }


    public void execute(AtomicInteger integer, CopyOnWriteArrayList<AtomicBoolean> bools, Runnable... task) {
        log.info("=== ThreadTransaction execute start ===");
        for (Runnable runnable : task) {
            Invocation invocation = new Invocation(runnable, task.length, bools, integer);
            threadPoolTaskExecutor.execute(invocation);
        }
    }


    private class Invocation implements Runnable {

        private final Runnable runnable;

        private final int threadNum;

        private final CopyOnWriteArrayList<AtomicBoolean> bools;

        private final AtomicInteger passThread;

        public Invocation(Runnable runnable, int threadNum, CopyOnWriteArrayList<AtomicBoolean> bools, AtomicInteger integer) {
            this.runnable = runnable;
            this.threadNum = threadNum;
            this.bools = bools;
            this.passThread = integer;
        }

        @Override
        public void run() {
            TransactionStatus transactionStatus = txManager.getTransaction(transactionDefinition);
            boolean isItDone = true;
            try {
                runnable.run();
                passThread.getAndIncrement();
                AtomicBoolean atomicBoolean = new AtomicBoolean(true);
                bools.add(atomicBoolean);
            } catch (Exception e) {
                log.error("=== Invocation  run  excption ===", e);
                txManager.rollback(transactionStatus);
                AtomicBoolean atomicBoolean = new AtomicBoolean(false);
                bools.add(atomicBoolean);
                isItDone = false;
            }
            while (isItDone) {
                List<AtomicBoolean> falseBools = bools.stream().filter(bool -> !bool.get()).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(falseBools)) {
                    log.info("=== Invocation run {}  ===", falseBools.size());
                    isItDone = false;
                    txManager.rollback(transactionStatus);
                } else if (passThread.get() == threadNum) {
                    log.info("=== Invocation run {}  ===", falseBools.size());
                    isItDone = false;
                    txManager.commit(transactionStatus);
                }
            }
        }
    }
}
