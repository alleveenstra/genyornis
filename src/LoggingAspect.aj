import nl.alleveenstra.genyornis.httpd.HttpRequest;

public aspect LoggingAspect {
	pointcut handlerCall(HttpRequest request) : execution (void handle(..)) && args(request, *);
	before(HttpRequest request) : handlerCall(request) {
	  // TODO implement some decent logging
		System.out.println(thisJoinPoint.getTarget().getClass().getCanonicalName() + ": " + request.getUri());
	}
}
