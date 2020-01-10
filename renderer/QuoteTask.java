package renderer;

import java.util.*;
import java.util.concurrent.*;

/**
 * 在预定时间内请求旅游报价
 *
 *
 * @author wulang
 * @create 2020/1/10/15:38
 */
public class QuoteTask implements Callable<TravelQuote> {
    private final TravelCompany company;
    private final TravelInfo travelInfo;

    public QuoteTask(TravelCompany company, TravelInfo travelInfo1) {
        this.company = company;
        this.travelInfo = travelInfo1;
    }

    @Override
    public TravelQuote call() throws Exception {
        return company.solicitQuote(travelInfo);
    }

    /**
     * 使用了支持限时的invokeAll,将多个任务提交到一个ExecutorService并获得结果。
     *
     * InvokeAll 方法的参数为一组任务，并返回一组Future.这两个集合有着相同的结构。
     * invokeAll按照任务集合中迭代器的顺序将所有的Future添加到返回的集合中，从而使调用者能将各个Future与其表示的Callable关联起来。
     * 当所有任务都执行完毕时，或者调用线程被中断时，又或者超过指定时限时，invokeAll 将返回。
     * 当超过指定时限后，任何还未完成的任务都会取消。
     * 当invokeAll返回后，每个任务要么正常地完成，要么被取消，而客户端代码可以调用get或isCancelled来判断究竟是何种情况。
     * @param travelInfo
     * @param companies
     * @param ranking
     * @param time
     * @param unit
     * @return
     * @throws InterruptedException
     */
    public List<TravelQuote> getRankedTravelQuote(
            TravelInfo travelInfo,
        Set<TravelCompany> companies,
        Comparator<TravelQuote> ranking, long time, TimeUnit unit)throws InterruptedException{
        ArrayList<QuoteTask> tasks = new ArrayList<>();
        for (TravelCompany company : companies){
            tasks.add(new QuoteTask(company,travelInfo));
        }
        List<Future<TravelQuote>> futures = exec.invokeAll(tasks, time, unit);
        ArrayList<TravelQuote> quotes = new ArrayList<>(tasks.size());
        Iterator<QuoteTask> taskIterator = tasks.iterator();
        for (Future<TravelQuote> f : futures){
            QuoteTask task = taskIterator.next();
            try {
                quotes.add(f.get());
            } catch (ExecutionException e) {
                quotes.add(task.getFailureQuote(e.getCause()));
            }catch (CancellationException e){
                quotes.add(task.getTimeoutQuote(e));
            }
        }
        Collections.sort(quotes,ranking);
        return quotes;
    }

    private TravelQuote getTimeoutQuote(CancellationException e) {
        return null;
    }

    private TravelQuote getFailureQuote(Throwable cause) {
        return null;
    }
    private final ExecutorService exec = new ExecutorService() {
        @Override
        public void shutdown() {

        }

        @Override
        public List<Runnable> shutdownNow() {
            return null;
        }

        @Override
        public boolean isShutdown() {
            return false;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return false;
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            return null;
        }

        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            return null;
        }

        @Override
        public Future<?> submit(Runnable task) {
            return null;
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
            return null;
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
            return null;
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
            return null;
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return null;
        }

        @Override
        public void execute(Runnable command) {

        }
    };
}
class TravelQuote{

}
class TravelCompany{

    public TravelQuote solicitQuote(TravelInfo travelInfo) {
        return null;
    }
}
class TravelInfo{

}