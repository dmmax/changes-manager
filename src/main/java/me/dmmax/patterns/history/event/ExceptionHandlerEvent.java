package me.dmmax.patterns.history.event;

public class ExceptionHandlerEvent implements ChangesEvent {

    private final Throwable throwable;

    public ExceptionHandlerEvent(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable throwable() {
        return throwable;
    }
}
