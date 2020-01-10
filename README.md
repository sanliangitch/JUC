## 并发技巧清单
* 可变状态时至关重要的。
   * 所有的并发问题都可以归结为如何协调对并发状态的访问，可变状态越少，就越容易确保线程安全性。
* 尽量将域声明为final类型，除非需要他们是可变的。
* 不可变对象一定是线程安全的。
   * 不可变对象能极大地降低并发编程的复杂性。它们更为简单而且安全，可以任意共享而无需加锁或保护性复制等机制。
* 封装有助于管理复杂性。
   * 在编写线程安全程序时，虽然可以将所有数据都报错在全局变量中，但为上面要这样做？将数据封装在对象中，更易于维持不变性条件；将同步机制封装在对象中，更易于遵循同步策略。
* 用锁来保护每个可变变量。
* 当保护同一个不变性条件中的所有变量时，要使用同一个锁。
* 在执行复合操作期间，要持有锁。
* 如果从多个线程中访问同一个可变变量时没有同步机制，那么程序会出现问题。
* 不要故作聪明地推断出不需要使用同步。
* 在设计过程中考虑线程安全，或者在文档中明确指出它不是线程安全的。
* 将同步策略文档化。

## Executor
   1. **`执行策略`**。在执行策略中定义了任务执行的“What、Where、When、How”等方面
      * 在什么（What）线程中执行任务？
      * 任务按照什么（What）顺序执行（FIFO、LIFO、优先级）？
      * 有多少个（How Many）任务能并发执行？
      * 如果系统由于过载而需要拒绝一个任务，那么应该选择哪一个（Which）任务？另外，如何（How）通知程序有任务被拒绝？
      * 在执行一个任务之前或之后，应该进行哪些（What）动作？
   2. **`线程池`**。可以通过Executors中的静态工厂方法之一来创建一个线程池。
      * newFixedThreadPool。newFixedThreadPool 将创建一个固定长度的线程池，每当提交一个任务时就创建一个线程，知道达到线程池的最大数量，这时线程池的规模不再变化（如果某个线程由于发生了未预期的Exception而结束，那么线程池会补充一个新的线程）。
      * newCachedThreadPool。newCachedThreadPool 将创建一个可缓存的线程池，如果线程池的当前规模超过处理需求时，那么将回收空闲的线程，而当需求增加时，则可以添加新的线程，线程池的规模不存在任何限制。
      * newSingleThreadScheduledExecutor。 newSingleThreadScheduledExecutor 是一个单线程的Executor，它创建单个工作线程来执行任务，如果这个线程异常结束，会创建另一个线程来代替。newSingleThreadScheduledExecutor能确保依照任务在队列中的顺序来串行执行（例如FIFO、LIFO、优先级）。
      * newScheduledThreadPool。 newScheduledThreadPool 创建一个固定长度的线程池，而且以延迟或定时的方式来执行任务，类似于Timer。
   3. Executor的**生命周期**。 JVM只有在所有（非守护）线程全部终止后才会退出。因此，如果无法正确地关闭Executor，那么JVM将无法结束。
      * 为了解决执行服务的生命周期问题，Executor拓展了ExecutorService接口，添加了一些用于生命周期管理的方法（同时还有一些用于任务提交的便利方法）。 
```java
public interface ExecutorService extends Executor {
    /**
    * shutdown方法：将执行平缓的关闭过程；不再接受新的任务，同时等待已经提交的任务完成——包括哪些还未开始执行的任务。
    */
	void shutdown();
	/**
	* shutdownNow方法：将执行粗暴的关闭过程：它将尝试取消所有运行中的任务，并且不再启动队列中尚未开始执行的任务。
    * @return 
    */
	List<Runnable> shutdownNow();
	boolean isShutdown();
	boolean isTerminated();
	/**
	* 可以调用awaitTermination来等待ExecutorService到达最终止状态，或者通过调用isTerminated来轮询ExecutorService是否已经终止。
	* 通常在调用awaitTermination之后会立即调用shutdown，从而产生同步关闭ExecutorService的效果。
    * @param timeout
    * @param unit
    * @return 
    * @throws InterruptedException
    */
	boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException;
	// ....其他用于任务提交的便利方法
}
```
      ExecutorService的生命周期有3种状态;运行、关闭和终止。ExecutorService在创始初建时处于运行状态。在ExecutorService关闭后提交的任务将由“拒绝策略”来处理，它会抛弃任务。
   4. **CompletionService**与BlockingQueue  
      * 如果向Executor 提交了一组计算任务，并且希望在计算完成后获得结果，那么可以保留与每个任务关联的Future,然后反复使用get方法，同时将参数timeout指定为0,从而通过轮询来判断任务是否完成。这种方法虽然可行，但却有些繁琐。幸运的是，还有一种更好的方法:完成服务(CompletionService)。
      * CompletionService将Executor和BlockingQueue的功能融合在一一起。你可以将Callable任务提交给它来执行，然后使用类似于队列操作的take和poll等方法来获得已完成的结果，而这些结果会在完成时将被封装为Future。ExecutorCompletionService 实现了CompletionService,并将计算部分委托给一个 Executor。
      *  ExecutorCompletionService的实现非常简单。在构造函数中创建一个BlockingQueue 来保存计算完成的结果。当计算完成时，调用Future-Task中的done方法。当提交某个任务时，该任务将首先包装为一个QueueingFuture,这是FutureTask的一个子类，然后再改写子类的done方法，并将结果放入BlockingQueue中。
      * [详情代码](https://github.com/sanliangitch/JUC/blob/master/renderer/Renderer.java)
   ```java
  public class Renderer {
      private final ExecutorService executor;
      Renderer(ExecutorService executor){
          this.executor = executor;
      }
      void renderPage(CharSequence source){
          List<ImageInfo> info = scanForImageInfo(source);
          ExecutorCompletionService<ImageData> completionService =
                  new ExecutorCompletionService<>(executor);
          for (final ImageInfo imageInfo : info){
              completionService.submit(imageInfo::downloadImage);
          }
          renderText(source);
          try {
          for (int t = 0,n = info.size();t < n; t++){
              Future<ImageData> f = completionService.take();
              ImageData imageData = f.get();
              renderImage(imageData);
          }
          } catch (InterruptedException e) {
              e.printStackTrace();
          }catch (ExecutionException e){
              throw launderThrowable(e.getCause());
          }
      }
      }
```
   5. 为任务设置时限
      *   有时候，如果某个任务无法在指定时间内完成，那么将不再需要它的结果，此时可以放弃这个任务。例如，某个Web应用程序从外部的广告服务器上获取广告信息，但如果该应用程序在两秒钟内得不到响应，那么将显示一个默认的广告，这样即使不能获得广告信息，也不会降低站点的响应性能。类似地，一个门户网站可以从多个数据源并行地获取数据，但可能只会在指定的时间内等待数据，如果超出了等待时间，那么只显示已经获得的数据。
      *   在有限时间内执行任务的主要困难在于，要确保得到答案的时间不会超过限定的时间，或者在限定的时间内无法获得答案。在支持时间限制的Future.get中支持这种需求:当结果可用时，它将立即返回，如果在指定时限内没有计算出结果，那么将抛出TimeoutException。
      *   在使用限时任务时需要注意，当这些任务超时后应该立即停止，从而避免为继续计算一个不再使用的结果而浪费计算资源。要实现这个功能，可以由任务本身来管理它的限定时间，并且在超时后中止执行或取消任务。此时可再次使用Future,如果一个限时的get方法抛出了TimcoutException,那么可以通过Future来取消任务。
      * [详情代码](https://github.com/sanliangitch/JUC/blob/master/renderer/Renderer.java)
```java
/**
* 在指定时间内获取广告信息
*
* Future.get的一种典型应用。在它生成的页面中包括响应用户请求的内容以及从广告服务器上获得的广告。它将获取广告的任务提交给一个Executor,
* 然后计算剩余的文本页面内容，最后等待广告信息，直到超出指定的时间。如果get超时，那么将取消广告获取任务，并转而使用默认的广告信息。
*/
Page renderPageWithAd() throws InterruptedException{
        long endNanos = System.nanoTime() + TIME_BUDGET;
        Future<Ad> f = (Future<Ad>) executor.submit(new FetchAdTask());
        //在等待广告的同时显示页面
        Page page = renderPageBody();
        Ad ad;
        try {
            long timeLeft = endNanos - System.nanoTime();
            ad = f.get(timeLeft, TimeUnit.NANOSECONDS);
        } catch (ExecutionException e) {
            ad = DEFULT_AD;
        } catch (TimeoutException e) {
            ad = DEFULT_AD;
            f.cancel(true);
        }
        page.setAd(ad);
        return page;
    }
 ```  
   6. 为任务组设置时限
   * “预定时间”方法可以很容易地扩展到任意数量的任务上。考虑这样一个旅行预定门户网站:用户输入旅行的日期和其他要求，门户网站获取并显示来自多条航线、旅店或汽车租赁公司的报价。在获取不同公司报价的过程中，可能会调用Web服务、访问数据库、执行一个EDI事务或其他机制。在这种情况下，不宜让页面的响应时间受限于最慢的响应时间，而应该只显示在指定时间内收到的信息。对于没有及时响应的服务提供者，页面可以忽略它们，或者显示一个提示信息，例如"Did not hear from Air Java in time。”
   * 从一个公司获得报价的过程与从其他公司获得报价的过程无关，因此可以将获取报价的过程当成一个任务，从而使获得报价的过程能并发执行。创建n个任务，将其提交到一个线程池，保留n个Future,并使用限时的get方法通过Future串行地获取每一个结果，这一切都很简单，但还有一个更简单的方法一invokeAll。
   * [详情代码](https://github.com/sanliangitch/JUC/blob/master/renderer/QuoteTask.java)
```java
/**
* 使用了支持限时的invokeAll,将多个任务提交到一个ExecutorService并获得结果。
*
* InvokeAll 方法的参数为一组任务，并返回一组Future.这两个集合有着相同的结构。
* invokeAll按照任务集合中迭代器的顺序将所有的Future添加到返回的集合中，从而使调用者能将各个Future与其表示的Callable关联起来。
* 当所有任务都执行完毕时，或者调用线程被中断时，又或者超过指定时限时，invokeAll 将返回。
* 当超过指定时限后，任何还未完成的任务都会取消。
* 当invokeAll返回后，每个任务要么正常地完成，要么被取消，而客户端代码可以调用get或isCancelled来判断究竟是何种情况。
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
 ```     