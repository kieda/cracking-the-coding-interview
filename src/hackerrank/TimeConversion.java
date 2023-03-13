package hackerrank;

public class TimeConversion {
    public static String timeConversion(String s) {
        String exceptionMessage = s + " time is not in a valid 12 hour format";
        if(s.length() < 5)
            throw new RuntimeException(exceptionMessage);

        // accept inputs of 07, 7, etc
        boolean foundHour = false;
        int startHour = 0;
        int endHour = 0;
        for(int i = 0; i < s.length(); i++) {
            char current = s.charAt(i);
            if(current == '0' && !foundHour) {
                startHour++;
            } else if(current == ':') {
                endHour = i;
                break;
            } else if(current < '0' || current > '9') {
                throw new RuntimeException(s + " time is malformed");
            } else {
                foundHour = true;
            }
        }
        int hourLength = endHour - startHour;
        if(hourLength <= 0 || hourLength > 2)
            throw new RuntimeException(s + " time is malformed");

        char last1 = s.charAt(s.length() - 2);
        char last0 = s.charAt(s.length() - 1);
        boolean isAM;
        validAMPM:
        {
            if(last0 == 'M' || last0 == 'm') {
                if(last1 == 'A' || last1 == 'a') {
                    isAM = true;
                    break validAMPM;
                } else if(last1 == 'P' || last1 == 'p') {
                    isAM = false;
                    break validAMPM;
                }
            }
            throw new RuntimeException(s + " time is not in 12 hour format");
        }

        int resultLength = s.length() - 2; // remove AM/PM
        boolean addOne = endHour == 1 && !isAM;

        if(addOne) {
            // 7:00:00PM->, 19:00:00.
            resultLength++;
        }

        // copy all of s into our result, aside from the AM/PM portion
        char[] result = new char[resultLength];
        s.getChars(0, s.length() - 2, result, addOne ? 1 : 0);

        char militaryHour1;
        char militaryHour10;
        if(hourLength == 1) {
            // check that the time is between 1..9
            char hour = s.charAt(startHour);
            if(hour < '1' || hour > '9')
                throw new RuntimeException(s + " time is not in 12 hour format");
            if(isAM) {
                // add 0 to beginning
                militaryHour10 = '0';
                militaryHour1 = hour;
            } else {
                if(hour < '8') {
                    // add 1 to beginning
                    militaryHour10 = '1';
                    militaryHour1 = (char)(hour + 2);
                } else {
                    // add 2 to beginning
                    militaryHour10 = '2';
                    militaryHour1 = (char)(hour - 8);
                }
            }
        } else {
            // hour length is 2. Then the first digit must be 1, second digit must be 0..2
            char hour10 = s.charAt(startHour);
            char hour1 = s.charAt(startHour + 1);
            if(hour10 != '1' || hour1 < '0' || hour1 > '2') {
                throw new RuntimeException(s + " time is not in 12 hour format");
            }

            if(isAM && hour1 == '2') {
                // implies time is 12am. Thus, 00 hour
                militaryHour10 = '0';
                militaryHour1 = '0';
            } else if(!isAM && hour1 != '2') {
                // pm and 2 digit hour
                // first digit is 2
                // second digit
                militaryHour1 = (char)(hour1 + 2);
                militaryHour10 = '2';
            } else {
                // hour stays the same.
                militaryHour10 = hour10;
                militaryHour1 = hour1;
            }
        }
        // start from the next position if we added a digit at the beginning
        if(addOne)
            endHour++;
        result[endHour - 1] = militaryHour1;
        if(endHour - 2 >= 0) {
            result[endHour - 2] = militaryHour10;
        }
        return new String(result);
    }

    public static void main(String[] args) {
        System.out.println(timeConversion("1::am"));
        System.out.println(timeConversion("12:01:35am"));
        System.out.println(timeConversion("1:00:00am"));
        System.out.println(timeConversion("2:00:00am"));
        System.out.println(timeConversion("3:00:00am"));
        System.out.println(timeConversion("4:00:00am"));
        System.out.println(timeConversion("5:00:00am"));
        System.out.println(timeConversion("6:00:00am"));
        System.out.println(timeConversion("7:00:00am"));
        System.out.println(timeConversion("8:00:00am"));
        System.out.println(timeConversion("9:00:00am"));
        System.out.println(timeConversion("10:01:35am"));
        System.out.println(timeConversion("11:01:35am"));
        System.out.println(timeConversion("12:01:35pm"));
        System.out.println(timeConversion("1:01:35pm"));
        System.out.println(timeConversion("2:01:35pm"));
        System.out.println(timeConversion("3:01:35pm"));
        System.out.println(timeConversion("4:01:35pm"));
        System.out.println(timeConversion("5:01:35pm"));
        System.out.println(timeConversion("6:01:35pm"));
        System.out.println(timeConversion("7:01:35pm"));
        System.out.println(timeConversion("8:01:35pm"));
        System.out.println(timeConversion("9:01:35pm"));
        System.out.println(timeConversion("10:01:35pm"));
        System.out.println(timeConversion("11:01:35pm"));
        System.out.println();
        System.out.println(timeConversion("0012:01:3500am"));
        System.out.println(timeConversion("001:00:0000am"));
        System.out.println(timeConversion("002:00:0000am"));
        System.out.println(timeConversion("003:00:0001am"));
        System.out.println(timeConversion("004:00:00am"));
        System.out.println(timeConversion("005:00:00am"));
        System.out.println(timeConversion("006:00:00am"));
        System.out.println(timeConversion("007:00:00am"));
        System.out.println(timeConversion("008:00:00am"));
        System.out.println(timeConversion("009:00:00am"));
        System.out.println(timeConversion("0010:01:35am"));
        System.out.println(timeConversion("0011:01:35am"));
        System.out.println(timeConversion("0012:01:35pm"));
        System.out.println(timeConversion("001:01:35pm"));
        System.out.println(timeConversion("002:01:35pm"));
        System.out.println(timeConversion("003:01:35pm"));
        System.out.println(timeConversion("004:01:35pm"));
        System.out.println(timeConversion("005:01:35pm"));
        System.out.println(timeConversion("006:01:35pm"));
        System.out.println(timeConversion("007:01:35pm"));
        System.out.println(timeConversion("008:01:35pm"));
        System.out.println(timeConversion("009:01:35pm"));
        System.out.println(timeConversion("0010:01:35pm"));
        System.out.println(timeConversion("0011:01:35pm"));
    }
}
