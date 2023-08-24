# Tool Rental Generator
### Created by Adam Winters (2023)

This application is a Rental Agreement generator based off of the Rental Request provided to the `RentalService.checkout()` function


By providing the following data within the `RentalRequest` object:
```
    String toolCode;
    int numDaysToRent;
    int discountPercent;
    Date checkoutDate;
```

The `toolCode` string is always 4 characters long. The first 3 characters are associated
with the type of tool being rented (ex "CHS" = "CHAINSAW")
The last character is the Brand (ex "S" = "STIHL")

Each tool type has a unique daily charge amount, and some are charged on weekends 
& holidays while others are not.

The `numDaysToRent` must be at least one day

The `discountPercent` is a whole number between 0 & 100

The `checkoutDate` is the beginning of the rental.

See `RentalService.validateRentalRequest` for all data requirements

A `RentalAgreement` will be returned with the following fields:

```
    Tool tool; // see Tool.java for definition
    int numDaysRented;
    Date checkOutDate;
    Date dueDate;
    BigDecimal dailyRentalCharge;
    int chargeDays;
    int discountDays;
    BigDecimal preDiscountCharge;
    int discountPercent;
    BigDecimal discountAmount;
    BigDecimal finalCharge;
```

The field `chargeDays` is NOT the same as `numDaysToRent`. The number of days charged does not begin 
until the first day **AFTER** the `checkoutDate`, and will default to a minimum of one charge
day if holidays / weekends / weekday charges would have calculated out to zero otherwise 
(Ex: rental is checked out on July 3rd and returned on July 4th and the tool does not charge for Holidays)

More detail on each field can be found within `RentalAgreement.java`


