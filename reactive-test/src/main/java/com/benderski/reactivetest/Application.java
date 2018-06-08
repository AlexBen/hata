package com.benderski.reactivetest;
//
//import com.benderski.hata.infrastructure.TaskScheduler;
//import io.reactivex.observers.DisposableObserver;
//import io.reactivex.subjects.PublishSubject;
//import io.reactivex.subjects.Subject;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//
//import java.util.concurrent.TimeUnit;
//
//
//@SpringBootApplication
//@ComponentScan({"com.benderski.hata.integration", "com.benderski.hata.reactivetest"})
public class Application {}
//
//    public static int i = 0;
//
//    public static void main(String[] args) {
//        SpringApplication.run(Application.class, args);
//
//    }
//
//    public class PrintSubscriber extends DisposableObserver<Integer> {
//
//        String name;
//
//        public PrintSubscriber(String name) {
//            this.name = name;
//        }
//
//        @Override
//        public void onNext(Integer o) {
//            System.out.println(name + " on next " + o);
//        }
//
//        @Override
//        public void onError(Throwable throwable) {
//            System.out.println(name + " on error ");
//
//        }
//
//        @Override
//        public void onComplete() {
//            System.out.println(name + " on complete ");
//        }
//    }
//
//    @Bean
//    public CommandLineRunner commandLineRunner(ApplicationContext ctx) throws InterruptedException {
//        TaskScheduler taskScheduler = new TaskScheduler();
//        Subject<Integer> subject = PublishSubject.create();
//        taskScheduler.scheduleRepeatableTask(createTask(subject), 5, TimeUnit.SECONDS);
//        DisposableObserver first = new PrintSubscriber("frist");
//        subject.subscribe(first);
//        DisposableObserver second = new PrintSubscriber("second");
//        subject.skip(4).subscribe(second);
//        subject.take(3).subscribe(new PrintSubscriber("third"));
//
//        return (args -> {});
//    }
//
//    private Runnable createTask(Subject<Integer> subject) {
//        return () -> {
//            if (i < 20) {
//                subject.onNext(i++);
//            } else {
//                subject.onComplete();
//            }
//        };
//
//    }
//}
