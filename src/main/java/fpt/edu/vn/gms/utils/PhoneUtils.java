package fpt.edu.vn.gms.utils;

public class PhoneUtils {

    public static String validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return "Số điện thoại không được để trống.";
        }

        // Remove all white spaces
        phoneNumber = phoneNumber.replaceAll("\\s+", "");

        // Check if all characters are digits
        if (!phoneNumber.matches("\\d+")) {
            return "Số điện thoại chỉ được chứa chữ số.";
        }

        // Check length for mobile and landline numbers
        if (phoneNumber.length() != 10 && phoneNumber.length() != 11) {
            return "Số điện thoại phải có độ dài 10 hoặc 11 chữ số.";
        }

        // Check valid format for mobile phone numbers
        if (phoneNumber.matches("^(0(3[2-9]|5[2689]|7[06-9]|8[1-689]|9[0-9])[0-9]{7})$")) {
            return null;
        }

        // Check valid format for landline numbers with 10 digits
        if (phoneNumber.matches("^(02[0-9]{9})$")) {
            return null;
        }

        // Check valid format for landline numbers with 9 digits
        if (phoneNumber.matches("^(02[0-9]{8})$")) {
            return null;
        }

        return "Số điện thoại không hợp lệ.";
    }

    public static String normalize(String phone) {
        if (phone == null || phone.isBlank()) return phone;

        if (phone.startsWith("0")) {
            return "84" + phone.substring(1);
        }

        if (phone.startsWith("84")) {
            return phone;
        }

        throw new IllegalArgumentException("Số điện thoại không hợp lệ!");
    }
}

