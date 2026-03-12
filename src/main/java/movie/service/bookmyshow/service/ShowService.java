package movie.service.bookmyshow.service;

import movie.service.bookmyshow.model.Show;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShowService {

    private final Map<String, Show> shows = new HashMap<>();

    public Show createShow(Show show) {
        String id = UUID.randomUUID().toString();
        show.setId(id);
        shows.put(id, show);
        return show;
    }

    public Show updateShow(String id, Show updated) {
        Show existing = getShow(id);
        existing.setTheatreName(updated.getTheatreName());
        existing.setCity(updated.getCity());
        existing.setMovieTitle(updated.getMovieTitle());
        existing.setShowDate(updated.getShowDate());
        existing.setShowTime(updated.getShowTime());
        existing.setTicketPrice(updated.getTicketPrice());
        return existing;
    }

    public void deleteShow(String id) {
        shows.remove(id);
    }

    public Show getShow(String id) {
        Show show = shows.get(id);
        if (show == null) {
            throw new NoSuchElementException("Show not found: " + id);
        }
        return show;
    }

    public List<Show> findShows(String movieTitle, String city, LocalDate date) {
        return shows.values().stream()
                .filter(s -> movieTitle == null || s.getMovieTitle().equalsIgnoreCase(movieTitle))
                .filter(s -> city == null || s.getCity().equalsIgnoreCase(city))
                .filter(s -> date == null || s.getShowDate().equals(date))
                .collect(Collectors.toList());
    }

    public Show allocateSeats(String showId, Set<String> seatIds) {
        Show show = getShow(showId);
        show.setSeatInventory(new HashSet<>(seatIds));
        // Remove any booked seats that are no longer in inventory
        show.getBookedSeats().retainAll(seatIds);
        return show;
    }

    public void releaseSeats(String showId, Collection<String> seats) {
        Show show = getShow(showId);
        show.getBookedSeats().removeAll(seats);
    }

    public void reserveSeats(String showId, Collection<String> seats) {
        Show show = getShow(showId);
        if (!show.getSeatInventory().containsAll(seats)) {
            throw new IllegalArgumentException("Some seats are not in inventory");
        }
        Set<String> intersection = new HashSet<>(show.getBookedSeats());
        intersection.retainAll(seats);
        if (!intersection.isEmpty()) {
            throw new IllegalArgumentException("Some seats are already booked: " + intersection);
        }
        show.getBookedSeats().addAll(seats);
    }
}

