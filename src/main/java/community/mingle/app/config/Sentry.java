package community.mingle.app.config;


import io.sentry.SentryEvent;

public class Sentry {
    public static void main(String[] args) {
        io.sentry.Sentry.init(options ->
        {
            options.setDsn("https://701591fa1e114b3cab2c2aaff7f10929@o4504344089067520.ingest.sentry.io/4504344297209856");
            // Set tracesSampleRate to 1.0 to capture 100% of transactions for performance monitoring.
            // We recommend adjusting this value in production.
            options.setTracesSampleRate(1.0);
            // When first trying Sentry it's good to see what the SDK is doing:
            options.setDebug(true);
        });

        try {
            throw new Exception("This is a test.");
        } catch (Exception e) {
            Sentry.captureException(e);
        }

    }

    static void captureException(Exception e) {
    }


    public static void captureEvent(SentryEvent event) {
    }
}

//import io.sentry.Sentry;
//import io.sentry.SentryLevel;
//
//
//public class SentryConfig {
//    public static void main(String... args) {
//        Sentry.init(options -> {
//            options.setDsn("https://adc51d49b73c48cd8b3fef1bb4770ffe@o4504344089067520.ingest.sentry.io/4504344098766848");
//
//            // All events get assigned to the release. See more at
//            // https://docs.sentry.io/workflow/releases/
//            options.setRelease("io.sentry.samples.console@4.2.0+1");
//
//            // Modifications to event before it goes out. Could replace the event altogether
//            options.setBeforeSend((event, hint) -> {
//                // Drop an event altogether:
//                if (event.getTag("SomeTag") != null) {
//                    return null;
//                }
//                return event;
//            });
//
//            // Allows inspecting and modifying, returning a new or simply rejecting (returning null)
//            options.setBeforeBreadcrumb((breadcrumb, hint) -> {
//                // Don't add breadcrumbs with message containing:
//                if (breadcrumb.getMessage() != null
//                        && breadcrumb.getMessage().contains("bad breadcrumb")) {
//                    return null;
//                }
//                return breadcrumb;
//            });
//
//            // Enable SDK logging with Debug level
//            options.setDebug(true);
//            // To change the verbosity, use:
//            options.setDiagnosticLevel(
//                    // By default it's DEBUG.
//                    // A good option to have SDK debug log in prod is to use only level
//                    // ERROR here.
//                    SentryLevel.ERROR
//            );
//
//            // Exclude frames from some packages from being "inApp" so are hidden by default in Sentry
//            // UI:
//            options.addInAppExclude("org.jboss");
//        });
//    }
//}