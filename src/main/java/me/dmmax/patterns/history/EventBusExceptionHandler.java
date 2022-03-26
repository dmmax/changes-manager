package me.dmmax.patterns.history;

import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import me.dmmax.patterns.history.event.ExceptionHandlerEvent;

public class EventBusExceptionHandler implements SubscriberExceptionHandler {

    private static final EventBusExceptionHandler INSTANCE = new EventBusExceptionHandler();

    @Override
    public void handleException(Throwable exception, SubscriberExceptionContext context) {
        context.getEventBus()
                .post(new ExceptionHandlerEvent(new RuntimeException(message(context), exception)));
    }

    public static EventBusExceptionHandler getInstance() {
        return INSTANCE;
    }

    private static String message(SubscriberExceptionContext context) {
        var method = context.getSubscriberMethod();
        return "Exception thrown by subscriber method "
                + method.getName()
                + '('
                + method.getParameterTypes()[0].getName()
                + ')'
                + " on subscriber "
                + context.getSubscriber()
                + " when dispatching event: "
                + context.getEvent();
    }
}
