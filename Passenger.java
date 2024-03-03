public class Passenger {
    private String bookingReference;
    private String name;
    private String flightCode;
    private boolean checkedIn;
    private double baggageWeight;
    private double baggageVolume;

   // Constructor
   public Passenger(String bookingReference, String name, String flightCode) {
       this.bookingReference = bookingReference;
       this.name = name;
       this.flightCode = flightCode;
       this.checkedIn = false; // Default value
   }

   // Check-in method
   public void checkIn(double baggageWeight, double baggageVolume) {
       this.checkedIn = true;
       this.baggageWeight = baggageWeight;
       this.baggageVolume = baggageVolume;
       // Here you could calculate and set any excess baggage fees, if necessary
   }

   // Getter and Setter methods
   // For example:
   public String getName() {
       return name;
   }

   // Include other getters and setters as needed

   // You might also want to override the toString() method for easier debugging
   @Override
   public String toString() {
       return "Passenger{" +
              "bookingReference='" + bookingReference + '\'' +
              ", name='" + name + '\'' +
              ", flightCode='" + flightCode + '\'' +
              ", checkedIn=" + checkedIn +
              ", baggageWeight=" + baggageWeight +
              ", baggageVolume=" + baggageVolume +
              '}';
   }
}
