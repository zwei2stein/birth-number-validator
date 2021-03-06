package cz.zweistein.czechvalidations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Calendar;

import org.junit.Test;

/**
 *
 * Test suite for BirthNumberValidator
 *
 * @author Petr Prokop
 *
 */
public class BirthNumberValidatorTest {

	// @Test
	// public void performance() {
	//
	// for (int i = 0; i < 166666; i++) { // 1 mil tests
	// assertEquals(false,
	// BirthNumberValidator.validateBirthNumber("7401040820"));
	// assertEquals(true,
	// BirthNumberValidator.validateBirthNumber("7801233540"));
	// assertEquals(false,
	// BirthNumberValidator.validateBirthNumber("9107036658"));
	// assertEquals(true,
	// BirthNumberValidator.validateBirthNumber("8002104946"));
	// assertEquals(false,
	// BirthNumberValidator.validateBirthNumber("68292612"));
	// assertEquals(true,
	// BirthNumberValidator.validateBirthNumber("0531245099"));
	// }
	//
	// }

	@Test
	public void nullOrEmptyParam() {
		assertEquals(false, BirthNumberValidator.validateBirthNumber(null));
		assertEquals(false, BirthNumberValidator.validateBirthNumber(""));

		try {
			BirthNumberValidator.validateBirthNumberHarshly(null, null, null);
			fail();
		} catch (BirthNumberValidationException e) {
			assertEquals(BirthNumberValidityError.NULL_PARAM, e.getErrorCode());
		}

		try {
			BirthNumberValidator.validateBirthNumberHarshly("", null, null);
			fail();
		} catch (BirthNumberValidationException e) {
			assertEquals(BirthNumberValidityError.INVALID_LENGTH, e.getErrorCode());
		}

	}

	@Test
	public void wrongLength() {
		assertEquals(false, BirthNumberValidator.validateBirthNumber("12345678"));
		assertEquals(false, BirthNumberValidator.validateBirthNumber("12345678910"));

		try {
			BirthNumberValidator.validateBirthNumberHarshly("12345678", null, null);
			fail();
		} catch (BirthNumberValidationException e) {
			assertEquals(BirthNumberValidityError.INVALID_LENGTH, e.getErrorCode());
		}

		try {
			BirthNumberValidator.validateBirthNumberHarshly("12345678910", null, null);
			fail();
		} catch (BirthNumberValidationException e) {
			assertEquals(BirthNumberValidityError.INVALID_LENGTH, e.getErrorCode());
		}
	}

	@Test
	public void unsupportedCharacters() {
		assertEquals(false, BirthNumberValidator.validateBirthNumber("123456/708"));
		assertEquals(false, BirthNumberValidator.validateBirthNumber("ABCDERFGHI"));
		assertEquals(false, BirthNumberValidator.validateBirthNumber("12356-7890"));

		try {
			BirthNumberValidator.validateBirthNumberHarshly("123456/708", null, null);
			fail();
		} catch (BirthNumberValidationException e) {
			assertEquals(BirthNumberValidityError.NONNUMERIC_CHARACTER, e.getErrorCode());
		}

		try {
			BirthNumberValidator.validateBirthNumberHarshly("ABCDERFGHI", null, null);
			fail();
		} catch (BirthNumberValidationException e) {
			assertEquals(BirthNumberValidityError.NONNUMERIC_CHARACTER, e.getErrorCode());
		}

		try {
			BirthNumberValidator.validateBirthNumberHarshly("12386-7890", null, null);
			fail();
		} catch (BirthNumberValidationException e) {
			assertEquals(BirthNumberValidityError.NONNUMERIC_CHARACTER, e.getErrorCode());
		}

	}

	@Test
	public void nineDigitNumber() {
		assertEquals(true, BirthNumberValidator.validateBirthNumber("130217789"));
		assertEquals(true, BirthNumberValidator.validateBirthNumber("505101250"));
		assertEquals(false, BirthNumberValidator.validateBirthNumber("923456789"));

		try {
			BirthNumberValidator.validateBirthNumberHarshly("923456789", null, null);
			fail();
		} catch (BirthNumberValidationException e) {
			assertEquals(BirthNumberValidityError.INVALID_DATE, e.getErrorCode());
		}
	}

	@Test
	public void checksumFailure() {
		assertEquals(false, BirthNumberValidator.validateBirthNumber("1234567890"));

		try {
			BirthNumberValidator.validateBirthNumberHarshly("1234567890", null, null);
			fail();
		} catch (BirthNumberValidationException e) {
			assertEquals(BirthNumberValidityError.MOD_11_CHECKSUM_FAILUE, e.getErrorCode());
		}
	}

	@Test
	public void sexDetection() {
		assertEquals(true, BirthNumberValidator.validateBirthNumber("8911020019", Sex.MALE));
		assertEquals(false, BirthNumberValidator.validateBirthNumber("8911020019", Sex.FEMALE));
		try {
			BirthNumberValidator.validateBirthNumberHarshly("8911020019", null, Sex.FEMALE);
			fail();
		} catch (BirthNumberValidationException e) {
			assertEquals(BirthNumberValidityError.SEX_MISMATCH, e.getErrorCode());
		}
		assertEquals(false, BirthNumberValidator.validateBirthNumber("8961020013", Sex.MALE));
		try {
			BirthNumberValidator.validateBirthNumberHarshly("8961020013", null, Sex.MALE);
			fail();
		} catch (BirthNumberValidationException e) {
			assertEquals(BirthNumberValidityError.SEX_MISMATCH, e.getErrorCode());
		}
		assertEquals(true, BirthNumberValidator.validateBirthNumber("8961020013", Sex.FEMALE));

		assertEquals(true, BirthNumberValidator.validateBirthNumber("8911020019", null, null));
	}

	@Test
	public void wrongDate() {
		assertEquals(false, BirthNumberValidator.validateBirthNumber("1234567895"));
		assertEquals(false, BirthNumberValidator.validateBirthNumber("0000000000"));
		assertEquals(false, BirthNumberValidator.validateBirthNumber("8975120011"));

		try {
			BirthNumberValidator.validateBirthNumberHarshly("1234567895", null, null);
			fail();
		} catch (BirthNumberValidationException e) {
			assertEquals(BirthNumberValidityError.INVALID_DATE, e.getErrorCode());
		}
	}

	@Test
	public void matchingDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(1989, 11 - 1, 2, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		assertEquals(true, BirthNumberValidator.validateBirthNumber("8911020019", cal.getTime()));
		cal.set(1972, 11 - 1, 2, 0, 0);
		assertEquals(false, BirthNumberValidator.validateBirthNumber("8911020019", cal.getTime()));
		cal.set(1950, 1 - 1, 1, 0, 0);
		assertEquals(true, BirthNumberValidator.validateBirthNumber("505101250", cal.getTime()));
		cal.set(1850, 1 - 1, 1, 0, 0);
		assertEquals(false, BirthNumberValidator.validateBirthNumber("505101250", cal.getTime()));
		try {
			cal.set(1972, 11 - 1, 2, 0, 0);
			BirthNumberValidator.validateBirthNumberHarshly("8911020019", cal.getTime(), null);
			fail();
		} catch (BirthNumberValidationException e) {
			assertEquals(BirthNumberValidityError.BIRTH_DATE_MISMATCH, e.getErrorCode());
		}
	}

	@Test
	public void exampleBirthNumbers() {
		assertEquals(true, BirthNumberValidator.validateBirthNumber("7401040020"));
		assertEquals(true, BirthNumberValidator.validateBirthNumber("7801233540"));
		assertEquals(false, BirthNumberValidator.validateBirthNumber("9107036658"));
		assertEquals(true, BirthNumberValidator.validateBirthNumber("8002104946"));
		assertEquals(true, BirthNumberValidator.validateBirthNumber("280715152"));
		assertEquals(true, BirthNumberValidator.validateBirthNumber("0531135099"));
		assertEquals(true, BirthNumberValidator.validateBirthNumber("0681186066"));

		try {
			assertEquals(true, BirthNumberValidator.validateBirthNumberHarshly("1111111111", null, null));
		} catch (BirthNumberValidationException e) {
			fail();
		}
	}

	@Test
	public void testNineDigitSuffix() {
		assertEquals(true, BirthNumberValidator.validateBirthNumber("130217789"));
		assertEquals(false, BirthNumberValidator.validateBirthNumber("123456000"));
	}

	@Test
	public void testYearDetection() {
		Calendar cal = Calendar.getInstance();
		cal.set(1989, 11 - 1, 2, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		assertEquals(true, BirthNumberValidator.validateBirthNumber("8911020019", cal.getTime()));

		cal.set(2021, 11 - 1, 2, 0, 0);
		assertEquals(true, BirthNumberValidator.validateBirthNumber("2111020010", cal.getTime()));

		cal.set(1889, 11 - 1, 2, 0, 0);
		assertEquals(true, BirthNumberValidator.validateBirthNumber("891102001", cal.getTime()));

	}

}
