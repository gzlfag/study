package com.juc;

import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import sun.security.util.SecurityConstants;

/**
 * 此包中定义的{@link Executor}，{@link ExecutorService}，{@link ScheduledExecutorService}，
 * {@link ThreadFactory}和{@link Callable}类的工厂和实用程序方法
 * 该类支持以下几种方法:
 * <li> 创建并返回{@link ExecutorService}的方法，其中设置了常用的配置设置.
 * <li> 创建并返回{@link ScheduledExecutorService}的方法，其中设置了常用的配置设置.
 * <li> 创建并返回“包装”ExecutorService的方法，通过使实现特定的方法无法访问来禁用重新配置.
 * <li> 创建并返回将新创建的线程设置为已知状态的{@link ThreadFactory}的方法.
 * <li> 从其他类似闭包的表单中创建并返回{@link Callable}的方法，因此可以在执行方法中使用{@code Callable}
 */
@SuppressWarnings("restriction")
public class Executors {

    /**
     * 创建一个线程池，重新使用固定数量的线程从共享无界队列中运行.
     * 在任何时候，最多{@code nThreads}线程将是主动处理任务.
     * 如果所有线程处于活动状态时都会提交其他任务，那么它们将等待队列直到线程可用.
     * 如果任何线程由于在关闭之前的执行期间发生故障而终止，则如果需要执行后续任务，则新线程将占用它
     * 池中的线程将存在，直到它显式{@link ExecutorService＃shutdown shutdown}.
     *
     * @param nThreads 池中的线程数
     * @return 新创建的线程池
     * @throws IllegalArgumentException if {@code nThreads <= 0}
     */
    public static ExecutorService newFixedThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    /**
     * 创建一个线程池，它维护足够的线程来支持给定的并行级别，并且可以使用多个队列来减少争用.
     * 并行级别对应于主动参与或可用于从事任务处理的最大线程数
     * 线程的实际数量可以动态增长和收缩。
     * 工作窃取池不能保证执行提交的任务的顺序.
     *
     * @param
     * @return 新创建的线程池
     * @throws IllegalArgumentException if {@code parallelism <= 0}
     * @since 1.8
     */
    public static ExecutorService newWorkStealingPool(int parallelism) {
        return new ForkJoinPool
                (parallelism,
                        ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                        null, true);
    }

    /**
     * 使用所有{@link Runtime＃availableProcessors可用处理器}作为目标并行级别创建一个工作窃取线程池.
     *
     * @since 1.8
     */
    public static ExecutorService newWorkStealingPool( ) {
        return new ForkJoinPool
                (Runtime.getRuntime().availableProcessors(),
                        ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                        null, true);
    }

    /**
     * 创建一个线程池，重新使用固定数量的线程从共享无界队列中运行，使用提供的ThreadFactory在需要时创建新线程.
     * 在任何时候，最多{@code nThreads}线程将是主动处理任务。
     * 如果所有线程处于活动状态时都会提交其他任务，那么它们将等待队列直到线程可用.
     * 如果任何线程由于在关闭之前的执行期间发生故障而终止，则如果需要执行后续任务，则新线程将占用它.
     * 池中的线程将存在，直到它显式{@link ExecutorService＃shutdown shutdown}.
     *
     * @param nThreads      the number of threads in the pool
     * @param threadFactory 创建线程的工厂
     * @return the newly created thread pool
     * @throws NullPointerException     if threadFactory is null
     * @throws IllegalArgumentException if {@code nThreads <= 0}
     */
    public static ExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                threadFactory);
    }

    /**
     * 创建一个使用从无界队列运行的单个工作线程的执行程序.
     * （请注意，如果此单个线程由于在关闭之前的执行过程中发生故障而终止，则如果需要执行后续任务，则新的线程将占用它。）
     * 任务保证顺序执行，任何给定时间内不会有任务超过一个任务.
     * 不同于其他等效的{@code newFixedThreadPool（1）}，返回的执行器被保证不被重新配置以使用额外的线程.
     *
     * @return the newly created single-threaded Executor
     */
    public static ExecutorService newSingleThreadExecutor( ) {
        return new FinalizableDelegatedExecutorService
                (new ThreadPoolExecutor(1, 1,
                        0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>()));
    }

    /**
     * 通过工厂创建线程
     *
     * @return the newly created single-threaded Executor
     * @throws NullPointerException if threadFactory is null
     */
    public static ExecutorService newSingleThreadExecutor(ThreadFactory threadFactory) {
        return new FinalizableDelegatedExecutorService
                (new ThreadPoolExecutor(1, 1,
                        0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>(),
                        threadFactory));
    }

    /**
     * 创建一个根据需要创建新线程的线程池，但在可用时将重新使用以前构造的线程.
     * 这些池通常会提高执行许多短暂异步任务的程序的性能.
     * 调用{@code execute}将重用以前构造的线程（如果可用）。
     * 如果没有现有线程可用，将创建一个新线程并将其添加到池中.
     * 未使用六十秒的线程将被终止并从缓存中删除.
     * 因此，长时间保持空闲的池将不会消耗任何资源.
     * 请注意，可以使用{@link ThreadPoolExecutor}构造函数创建具有相似属性但不同详细信息（例如，超时参数）的池.
     * <p>
     * <p>
     * 核心线程数0
     * SynchronousQueue的容量为0.塞进去成功的前提是:刚好有一个队列从里面拿数据.
     * 拿出来的前提是:刚好有一个塞数据.
     * 就是一个数据交换功能,不能保存.
     * 从0开始增长线程的数量,一直增长到integer的最大值
     */
    public static ExecutorService newCachedThreadPool( ) {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
    }

    /**
     * 通过工厂创建
     */
    public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                threadFactory);
    }

    /**
     * 创建一个单线程执行程序，可以调度命令在给定的延迟之后运行，或定期执行。
     * （请注意，如果此单个线程由于在关闭之前的执行过程中发生故障而终止，则如果需要执行后续任务，则新的线程将占用它。）
     */
    public static ScheduledExecutorService newSingleThreadScheduledExecutor( ) {
        return new DelegatedScheduledExecutorService
                (new ScheduledThreadPoolExecutor(1));
    }

    /**
     * 工厂模式创建
     */
    public static ScheduledExecutorService newSingleThreadScheduledExecutor(ThreadFactory threadFactory) {
        return new DelegatedScheduledExecutorService
                (new ScheduledThreadPoolExecutor(1, threadFactory));
    }

    /**
     * 创建可以调度命令在给定延迟之后运行或定期执行的线程池.
     *
     * @param corePoolSize the number of threads to keep in the pool,
     *                     even if they are idle
     * @return a newly created scheduled thread pool
     * @throws IllegalArgumentException if {@code corePoolSize < 0}
     */
    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
        return new ScheduledThreadPoolExecutor(corePoolSize);
    }

    /**
     * 工厂模式
     */
    public static ScheduledExecutorService newScheduledThreadPool(
            int corePoolSize, ThreadFactory threadFactory) {
        return new ScheduledThreadPoolExecutor(corePoolSize, threadFactory);
    }

    /**
     * 返回一个将所有定义的{@link ExecutorService}方法委托给给定执行程序的对象，但不能以其他方式使用转换方式访问
     * 这提供了一种安全地“冻结”配置并且不允许调整给定的具体实现的方法.
     *
     * @param executor 底层实现
     * @return an {@code ExecutorService} instance
     * @throws NullPointerException if executor null
     */
    public static ExecutorService unconfigurableExecutorService(ExecutorService executor) {
        if (executor == null)
            throw new NullPointerException();
        return new DelegatedExecutorService(executor);
    }

    /**
     * Returns an object that delegates all defined {@link
     * ScheduledExecutorService} methods to the given executor, but
     * not any other methods that might otherwise be accessible using
     * casts. This provides a way to safely "freeze" configuration and
     * disallow tuning of a given concrete implementation.
     *
     * @param executor the underlying implementation
     * @return a {@code ScheduledExecutorService} instance
     * @throws NullPointerException if executor null
     */
    public static ScheduledExecutorService unconfigurableScheduledExecutorService(ScheduledExecutorService executor) {
        if (executor == null)
            throw new NullPointerException();
        return new DelegatedScheduledExecutorService(executor);
    }

    /**
     * Returns a default thread factory used to create new threads.
     * This factory creates all new threads used by an Executor in the
     * same {@link ThreadGroup}. If there is a {@link
     * java.lang.SecurityManager}, it uses the group of {@link
     * System#getSecurityManager}, else the group of the thread
     * invoking this {@code defaultThreadFactory} method. Each new
     * thread is created as a non-daemon thread with priority set to
     * the smaller of {@code Thread.NORM_PRIORITY} and the maximum
     * priority permitted in the thread group.  New threads have names
     * accessible via {@link Thread#getName} of
     * <em>pool-N-thread-M</em>, where <em>N</em> is the sequence
     * number of this factory, and <em>M</em> is the sequence number
     * of the thread created by this factory.
     *
     * @return a thread factory
     */
    public static ThreadFactory defaultThreadFactory( ) {
        return new DefaultThreadFactory();
    }

    /**
     * 返回一个用于创建与当前线程具有相同权限的新线程的线程工厂.
     * 该工厂创建与{@link Executors＃defaultThreadFactory}设置相同的线程，
     * 另外将新线程的AccessControlContext和contextClassLoader设置为与调用
     * 此{@code privilegedThreadFactory}方法的线程相同.
     * 可以在{@link AccessController＃doPrivileged AccessController.doPrivileged}操作
     * 中创建一个新的{@code privilegedThreadFactory}，
     * 设置当前线程的访问控制上下文以创建具有该操作中所选的权限设置的线程.
     *
     * <p>请注意，虽然在这些线程中运行的任务将具有与当前线程相同的访问控制和类加载器设置，
     * 但它们不需要具有相同的{@link java.lang.ThreadLocal}或{@link java.lang.InheritableThreadLocal}值.
     * 如果需要，可以使用{@link ThreadPoolExecutor＃beforeExecute（Thread，Runnable）}
     * 在{@link ThreadPoolExecutor}子类中运行任何任务之前设置或重置线程本地的特定值。
     * 此外，如果需要初始化工作线程以使其具有与其他指定线程相同的InheritableThreadLocal设置，
     * 则可以创建一个自定义ThreadFactory，该线程等待和服务请求创建将继承其值的其他人.
     *
     * @return a thread factory
     * @throws AccessControlException if the current access control
     *                                context does not have permission to both get and set context
     *                                class loader
     */
    public static ThreadFactory privilegedThreadFactory( ) {
        return new PrivilegedThreadFactory();
    }

    /**
     * 返回一个{@link Callable}对象，该对象在被调用时运行给定的任务并返回给定的结果.
     * 当将需要{@code Callable}的方法应用于其他无结果的操作时，这可能很有用.
     */
    public static <T> Callable<T> callable(Runnable task, T result) {
        if (task == null)
            throw new NullPointerException();
        return new RunnableAdapter<T>(task, result);
    }

    /**
     * 返回一个{@link Callable}对象，该对象在被调用时运行给定的任务并返回{@code null}.
     *
     * @param task the task to run
     * @return a callable object
     * @throws NullPointerException if task null
     */
    public static Callable<Object> callable(Runnable task) {
        if (task == null)
            throw new NullPointerException();
        return new RunnableAdapter<Object>(task, null);
    }

    /**
     * 返回一个{@link Callable}对象，该对象在被调用时运行给定的特权操作并返回其结果.
     *
     * @param action the privileged action to run
     * @return a callable object
     * @throws NullPointerException if action null
     */
    public static Callable<Object> callable(final PrivilegedAction<?> action) {
        if (action == null)
            throw new NullPointerException();
        return new Callable<Object>() {
            public Object call( ) {
                return action.run();
            }
        };
    }

    /**
     * 返回一个{@link Callable}对象，该对象在被调用时运行给定的特权异常操作并返回其结果.
     *
     * @param action the privileged exception action to run
     * @return a callable object
     * @throws NullPointerException if action null
     */
    public static Callable<Object> callable(final PrivilegedExceptionAction<?> action) {
        if (action == null)
            throw new NullPointerException();
        return new Callable<Object>() {
            public Object call( ) throws Exception {
                return action.run();
            }
        };
    }

    /**
     * Returns a {@link Callable} object that will, when called,
     * execute the given {@code callable} under the current access
     * control context. This method should normally be invoked within
     * an {@link AccessController#doPrivileged AccessController.doPrivileged}
     * action to create callables that will, if possible, execute
     * under the selected permission settings holding within that
     * action; or if not possible, throw an associated {@link
     * AccessControlException}.
     *
     * @param callable the underlying task
     * @param <T>      the type of the callable's result
     * @return a callable object
     * @throws NullPointerException if callable null
     */
    public static <T> Callable<T> privilegedCallable(Callable<T> callable) {
        if (callable == null)
            throw new NullPointerException();
        return new PrivilegedCallable<T>(callable);
    }

    /**
     * Returns a {@link Callable} object that will, when called,
     * execute the given {@code callable} under the current access
     * control context, with the current context class loader as the
     * context class loader. This method should normally be invoked
     * within an
     * {@link AccessController#doPrivileged AccessController.doPrivileged}
     * action to create callables that will, if possible, execute
     * under the selected permission settings holding within that
     * action; or if not possible, throw an associated {@link
     * AccessControlException}.
     *
     * @param callable the underlying task
     * @param <T>      the type of the callable's result
     * @return a callable object
     * @throws NullPointerException   if callable null
     * @throws AccessControlException if the current access control
     *                                context does not have permission to both set and get context
     *                                class loader
     */
    public static <T> Callable<T> privilegedCallableUsingCurrentClassLoader(Callable<T> callable) {
        if (callable == null)
            throw new NullPointerException();
        return new PrivilegedCallableUsingCurrentClassLoader<T>(callable);
    }

    // Non-public classes supporting the public methods

    /**
     * 运行给定任务并返回给定结果的可调用
     */
    static final class RunnableAdapter<T> implements Callable<T> {
        final Runnable task;
        final T result;

        RunnableAdapter(Runnable task, T result) {
            this.task = task;
            this.result = result;
        }

        public T call( ) {
            task.run();
            return result;
        }
    }

    /**
     * 在可用的访问控制设置下运行的可调用
     */
    static final class PrivilegedCallable<T> implements Callable<T> {
        private final Callable<T> task;
        private final AccessControlContext acc;

        PrivilegedCallable(Callable<T> task) {
            this.task = task;
            this.acc = AccessController.getContext();
        }

        public T call( ) throws Exception {
            try {
                return AccessController.doPrivileged(
                        new PrivilegedExceptionAction<T>() {
                            public T run( ) throws Exception {
                                return task.call();
                            }
                        }, acc);
            } catch (PrivilegedActionException e) {
                throw e.getException();
            }
        }
    }

    /**
     * 一个可以在既定访问控制设置和当前ClassLoader下运行的可调用
     */
    static final class PrivilegedCallableUsingCurrentClassLoader<T> implements Callable<T> {
        private final Callable<T> task;
        private final AccessControlContext acc;
        private final ClassLoader ccl;

        PrivilegedCallableUsingCurrentClassLoader(Callable<T> task) {
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                // Calls to getContextClassLoader from this class
                // never trigger a security check, but we check
                // whether our callers have this permission anyways.
                sm.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);

                // Whether setContextClassLoader turns out to be necessary
                // or not, we fail fast if permission is not available.
                sm.checkPermission(new RuntimePermission("setContextClassLoader"));
            }
            this.task = task;
            this.acc = AccessController.getContext();
            this.ccl = Thread.currentThread().getContextClassLoader();
        }

        public T call( ) throws Exception {
            try {
                return AccessController.doPrivileged(
                        new PrivilegedExceptionAction<T>() {
                            public T run( ) throws Exception {
                                Thread t = Thread.currentThread();
                                ClassLoader cl = t.getContextClassLoader();
                                if (ccl == cl) {
                                    return task.call();
                                } else {
                                    t.setContextClassLoader(ccl);
                                    try {
                                        return task.call();
                                    } finally {
                                        t.setContextClassLoader(cl);
                                    }
                                }
                            }
                        }, acc);
            } catch (PrivilegedActionException e) {
                throw e.getException();
            }
        }
    }

    /**
     * 默认线程工厂
     */
    static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory( ) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

    /**
     * 线程工厂捕获访问控制上下文和类加载器
     */
    static class PrivilegedThreadFactory extends DefaultThreadFactory {
        private final AccessControlContext acc;
        private final ClassLoader ccl;

        PrivilegedThreadFactory( ) {
            super();
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                // Calls to getContextClassLoader from this class
                // never trigger a security check, but we check
                // whether our callers have this permission anyways.
                sm.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);

                // Fail fast
                sm.checkPermission(new RuntimePermission("setContextClassLoader"));
            }
            this.acc = AccessController.getContext();
            this.ccl = Thread.currentThread().getContextClassLoader();
        }

        public Thread newThread(final Runnable r) {
            return super.newThread(new Runnable() {
                public void run( ) {
                    AccessController.doPrivileged(new PrivilegedAction<Void>() {
                        public Void run( ) {
                            Thread.currentThread().setContextClassLoader(ccl);
                            r.run();
                            return null;
                        }
                    }, acc);
                }
            });
        }
    }

    /**
     * 一个仅暴露ExecutorService实现的ExecutorService方法的包装类.
     */
    static class DelegatedExecutorService extends AbstractExecutorService {
        private final ExecutorService e;

        DelegatedExecutorService(ExecutorService executor) {
            e = executor;
        }

        public void execute(Runnable command) {
            e.execute(command);
        }

        public void shutdown( ) {
            e.shutdown();
        }

        public List<Runnable> shutdownNow( ) {
            return e.shutdownNow();
        }

        public boolean isShutdown( ) {
            return e.isShutdown();
        }

        public boolean isTerminated( ) {
            return e.isTerminated();
        }

        public boolean awaitTermination(long timeout, TimeUnit unit)
                throws InterruptedException {
            return e.awaitTermination(timeout, unit);
        }

        public Future<?> submit(Runnable task) {
            return e.submit(task);
        }

        public <T> Future<T> submit(Callable<T> task) {
            return e.submit(task);
        }

        public <T> Future<T> submit(Runnable task, T result) {
            return e.submit(task, result);
        }

        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
                throws InterruptedException {
            return e.invokeAll(tasks);
        }

        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
                                             long timeout, TimeUnit unit)
                throws InterruptedException {
            return e.invokeAll(tasks, timeout, unit);
        }

        public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
                throws InterruptedException, ExecutionException {
            return e.invokeAny(tasks);
        }

        public <T> T invokeAny(Collection<? extends Callable<T>> tasks,
                               long timeout, TimeUnit unit)
                throws InterruptedException, ExecutionException, TimeoutException {
            return e.invokeAny(tasks, timeout, unit);
        }
    }

    static class FinalizableDelegatedExecutorService
            extends DelegatedExecutorService {
        FinalizableDelegatedExecutorService(ExecutorService executor) {
            super(executor);
        }

        protected void finalize( ) {
            super.shutdown();
        }
    }

    /**
     * 仅公开ScheduledExecutorService实现的ScheduledExecutorService方法的包装类。
     */
    static class DelegatedScheduledExecutorService
            extends DelegatedExecutorService
            implements ScheduledExecutorService {
        private final ScheduledExecutorService e;

        DelegatedScheduledExecutorService(ScheduledExecutorService executor) {
            super(executor);
            e = executor;
        }

        public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
            return e.schedule(command, delay, unit);
        }

        public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
            return e.schedule(callable, delay, unit);
        }

        public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
            return e.scheduleAtFixedRate(command, initialDelay, period, unit);
        }

        public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
            return e.scheduleWithFixedDelay(command, initialDelay, delay, unit);
        }
    }

    /**
     * 不能实例化
     */
    private Executors( ) {
    }
}