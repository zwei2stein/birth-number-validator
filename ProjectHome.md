Simple utility class for validating czech and slovak birth number in java.

Jednoduchá utilitní třída pro validaci českého a slovenského rodného čísla v javě.

Checked items are:
  * Checksum
  * Validity of date
  * Sex
  * Date versus stated birth date
  * Basics (null, containing only numbers, etc...)

This class is unsuitable for validating insuarance number (číslo pojištěnce), which has slightly different format for foreigners where day of birth is offset by 50.

This class will also not work properly for birthNumbers issued after 2054.

This class may be used to validate slovak birth number (sloveské rodné číslo) which has identical format.

Simple use:

```
	boolean isValid = BirthNumberValidator.validateBirthNumber("8002104946");
```

Advanced use:

```
	boolean isValid = false;
	try {
		isValid = BirthNumberValidator.validateBirthNumberHarshly("8002104946", null, null);
	} catch (BirthNumberValidationException e) {
		// oh no!
	}
```