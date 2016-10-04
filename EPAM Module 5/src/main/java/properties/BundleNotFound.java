package properties;

/**
 * Exception thrown when bundle cannot be found
 */
class BundleNotFound extends Exception {
    private final Exception reason;

    BundleNotFound(Exception reason) {
        this.reason = reason;
    }
}
