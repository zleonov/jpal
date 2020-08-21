package software.leonov.common.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Uninterruptibles;

public class Test {

    public static void main(String... args) throws InterruptedException {
        
        final ThreadPoolExecutor exec = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>()) {
            @Override
            protected void beforeExecute(final Thread t, final Runnable r) {
                System.out.println(t.equals(Thread.currentThread()));
            }
        };
        
        exec.execute(() -> System.out.println("Hello, World"));
        
        ExecutorServices.ensureShutdown(exec);
        
//       System.out.println(System.getenv("JAVA_SOURCES"));
//
//        final ExecutorService exec = Executors.newFixedThreadPool(5);
//        final ListeningExecutorService les = MoreExecutors.listeningDecorator(exec);
//
//        final ListenableFuture<?> f = les.submit(() -> {
//            System.out.println("Executing in: " + Thread.currentThread().getName());
//            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
//        });
//        MoreFutures.addCallback(f, new FutureCallback<Object>() {
//
//            @Override
//            public void onSuccess(@Nullable Object result) {
//                System.out.println("Executing callback in: " + Thread.currentThread().getName());
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                // TODO Auto-generated method stub
//
//            }
//
//        });
//
//        IntStream.range(1, 10).forEach((int i) -> {
//            System.out.println(i);
//            Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);
//        });
//
//        ExecutorServices.ensureShutdown(les);

    }

}
