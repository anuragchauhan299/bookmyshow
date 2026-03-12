package movie.service.bookmyshow.constant;

public final class AppConstants {

    public static final class ErrorMessage {
        public static final String SHOW_NOT_FOUND = "Show not found: ";
        public static final String BOOKING_NOT_FOUND = "Booking not found: ";
        public static final String SEAT_NOT_AVAILABLE = "Seat %s is not available";
        public static final String SEAT_CURRENTLY_HELD = "Seat %s is currently held";
        public static final String SEAT_DOES_NOT_EXIST = "Some seats do not exist";
        public static final String SHOW_NOT_ACTIVE = "Show is not active";
        public static final String BOOKING_NOT_PENDING = "Booking is not in pending state";
        public static final String BOOKING_ALREADY_CANCELLED = "Booking is already cancelled";
        public static final String MAX_SEATS_EXCEEDED = "Maximum %d seats allowed per booking";
        public static final String MAX_BOOKINGS_EXCEEDED = "Maximum %d bookings allowed per day";
    }

    public static final class Audit {
        public static final String BOOKING_CREATED = "Booking created for show: ";
        public static final String BOOKING_CONFIRMED = "Booking confirmed with payment: ";
        public static final String BOOKING_CANCELLED = "Booking cancelled. Reason: ";
        public static final String PAYMENT_PROCESSED = "Payment processed for booking: ";
        public static final String USER_LOGIN = "User login from IP: ";
        public static final String SEATS_SELECTED = "Seats selected for show";
    }

    public static final class Offer {
        public static final String THIRD_TICKET_DISCOUNT_APPLIED = "Third ticket discount applied";
        public static final String AFTERNOON_SHOW_DISCOUNT_APPLIED = "Afternoon show discount applied";
    }

    public static final class Payment {
        public static final String STATUS_CAPTURED = "captured";
        public static final String STATUS_REFUNDED = "refunded";
        public static final String STATUS_VERIFIED = "verified";
        public static final String MESSAGE_PAYMENT_SUCCESS = "Payment successful via ";
        public static final String MESSAGE_REFUND_SUCCESS = "Refund processed successfully via ";
        public static final String MESSAGE_VERIFIED_SUCCESS = "Payment verified successfully";
        public static final String ID_PREFIX_ADYEN = "adyen_";
        public static final String ID_PREFIX_RAZORPAY = "razorpay_";
        public static final String ID_PREFIX_STRIPE = "stripe_";
        public static final String ID_PREFIX_REFUND = "refund_";
    }

    private AppConstants() {
    }
}
