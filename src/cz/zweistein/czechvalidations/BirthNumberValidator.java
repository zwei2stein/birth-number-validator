//Copyright (c) 2013, Petr Prokop
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions are met: 
//
//1. Redistributions of source code must retain the above copyright notice, this
//   list of conditions and the following disclaimer. 
//2. Redistributions in binary form must reproduce the above copyright notice,
//   this list of conditions and the following disclaimer in the documentation
//   and/or other materials provided with the distribution. 
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
//ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
//DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
//ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
//(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
//LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
//ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
//(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
//SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
//The views and conclusions contained in the software and documentation are those
//of the authors and should not be interpreted as representing official policies, 
//either expressed or implied, of the FreeBSD Project.
package cz.zweistein.czechvalidations;

import java.util.Calendar;
import java.util.Date;

/**
 * 
 * This is validator of czech birth number (èeské rodné èíslo).
 * 
 * Checked items are:
 *  - Checksum
 *  - Validity of date
 *  - Sex
 *  - Date versus stated birth date
 *  - Basics (null, containing only numbers, etc...)
 *  
 * This class is unsuitable for validating insuarance number (èíslo pojištìnce),
 * which has slightly different format for foreigners where day of birth is offset by 50.
 * 
 * This class will also not work properly for birthNumbers issued after 2054.
 * 
 * This class may be used to validate slovak birth number (sloveské rodné èíslo)
 * which has identical format.
 * 
 * Example use:
 * 
 * boolean isValid = BirthNumberValidator.validateBirthNumber("8002104946");
 * 
 * Advanced use:
 * 
 *	boolean isValid = false;
 *	try {
 *		isValid = BirthNumberValidator.validateBirthNumberHarshly("8002104946", null, null);
 *	} catch (BirthNumberValidationException e) {
 *		// oh no!
 *	}
 * 
 * @author Petr Prokop <zwei2stein@gmail.com>
 * @version 1.1
 * @license FreeBSD License
 *
 */
public class BirthNumberValidator {
	
	/**
	 * Utility method, see public boolean validateBirthNumber(String birthNumber, Date birthDate, Sex sex)
	 * 
	 * @param birthNumber
	 * @return
	 */
	public static boolean validateBirthNumber(String birthNumber) {
		return validateBirthNumber(birthNumber, null, Sex.UNSPECIFICED);
	}
	
	/**
	 * Utility method, see public boolean validateBirthNumber(String birthNumber, Date birthDate, Sex sex)
	 * 
	 * @param birthNumber
	 * @param birthDate
	 * @return
	 */
	public static boolean validateBirthNumber(String birthNumber, Date birthDate) {
		return validateBirthNumber(birthNumber, birthDate, Sex.UNSPECIFICED);
	}
	
	/**
	 * Utility method, see public boolean validateBirthNumber(String birthNumber, Date birthDate, Sex sex)
	 * 
	 * @param birthNumber
	 * @param sex
	 * @return
	 */
	public static boolean validateBirthNumber(String birthNumber, Sex sex) {
		return validateBirthNumber(birthNumber, null, sex);
	}
	
	/**
	 * Validates czech birth number (rodné èíslo).
	 * 
	 * Checked items are:
	 *  - Checksum
	 *  - Validity of date
	 *  - Sex
	 *  - Date versus stated birth date
	 *  - Basics (null, containing only numbers, etc...)
	 * 
	 * @param birthNumber - birth number in YYMMDDCCCC format without slash or dash
	 * @param birthDate - date of birth of person whose birth nuber we are validating, null if not available
	 * @param sex - sex of person whose birth we are validating, null or Sex.UNSPECIFIED if not available.
	 * @return true if birth number is validù
	 */
	public static boolean validateBirthNumber(String birthNumber, Date birthDate, Sex sex) {
		try {
			return validateBirthNumberHarshly(birthNumber, birthDate, sex);
		} catch (BirthNumberValidationException e) {
			return false;
		}
	}
	
	/**
	 * See public static boolean validateBirthNumber(String birthNumber, Date birthDate, Sex sex) for paramters
	 * 
	 * This method returns true if birth number is valid but throws exception if it is invalid
	 * 
	 * @param birthNumber
	 * @param birthDate
	 * @param sex
	 * @return
	 * @throws BirthNumberValidationException
	 */
	public static boolean validateBirthNumberHarshly(String birthNumber, Date birthDate, Sex sex) throws BirthNumberValidationException {

		if (birthNumber == null) {
			throw new BirthNumberValidationException("Input parameter is null.", BirthNumberValidityError.NULL_PARAM);
		} else if (birthNumber.length() != 10 && birthNumber.length() != 9) {
			throw new BirthNumberValidationException("Input parameter is not 9 or 10 characters long.", BirthNumberValidityError.INVALID_LENGTH);
		} else if (!birthNumber.matches("[0-9]+")) {
			throw new BirthNumberValidationException("Input parameter does not contain only numbers.", BirthNumberValidityError.NONNUMERIC_CHARACTER);
		} else {

			int year = Integer.parseInt(birthNumber.substring(0, 2), 10);
			int month = Integer.parseInt(birthNumber.substring(2, 4), 10);
			int day = Integer.parseInt(birthNumber.substring(4, 6), 10);
			int ext = Integer.parseInt(birthNumber.substring(6, 9), 10);

			if (birthNumber.length() == 9 && ext == 0) {
				throw new BirthNumberValidationException("Nine digit birth number suffix is 000 ", BirthNumberValidityError.NINE_DIGITS_000_SUFFIX);
			}
			
			if (birthNumber.length() == 10) {
				int checkDigit = Integer.parseInt(birthNumber.substring(9), 10);
				int mod11 = Integer.parseInt(birthNumber.substring(0, 9), 10) % 11;
	
				if (mod11 == 10) {
					mod11 = 0;
				}
				if (mod11 != checkDigit) {
					throw new BirthNumberValidationException("Mod 11 checksum not matching.", BirthNumberValidityError.MOD_11_CHECKSUM_FAILUE);
				}
			}

			// restoring year of birth.
			if (year < 54 && birthNumber.length() == 10) {
				year = 2000 + year;
			} else if (birthNumber.length() == 10) {
				year = 1900 + year;
			} else {
				year = 1800 + year;
			}

			// month > 50 means that birth number belongs to female
			if (month > 50 && Sex.MALE.equals(sex)) {
				throw new BirthNumberValidationException("Supplied sex not matching.", BirthNumberValidityError.SEX_MISMATCH);
			} else if (month < 50 && Sex.FEMALE.equals(sex)) {
				throw new BirthNumberValidationException("Supplied sex not matching.", BirthNumberValidityError.SEX_MISMATCH);
			}

			// removing sex offsets from month of birth.
			if (month > 70 && year > 2003) {
				month -= 70;
			} else if (month > 50) {
				month -= 50;
			} else if (month > 20 && year > 2003) {
				month -= 20;
			}

			Calendar cal = Calendar.getInstance();
			cal.setLenient(false); // makes calendar throw exception on invalid date
			try {
				cal.set(year, month - 1, day); // month starts at 0
				Date date = cal.getTime(); //you need to call get time to make calendar validate date and throw exception

				if (birthDate != null) {
					if (!birthDate.equals(date)) {
						throw new BirthNumberValidationException("Supplied birth date not matching.", BirthNumberValidityError.BIRTH_DATE_MISMATCH);
					}
				}

			} catch (IllegalArgumentException e) {
				throw new BirthNumberValidationException("Invalid birth date.", BirthNumberValidityError.INVALID_DATE);
			}
			
		}

		return true;

	}

}
