package task1;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Processes money transfers ASYNCHRONOUSLY using {@code java.util.concurrent}
 */
public class TransferManagerConcurrent implements Runnable {
    private static final Logger LOG = LogManager.getLogger();
    private static final int NUMBER_OF_TRANSFER_PROCESSORS = 4;

    private final ExecutorService exec = Executors.newFixedThreadPool(NUMBER_OF_TRANSFER_PROCESSORS);
    private final Map<Account, ReadWriteLock> knownAccounts;
    private final BlockingDeque<TransferInfo> transfers = new LinkedBlockingDeque<>();

    TransferManagerConcurrent(Set<Account> initialState) {
        knownAccounts = new HashMap<>();
        initialState.parallelStream().forEach(account -> knownAccounts.put(account, new ReentrantReadWriteLock()));
    }

    public void loadTransfers(Deque<TransferInfo> moreTransfers) {
        transfers.addAll(moreTransfers);
    }

    public void waitForCompletion() {
        try {
            while (transfers.size() > 0) TimeUnit.MILLISECONDS.sleep(1000);
            LOG.info(() -> "Queue empty, shutting down threads");
            exec.shutdownNow();
            exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.error(() -> "Execution interrupted while waiting for processor threads to complete transfers");
        }
    }

    public Set<Account> getSnapshot() {
        LOG.info(() -> "Snapshot requested");

        knownAccounts.entrySet().stream().map(Map.Entry::getValue).map(ReadWriteLock::readLock).forEachOrdered(Lock::lock);

        LOG.info(() -> "Taking a snapshot of account database");
        Set<Account> snapShot = knownAccounts.keySet().parallelStream().map(Account::clone).collect(Collectors.toSet());

        knownAccounts.entrySet().stream().map(Map.Entry::getValue).map(ReadWriteLock::readLock).forEachOrdered(Lock::unlock);
        return snapShot;
    }

    @Override
    public void run() {
        LOG.info(() -> "Starting processor threads");
        for (int i = 0; i < NUMBER_OF_TRANSFER_PROCESSORS; i++) {
            exec.execute(new TransferProcessorConcurrent(i + 1));
        }

    }

    class TransferProcessorConcurrent implements Runnable {
        final int procNum;

        TransferProcessorConcurrent(int procNum) {
            this.procNum = procNum;
        }

        @Override
        public void run() {
            LOG.debug(() -> "Processor " + procNum + " is starting");

            try {
                while (true) {
                    TransferInfo currentElement = transfers.take();
                    final long from = currentElement.accountFrom;
                    final long to = currentElement.accountTo;

                    LOG.trace(() -> String.format("Processor %d is performing transaction from %d to %d", procNum, from, to));

                    try {
                        Account accFrom = knownAccounts.keySet().stream().filter(acc -> acc.getNumber() == from).findAny().get();
                        Account accTo = knownAccounts.keySet().stream().filter(acc -> acc.getNumber() == to).findAny().get();

                        ReadWriteLock lock1;
                        ReadWriteLock lock2;
                        if (accFrom.getNumber() > accTo.getNumber()) {
                            lock1 = knownAccounts.get(accTo);
                            lock2 = knownAccounts.get(accFrom);
                        } else {
                            lock1 = knownAccounts.get(accFrom);
                            lock2 = knownAccounts.get(accTo);
                        }
                        LOG.trace(() -> String.format("Processor %d is accessing acc. %d and %d", procNum, accFrom.getNumber(), accTo.getNumber()));

                        // Accounts must be ALWAYS locked starting with lowest-number account to prevent deadlocks.

                        lock1.writeLock().lock();
                        lock2.writeLock().lock();

                        LOG.debug(() -> String.format("Processor %d is performing transfer from acc. %d to acc. %d for %4.2f", procNum, accFrom.getNumber(), accTo.getNumber(), currentElement.amount));
                        accFrom.withdraw(currentElement.amount);
                        accTo.deposit(currentElement.amount);

                        lock2.writeLock().unlock();
                        lock1.writeLock().unlock();
                    } catch (NullPointerException | NoSuchElementException e) {
                        LOG.error(() -> "Operation on non-existing account");
                    }
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException("Shutdown request");
                    }

                    if (LOG.isDebugEnabled()) TimeUnit.MILLISECONDS.sleep(200);
                    else Thread.yield();
                }
            } catch (InterruptedException e) {
                LOG.debug(() -> "Processor " + procNum + " is stopping");
            }
        }
    }
}
