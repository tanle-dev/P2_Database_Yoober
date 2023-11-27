/*
 * Group members: YOUR NAMES HERE
 * Instructions: For Project 2, implement all methods in this class, and test to confirm they behave as expected when the program is run.
 */

package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import dataClasses.Account;
import dataClasses.Address;
import dataClasses.Driver;
import dataClasses.FavouriteDestination;
import dataClasses.Passenger;
import dataClasses.Ride;
import dataClasses.RideRequest;

public class DatabaseMethods {
  private Connection conn;

  public DatabaseMethods(Connection conn) {
    this.conn = conn;
  }

  /*
   * Accepts: Nothing
   * Behaviour: Retrieves information about all accounts
   * Returns: List of account objects
   */
  public ArrayList<Account> getAllAccounts() throws SQLException {
    ArrayList<Account> accounts = new ArrayList<Account>();

    // TODO: Implement
    String getAllAccSql = "SELECT * FROM accounts ac INNER JOIN addresses ad ON ac.ADDRESS_ID = ad.ID";
    PreparedStatement pStmtforGetAllAccounts = conn.prepareStatement(getAllAccSql);
    ResultSet rs = null;

    rs = pStmtforGetAllAccounts.executeQuery();

    while (rs.next()) {
      Account account = new Account(rs.getString("FIRST_NAME"), rs.getString("LAST_NAME"), rs.getString("STREET"),
          rs.getString("CITY"), rs.getString("PROVINCE"),
          rs.getString("POSTAL_CODE"), rs.getString("PHONE_NUMBER"), rs.getString("EMAIL"), rs.getString("BIRTHDATE"),
          false, false);
      accounts.add(account);
    }

    return accounts;
  }

  /*
   * Accepts: Email address of driver
   * Behaviour: Calculates the average rating over all rides performed by the
   * driver specified by the email address
   * Returns: The average rating value
   */
  public double getAverageRatingForDriver(String driverEmail) throws SQLException {
    double averageRating = 0.0;

    // TODO: Implement
    String getAvgRatingSql = "SELECT AVG(RATING_FROM_PASSENGER) FROM accounts a INNER JOIN rides r ON a.ID = r.DRIVER_ID WHERE a.EMAIL = ? GROUP BY r.DRIVER_ID";
    PreparedStatement pStmtforGetAvgRating = conn.prepareStatement(getAvgRatingSql);
    pStmtforGetAvgRating.setString(1, driverEmail);
    ResultSet rs = pStmtforGetAvgRating.executeQuery();

    while (rs.next()) {
      averageRating = rs.getDouble(1);
    }

    return averageRating;
  }

  /*
   * Accepts: Account details, and passenger and driver specific details.
   * Passenger or driver details could be
   * null if account is only intended for one type of use.
   * Behaviour:
   * - Insert new account using information provided in Account object
   * - For non-null passenger/driver details, insert the associated data into the
   * relevant tables
   * Returns: Nothing
   */
  public void createAccount(Account account, Passenger passenger, Driver driver) throws SQLException {
    // TODO: Implement
    // Hint: Use the available insertAccount, insertPassenger, and insertDriver
    // methods
  }

  /*
   * Accepts: Account details (which includes address information)
   * Behaviour: Inserts the new account, as well as the account's address if it
   * doesn't already exist. The new/existing address should
   * be linked to the account
   * Returns: Id of the new account
   */
  public int insertAccount(Account account) throws SQLException {
    int accountId = -1;

    // TODO: Implement
    // Hint: Use the insertAddressIfNotExists method

    return accountId;
  }

  /*
   * Accepts: Passenger details (should not be null), and account id for the
   * passenger
   * Behaviour: Inserts the new passenger record, correctly linked to the account
   * id
   * Returns: Id of the new passenger
   */
  public int insertPassenger(Passenger passenger, int accountId) throws SQLException {
    // TODO: Implement

    return accountId;
  }

  /*
   * Accepts: Driver details (should not be null), and account id for the driver
   * Behaviour: Inserts the new driver and driver's license record, correctly
   * linked to the account id
   * Returns: Id of the new driver
   */
  public int insertDriver(Driver driver, int accountId) throws SQLException {
    // TODO: Implement
    // Hint: Use the insertLicense method

    return accountId;
  }

  /*
   * Accepts: Driver's license number and license expiry
   * Behaviour: Inserts the new driver's license record
   * Returns: Id of the new driver's license
   */
  public int insertLicense(String licenseNumber, String licenseExpiry) throws SQLException {
    int licenseId = -1;
    // TODO: Implement
    String licenseExistsSql = "SELECT ID FROM licenses WHERE NUMBER = ?";
    String insertLicenseSql = "INSERT INTO licenses(NUMBER, EXPIRY_DATE) VALUES(?, ?)";
    PreparedStatement pStmtlicenseExists = conn.prepareStatement(licenseExistsSql);
    PreparedStatement pStmtInsertLicense = conn.prepareStatement(insertLicenseSql, Statement.RETURN_GENERATED_KEYS);

    pStmtlicenseExists.setString(1, licenseNumber);
    ResultSet rs = pStmtlicenseExists.executeQuery();

    if (rs.next()) {
      licenseId = rs.getInt(1);
    } else {
      pStmtInsertLicense.setString(1, licenseNumber);
      pStmtInsertLicense.setString(2, licenseExpiry);

      pStmtlicenseExists.setString(1, licenseNumber);
      rs = pStmtlicenseExists.executeQuery();
      while (rs.next()) {
        licenseId = rs.getInt(1);
      }
    }

    return licenseId;
  }

  /*
   * Accepts: Address details
   * Behaviour:
   * - Checks if an address with these properties already exists.
   * - If it does, gets the id of the existing address.
   * - If it does not exist, creates the address in the database, and gets the id
   * of the new address
   * Returns: Id of the address
   */
  public int insertAddressIfNotExists(Address address) throws SQLException {
    int addressId = -1;

    // TODO: Implement

    return addressId;
  }

  /*
   * Accepts: Name of new favourite destination, email address of the passenger,
   * and the id of the address being favourited
   * Behaviour: Finds the id of the passenger with the email address, then inserts
   * the new favourite destination record
   * Returns: Nothing
   */
  public void insertFavouriteDestination(String favouriteName, String passengerEmail, int addressId)
      throws SQLException {
    // TODO: Implement
    String insertFavDestSql = "INSERT INTO favourite_locations(NAME, PASSENGER_ID, LOCATION_ID) VALUES(?, ?, ?)";
    PreparedStatement pStmtInsertFavDest = conn.prepareStatement(insertFavDestSql, Statement.RETURN_GENERATED_KEYS);

    pStmtInsertFavDest.setString(1, favouriteName);
    pStmtInsertFavDest.setInt(2, this.getPassengerIdFromEmail(passengerEmail));
    pStmtInsertFavDest.setInt(3, addressId);

    pStmtInsertFavDest.executeUpdate();
  }

  /*
   * Accepts: Email address
   * Behaviour: Determines if a driver exists with the provided email address
   * Returns: True if exists, false if not
   */
  public boolean checkDriverExists(String email) throws SQLException {
    // TODO: Implement
    String checkDriverExistsSql = "SELECT COUNT(*) FROM accounts a INNER JOIN drivers d ON a.ID = d.ID WHERE a.EMAIL = ?";
    PreparedStatement pStmtCheckDriverExists = conn.prepareStatement(checkDriverExistsSql);
    pStmtCheckDriverExists.setString(1, email);
    ResultSet rs = pStmtCheckDriverExists.executeQuery();

    boolean isExistedDriver = true;
    while (rs.next()) {
      isExistedDriver = rs.getInt(1) > 0 ? true : false;
    }

    return isExistedDriver;
  }

  /*
   * Accepts: Email address
   * Behaviour: Determines if a passenger exists with the provided email address
   * Returns: True if exists, false if not
   */
  public boolean checkPassengerExists(String email) throws SQLException {
    // TODO: Implement
    String checkPaxExistsSql = "SELECT COUNT(*) FROM accounts a INNER JOIN passengers p ON a.ID = p.ID WHERE a.EMAIL = ?";
    PreparedStatement pStmtCheckPaxExists = conn.prepareStatement(checkPaxExistsSql);
    pStmtCheckPaxExists.setString(1, email);
    ResultSet rs = pStmtCheckPaxExists.executeQuery();

    boolean isExistedPax = true;
    while (rs.next()) {
      isExistedPax = rs.getInt(1) > 0 ? true : false;
    }

    return isExistedPax;
  }

  /*
   * Accepts: Email address of passenger making request, id of dropoff address,
   * requested date/time of ride, and number of passengers
   * Behaviour: Inserts a new ride request, using the provided properties
   * Returns: Nothing
   */
  public void insertRideRequest(String passengerEmail, int dropoffLocationId, String date, String time,
      int numberOfPassengers) throws SQLException {
    int passengerId = this.getPassengerIdFromEmail(passengerEmail);
    int pickupAddressId = this.getAccountAddressIdFromEmail(passengerEmail);

    // TODO: Implement
    String insertRideRqSql = "INSERT INTO ride_requests(PASSENGER_ID, PICKUP_LOCATION_ID, PICKUP_DATE, PICKUP_TIME, NUMBER_OF_RIDERS, DROPOFF_LOCATION_ID) VALUES(?,?,?,?,?,?)";
    PreparedStatement insertRideRq = conn.prepareStatement(insertRideRqSql, Statement.RETURN_GENERATED_KEYS);

    insertRideRq.setInt(1, passengerId);
    insertRideRq.setInt(2, pickupAddressId);
    insertRideRq.setString(3, date);
    insertRideRq.setString(4, time);
    insertRideRq.setInt(5, numberOfPassengers);
    insertRideRq.setInt(6, dropoffLocationId);

    insertRideRq.executeUpdate();
  }

  /*
   * Accepts: Email address
   * Behaviour: Gets id of passenger with specified email (assumes passenger
   * exists)
   * Returns: Id
   */
  public int getPassengerIdFromEmail(String passengerEmail) throws SQLException {
    int passengerId = -1;
    // TODO: Implement
    String getPaxIdFromEmailSql = "SELECT p.ID FROM accounts a INNER JOIN passengers p ON a.ID = p.ID WHERE a.EMAIL = ?";
    PreparedStatement pStmtgetPaxIdFromEmail = conn.prepareStatement(getPaxIdFromEmailSql);
    pStmtgetPaxIdFromEmail.setString(1, passengerEmail);
    ResultSet rs = pStmtgetPaxIdFromEmail.executeQuery();

    while (rs.next()) {
      passengerId = rs.getInt(1);
    }

    return passengerId;
  }

  /*
   * Accepts: Email address
   * Behaviour: Gets id of driver with specified email (assumes driver exists)
   * Returns: Id
   */
  public int getDriverIdFromEmail(String driverEmail) throws SQLException {
    int driverId = -1;
    // TODO: Implement
    String getDriverIdFromEmailSql = "SELECT d.ID FROM accounts a INNER JOIN drivers d ON a.ID = d.ID WHERE a.EMAIL = ?";
    PreparedStatement pStmtgetDriverIdFromEmail = conn.prepareStatement(getDriverIdFromEmailSql);
    pStmtgetDriverIdFromEmail.setString(1, driverEmail);
    ResultSet rs = pStmtgetDriverIdFromEmail.executeQuery();

    while (rs.next()) {
      driverId = rs.getInt(1);
    }

    return driverId;
  }

  /*
   * Accepts: Email address
   * Behaviour: Gets the id of the address tied to the account with the provided
   * email address
   * Returns: Address id
   */
  public int getAccountAddressIdFromEmail(String email) throws SQLException {
    int addressId = -1;
    // TODO: Implement
    String getAddIdFromEmailSql = "SELECT a.ADDRESS_ID FROM accounts a WHERE a.EMAIL = ?";
    PreparedStatement pStmtgetAddIdFromEmail = conn.prepareStatement(getAddIdFromEmailSql);
    pStmtgetAddIdFromEmail.setString(1, email);
    ResultSet rs = pStmtgetAddIdFromEmail.executeQuery();

    while (rs.next()) {
      addressId = rs.getInt(1);
    }

    return addressId;
  }

  /*
   * Accepts: Email address of passenger
   * Behaviour: Gets a list of all the specified passenger's favourite
   * destinations
   * Returns: List of favourite destinations
   */
  public ArrayList<FavouriteDestination> getFavouriteDestinationsForPassenger(String passengerEmail)
      throws SQLException {
    ArrayList<FavouriteDestination> favouriteDestinations = new ArrayList<FavouriteDestination>();

    // TODO: Implement
    String getFavDestForPaxSql = "SELECT f.NAME, ad.ID, ad.STREET, ad.CITY, ad.PROVINCE, ad.POSTAL_CODE FROM accounts a INNER JOIN passengers p ON a.ID = p.ID INNER JOIN favourite_locations f ON p.ID = f.PASSENGER_ID INNER JOIN addresses ad ON f.LOCATION_ID = ad.ID WHERE a.EMAIL = ?";
    PreparedStatement pStmtgetFavDestForPax = conn.prepareStatement(getFavDestForPaxSql);
    pStmtgetFavDestForPax.setString(1, passengerEmail);
    ResultSet rs = pStmtgetFavDestForPax.executeQuery();

    while (rs.next()) {
      FavouriteDestination favDest = new FavouriteDestination(rs.getString("NAME"), rs.getInt("ID"),
          rs.getString("STREET"), rs.getString("CITY"), rs.getString("PROVINCE"), rs.getString("POSTAL_CODE"));

      favouriteDestinations.add(favDest);
    }

    return favouriteDestinations;
  }

  /*
   * Accepts: Nothing
   * Behaviour: Gets a list of all uncompleted ride requests (i.e. requests
   * without an associated ride record)
   * Returns: List of all uncompleted rides
   */
  public ArrayList<RideRequest> getUncompletedRideRequests() throws SQLException {
    ArrayList<RideRequest> uncompletedRideRequests = new ArrayList<RideRequest>();

    // TODO: Implement
    String getUncompltedRideReqSql = "SELECT rrq.ID, a.FIRST_NAME, a. LAST_NAME, ad_p.STREET AS PICK_UP_STREET, ad_p.CITY AS PICK_UP_CITY, ad_d.STREET AS DROP_OFF_STREET, ad_d.CITY AS DROP_OFF_CITY, rrq.PICKUP_DATE, rrq.PICKUP_TIME FROM ride_requests rrq LEFT JOIN rides r ON rrq.ID = r.REQUEST_ID INNER JOIN accounts a ON rrq.PASSENGER_ID = a.ID INNER JOIN addresses ad_p ON rrq.PICKUP_LOCATION_ID = ad_p.ID INNER JOIN addresses ad_d ON rrq.DROPOFF_LOCATION_ID = ad_d.ID WHERE r.ID IS NULL";
    PreparedStatement pStmtGetUncompltedRideReq = conn.prepareStatement(getUncompltedRideReqSql);
    ResultSet rs = pStmtGetUncompltedRideReq.executeQuery();

    while (rs.next()) {
      RideRequest rideRequest = new RideRequest(rs.getInt("ID"), rs.getString("FIRST_NAME"), rs.getString("LAST_NAME"),
          rs.getString("PICK_UP_STREET"), rs.getString("PICK_UP_CITY"), rs.getString("DROP_OFF_STREET"),
          rs.getString("DROP_OFF_CITY"), rs.getString("PICKUP_DATE"), rs.getString("PICKUP_TIME"));

      uncompletedRideRequests.add(rideRequest);
    }

    return uncompletedRideRequests;
  }

  /*
   * Accepts: Ride details
   * Behaviour: Inserts a new ride record
   * Returns: Nothing
   */
  public void insertRide(Ride ride) throws SQLException {
    // TODO: Implement
    // Hint: Use getDriverIdFromEmail
    String rideRqExistSql = "SELECT ID FROM rides WHERE REQUEST_ID = ?";
    String insertRide = "INSERT INTO rides(DRIVER_ID, REQUEST_ID, ACTUAL_START_DATE, ACTUAL_START_TIME, ACTUAL_END_DATE, ACTUAL_END_TIME, RATING_FROM_DRIVER, RATING_FROM_PASSENGER, DISTANCE, CHARGE) VALUES (?,?,?,?,?,?,?,?,?,?)";

    // Check ride exists firstly
    PreparedStatement pStmtRideRqExist = conn.prepareStatement(rideRqExistSql);
    PreparedStatement pStmtInsertRide = conn.prepareStatement(insertRide, Statement.RETURN_GENERATED_KEYS);
    pStmtRideRqExist.setInt(1, ride.getRideRequestId());
    ResultSet rs = pStmtRideRqExist.executeQuery();

    if (rs.next()) {

    } else {
      pStmtInsertRide.setInt(1, this.getDriverIdFromEmail(ride.getDriverEmail()));
      pStmtInsertRide.setInt(2, ride.getRideRequestId());
      pStmtInsertRide.setString(3, ride.getStartDate());
      pStmtInsertRide.setString(4, ride.getStartTime());
      pStmtInsertRide.setString(5, ride.getEndDate());
      pStmtInsertRide.setString(6, ride.getEndTime());
      pStmtInsertRide.setInt(7, ride.getRatingFromDriver());
      pStmtInsertRide.setInt(8, ride.getRatingFromPassenger());
      pStmtInsertRide.setDouble(9, ride.getDistance());
      pStmtInsertRide.setDouble(10, ride.getCharge());

      pStmtInsertRide.executeUpdate();
    }
  }

}
