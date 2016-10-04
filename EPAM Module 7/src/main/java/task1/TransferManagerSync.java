package task1;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

/**
 * Processes money transfers ASYNCHRONOUSLY using {@code synchronized}
 */
public class TransferManagerSync implements Runnable {
    private static final Logger LOG = LogManager.getLogger();
    private static final int NUMBER_OF_TRANSFER_PROCESSORS = 4;

    private final SortedSet<Account> knownAccounts;
    private final Deque<TransferInfo> transfers = new LinkedList<>();
    private final ThreadGroup processors = new ThreadGroup("Processors");
    private AtomicInteger activeProcessors = new AtomicInteger(0);
    private boolean threadsActive = false;
    private boolean loadingDisabled = false;

    TransferManagerSync(Set<Account> initialState) {
        knownAccounts = new TreeSet<>();
        knownAccounts.addAll(initialState);
    }

    /**
     * Adds money transfers to the processing queue. Can be used while the processors are running.
     *
     * @param moreTransfers Transfers to add
     * @return False if operation fails because processor threads are currently shutting down
     */
    public boolean loadTransfers(Deque<TransferInfo> moreTransfers) {
        synchronized (transfers) {
            if (loadingDisabled) return false;
            transfers.addAll(moreTransfers);
            transfers.notify();
            return true;
        }
    }

    /**
     * Stops all processor activity. Waits until all processors have stopped.
     * Accounts are guaranteed to be in consistent state after this call.
     */
    public void stopTransfers() {
        synchronized (processors) {
            processors.interrupt();
            try {
                while (threadsActive) TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                LOG.error(() -> "Execution interrupted while stopping processor threads");
            }
        }
    }

    /**
     * Launches the transfer processors in asynchronous mode, returns after that.
     */
    @Override
    public void run() {
        synchronized (processors) {
            LOG.info(() -> "Starting processor threads");
            for (int i = 0; i < NUMBER_OF_TRANSFER_PROCESSORS; i++) {
                new Thread(processors, new TransferProcessorSync(i + 1)).start();
            }
            try {
                while (activeProcessors.get() < NUMBER_OF_TRANSFER_PROCESSORS) TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                LOG.error(() -> "Execution interrupted while stopping processor threads");
            }
            LOG.info(() -> "Processor threads running");
        }
    }

    /**
     * Gets a snapshot of the state of accounts by stopping/resuming all processors.
     *
     * @return Copy of account state. Guaranteed to be consistent
     */
    public Set<Account> getSnapshot() {
        Set<Account> snapShot;

        synchronized (processors) {
            LOG.info(() -> "Snapshot requested");
            boolean needRestart = threadsActive;
            stopTransfers();
            LOG.info(() -> "Taking a snapshot of account database");
            snapShot = knownAccounts.parallelStream().map(Account::clone).collect(Collectors.toSet());
            if (needRestart) run();
        }

        return snapShot;
    }

    /**
     * Lets processors run until all transfers are processed, stops them afterward.
     */
    public void waitForCompletion() {
        synchronized (transfers) {
            try {
                while (!transfers.isEmpty()) {
                    LOG.debug(() -> "Waiting for completion: " + transfers.size() + " remaining");
                    transfers.wait(1000);
                }
            } catch (InterruptedException e) {
                LOG.error(() -> "Execution interrupted while waiting for processor threads to complete transfers");
            }
            loadingDisabled = true;
        }
        LOG.debug(() -> "Stopping transfer threads");
        stopTransfers();
        loadingDisabled = false;
    }

    class TransferProcessorSync implements Runnable {
        final int procNum;

        TransferProcessorSync(int procNum) {
            this.procNum = procNum;
        }

        @SuppressWarnings({"OptionalGetWithoutIsPresent", "SynchronizationOnLocalVariableOrMethodParameter"})
        @Override
        public void run() {
            activeProcessors.incrementAndGet();
            threadsActive = true;
            LOG.debug(() -> "Processor " + procNum + " is starting");

            try {
                while (true) {
                    TransferInfo currentElement;

                    synchronized (transfers) {
                        if (transfers.isEmpty()) {
                            LOG.trace(() -> "Processor " + procNum + " is waiting for transfers to do");
                            transfers.wait();
                        }
                        currentElement = transfers.pollFirst();
                        transfers.notify();
                    }
                    if (currentElement != null) {
                        Account acc1;
                        Account acc2;
                        final long from = currentElement.accountFrom;
                        final long to = currentElement.accountTo;
                        Account accFrom = null;
                        Account accTo;
                        try {
                            accFrom = knownAccounts.stream().filter(acc -> acc.getNumber() == from).findAny().get();
                            accTo = knownAccounts.stream().filter(acc -> acc.getNumber() == to).findAny().get();

                            if (accFrom.getNumber() > accTo.getNumber()) {
                                acc1 = accTo;
                                acc2 = accFrom;
                            } else {
                                acc1 = accFrom;
                                acc2 = accTo;
                            }

                            LOG.debug(() -> String.format("Processor %d is accessing acc. %d and %d", procNum, acc1.getNumber(), acc2.getNumber()));

                            // Accounts must be ALWAYS locked starting with lowest-number account to prevent deadlocks.
                            synchronized (acc1) {
                                synchronized (acc2) {
                                    LOG.debug(() -> String.format("Processor %d is performing transfer from acc. %d to acc. %d for %4.2f", procNum, from, to, currentElement.amount));
                                    accFrom.withdraw(currentElement.amount);
                                    accTo.deposit(currentElement.amount);
                                }
                            }
                        } catch (NoSuchElementException e) {
                            final long invalidAccount = isNull(accFrom) ? currentElement.accountFrom : currentElement.accountTo;
                            LOG.error(() -> "Operation on non-existing account #" + invalidAccount);
                        }
                    }
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException("Shutdown request");
                    }

                    if (LOG.isDebugEnabled()) TimeUnit.MILLISECONDS.sleep(200);
                    else Thread.yield();
                }
            } catch (InterruptedException e) {
                LOG.debug(() -> "Processor " + procNum + " is stopping");
            } finally {
                if (activeProcessors.decrementAndGet() == 0) threadsActive = false;
            }
        }
    }

}
