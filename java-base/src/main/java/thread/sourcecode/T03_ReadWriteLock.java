package thread.sourcecode;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class T03_ReadWriteLock {
    String data;
    volatile boolean cacheValid;
    final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    void processCachedData() {
        rwl.readLock().lock();
        if (!cacheValid) {
            // Must release read lock before acquiring write lock
            rwl.readLock().unlock();
            rwl.writeLock().lock();
            try {
                // Recheck state because another thread might have
                // acquired write lock and changed state before we did.
                if (!cacheValid) {
                    data = "Hello ";
                    cacheValid = true;
                }
                // Downgrade by acquiring read lock before releasing write lock
                rwl.readLock().lock();
            } finally {
                rwl.writeLock().unlock(); // Unlock write, still hold read
            }
        }

        try {
            System.out.println(data);
        } finally {
            rwl.readLock().unlock();
        }
    }

    public static void main(String[] args) {
        T03_ReadWriteLock t=new T03_ReadWriteLock();
        t.processCachedData();
    }
}
