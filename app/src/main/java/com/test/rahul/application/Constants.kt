package com.test.rahul.application

/**
 * Constants class to hold app wide constants.
 */
object Constants {
    const val DIALING_CODE_FORMAT = "+%d"
    const val DIALING_CODE_FLAG_PREFIX_FORMAT = "%s $DIALING_CODE_FORMAT"
    const val MAX_COLOR_VALUE: Int = 255
}

object DateFormats {
    val formatsAmbiguous = arrayOf(
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
        "yyyy-MM-dd'T'HH:mm:ssZ",
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ",
        "yyyy-MM-dd'T'HH:mm:ssZZZ"
    )
    const val FORMAT_STANDARD_DATE_WITHOUT_MILLI = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    const val FORMAT_DATE_FULL = "dd-MM-yyyy"
    const val FORMAT_DATE_VARIANT = "dd/MM/yyyy"
    const val FORMAT_DATE_FULL_REVERSE = "yyyy-MM-dd"
    const val FORMAT_YEAR_ONLY = "yyyy"
    const val FORMAT_MONTH_ONLY = "MMM"
    const val FORMAT_FULL_MONTH = "MMMM"
    const val FORMAT_DAY_ONLY = "EEE"
    const val FORMAT_FULL_DAY = "EEEE"
    const val FORMAT_DATE_ONLY = "dd"
    const val FORMAT_SINGLE_DATE = "d"
}

object TimeFormats {
    const val FORMAT_MINUTES = "hh:mm a"
}

object AlphabeticChars {
    const val ENGLISH = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z"
    const val ARABIC =
        "ا ب ت ث ج ح خ د ذ ر ز س ش ص ض ط ظ ع غ ف ق ک ل م ن و ہ ی ے"
}
