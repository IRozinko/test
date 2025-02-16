package fintech.crm.logins;


import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class PasswordHash {

	@SuppressWarnings("serial")
	static public class InvalidHashException extends RuntimeException {
		public InvalidHashException(String message) {
			super(message);
		}

		public InvalidHashException(String message, Throwable source) {
			super(message, source);
		}
	}

	@SuppressWarnings("serial")
	static public class CannotPerformOperationException extends RuntimeException {
		public CannotPerformOperationException(String message) {
			super(message);
		}

		public CannotPerformOperationException(String message, Throwable source) {
			super(message, source);
		}
	}

	private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";

	// These constants may be changed without breaking existing hashes.
	private static final int SALT_BYTE_SIZE = 24;
	private static final int HASH_BYTE_SIZE = 18;
	private static final int PBKDF2_ITERATIONS = 64000;

	// These constants define the encoding and may not be changed.
	private static final int HASH_SECTIONS = 5;
	private static final int HASH_ALGORITHM_INDEX = 0;
	private static final int ITERATION_INDEX = 1;
	private static final int HASH_SIZE_INDEX = 2;
	private static final int SALT_INDEX = 3;
	private static final int PBKDF2_INDEX = 4;

	public static String createHash(String password)
			throws CannotPerformOperationException {
		return createHash(password.toCharArray());
	}

	private static String createHash(char[] password)
			throws CannotPerformOperationException {
		// Generate a random salt
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[SALT_BYTE_SIZE];
		random.nextBytes(salt);

		// Hash the password
		byte[] hash = pbkdf2(password, salt, PBKDF2_ITERATIONS, HASH_BYTE_SIZE);
		int hashSize = hash.length;

		// format: algorithm:iterations:hashSize:salt:hash
		return "sha1:" + PBKDF2_ITERATIONS + ":" + hashSize + ":" + toBase64(salt) + ":" + toBase64(hash);
	}

	public static boolean verifyPassword(String password, String correctHash) throws CannotPerformOperationException, InvalidHashException {
		return verifyPassword(password.toCharArray(), correctHash);
	}

	private static boolean verifyPassword(char[] password, String correctHash) throws CannotPerformOperationException, InvalidHashException {
		// Decode the hash into its parameters
		String[] params = correctHash.split(":");
		if (params.length != HASH_SECTIONS) {
			throw new InvalidHashException("Fields are missing from the password hash.");
		}

		// Currently, Java only supports SHA1.
		if (!params[HASH_ALGORITHM_INDEX].equals("sha1")) {
			throw new CannotPerformOperationException("Unsupported hash type.");
		}

		int iterations = 0;
		try {
			iterations = Integer.parseInt(params[ITERATION_INDEX]);
		} catch (NumberFormatException ex) {
            throw new InvalidHashException("Could not parse the iteration size as an integer.", ex);
        }

		if (iterations < 1) {
			throw new InvalidHashException("Invalid number of iterations. Must be >= 1.");
		}

		byte[] salt = null;
		try {
			salt = fromBase64(params[SALT_INDEX]);
		} catch (IllegalArgumentException ex) {
			throw new InvalidHashException("Base64 decoding of salt failed.", ex);
		}

		byte[] hash = null;
		try {
			hash = fromBase64(params[PBKDF2_INDEX]);
		} catch (IllegalArgumentException ex) {
			throw new InvalidHashException("Base64 decoding of pbkdf2 output failed.", ex);
		}


		int storedHashSize = 0;
		try {
			storedHashSize = Integer.parseInt(params[HASH_SIZE_INDEX]);
		} catch (NumberFormatException ex) {
			throw new InvalidHashException("Could not parse the hash size as an integer.", ex);
		}

		if (storedHashSize != hash.length) {
			throw new InvalidHashException("Hash length doesn't match stored hash length.");
		}

		// Compute the hash of the provided password, using the same salt,
        // iteration size, and hash length
        byte[] testHash = pbkdf2(password, salt, iterations, hash.length);
		// Compare the hashes in constant time. The password is correct if
		// both hashes match.
		return slowEquals(hash, testHash);
	}

	public static boolean slowEquals(byte[] a, byte[] b) {
		int diff = a.length ^ b.length;
		for (int i = 0; i < a.length && i < b.length; i++)
			diff |= a[i] ^ b[i];
		return diff == 0;
	}

	private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes)
			throws CannotPerformOperationException {
		try {
			PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
			SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
			return skf.generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException ex) {
			throw new CannotPerformOperationException("Hash algorithm not supported.", ex);
		} catch (InvalidKeySpecException ex) {
			throw new CannotPerformOperationException("Invalid key spec.", ex);
		}
	}

	private static byte[] fromBase64(String hex)
			throws IllegalArgumentException {
		return DatatypeConverter.parseBase64Binary(hex);
	}

	private static String toBase64(byte[] array) {
		return DatatypeConverter.printBase64Binary(array);
	}

}
