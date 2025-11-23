package fpt.edu.vn.gms.utils;

public class NumberToVietnameseWords {
    private static final String[] chuSo = { "không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín" };
    private static final String[] hang = { "", "nghìn", "triệu", "tỷ" };

    private static String docSo3ChuSo(int number) {
        int tram = number / 100;
        int chuc = (number % 100) / 10;
        int donvi = number % 10;
        StringBuilder result = new StringBuilder();

        if (tram > 0) {
            result.append(chuSo[tram]).append(" trăm");
            if (chuc == 0 && donvi > 0) {
                result.append(" linh");
            }
        }

        if (chuc > 1) {
            result.append(" ").append(chuSo[chuc]).append(" mươi");
            if (donvi == 1)
                result.append(" mốt");
            else if (donvi == 5)
                result.append(" lăm");
            else if (donvi > 0)
                result.append(" ").append(chuSo[donvi]);
        } else if (chuc == 1) {
            result.append(" mười");
            if (donvi == 1)
                result.append(" một");
            else if (donvi == 5)
                result.append(" lăm");
            else if (donvi > 0)
                result.append(" ").append(chuSo[donvi]);
        } else if (chuc == 0 && donvi > 0 && tram > 0) {
            result.append(" ").append(chuSo[donvi]);
        } else if (tram == 0 && chuc == 0 && donvi > 0) {
            result.append(chuSo[donvi]);
        }

        return result.toString();
    }

    public static String convert(long number) {
        if (number == 0) {
            return "Không đồng";
        }

        StringBuilder result = new StringBuilder();
        int i = 0;

        while (number > 0) {
            int part = (int) (number % 1000);
            if (part > 0) {
                String s = docSo3ChuSo(part);
                result.insert(0, s + " " + hang[i] + " ");
            }
            number /= 1000;
            i++;
        }

        String text = result.toString().trim();
        text = text.substring(0, 1).toUpperCase() + text.substring(1);

        return text + " đồng";
    }
}

