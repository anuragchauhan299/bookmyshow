package movie.service.bookmyshow.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Booking {

    private String id;
    private String showId;
    private List<String> seats;
    private BigDecimal basePrice;
    private BigDecimal discountAmount;
    private BigDecimal totalPrice;
    private BookingStatus status;
    private List<String> appliedOffers = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public List<String> getSeats() {
        return seats;
    }

    public void setSeats(List<String> seats) {
        this.seats = seats;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public List<String> getAppliedOffers() {
        return appliedOffers;
    }

    public void setAppliedOffers(List<String> appliedOffers) {
        this.appliedOffers = appliedOffers;
    }
}

