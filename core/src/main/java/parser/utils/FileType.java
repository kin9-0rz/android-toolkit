package parser.utils;

/**
 * 文件类型定义
 *
 * @author lai
 */
public enum FileType {
    /**
     * CAD.
     */
    DWG("41433130"),

    /**
     * Adobe Photoshop.
     */
    PSD("38425053"),

    /**
     * Rich Text Format.
     */
    RTF("7B5C727466"),

    /**
     * XML.
     */
    XML("3C3F786D6C"),

    /**
     * HTML.
     */
    HTML("68746D6C3E"),

    /**
     * Email [thorough only].
     */
    EML("44656C69766572792D646174653A"),

    /**
     * Outlook Express.
     */
    DBX("CFAD12FEC5FD746F"),

    /**
     * Outlook (pst).
     */
    PST("2142444E"),

    /**
     * MS Word/Excel.
     */
    XLS_DOC("D0CF11E0"),

    /**
     * MS Access.
     */
    MDB("5374616E64617264204A"),

    /**
     * WordPerfect.
     */
    WPD("FF575043"),

    /**
     * Postscript.
     */
    EPS("252150532D41646F6265"),

    //   Adobe Acrobat.
    PDF("255044462D312E"),

    //   Quicken
    QDF("AC9EBD8F"),

    // Windows Password.
    PWL("E3828596"),

    //   MIDI
    MID("4D546864"),

    // ELF 32-bit LSB  shared object
    ELF_LSB_SHARED_OBJECT("7F454C460101010000000000000000000300280001000000"),

    // ELF 32-bit LSB  executable, ARM
    ELF_LSB_EXECUTABLE("7F454C46010101000000000000000000020028000100000"),

    // SQLite 3.x database
    SQLite_3X_DATABASE("53514C69746520666F726D6174203300"),


    // --------------------------------------------- Fonts File  -----------------------------------------------------

    // TrueType font data
    FONTS_FILE_TTC("747463660001"),

    FONTS_FILE_TTF("0001000000"),

    // --------------------------------------------- Symbian File  -----------------------------------------------------

    // Symbian package
    SIS("7A1A2010"),

    // Symbian Executable File
    SIS_EXE("7A000010"),

    // --------------------------------------------- Android File  -----------------------------------------------------

    //  Android binary XML
    ANDROID_BINARY_XML("03000800"),

    // Android Dalvik Executable File
    DEX("6465780A30333500"),

    // Android Dalvik Package File
    APK("504B0304140008000800"),

    // resources.arsc : a file containing pre-compiled resources
    ANDROID_ARSC("02000C00"),

    // Android .RSA
    RSA("30820"),




    // ------------------------------------------- Video data  ----------------------------------------------------

    // Macromedia Flash data
    SWF("4357530E"),

    // Real Media.
    RM("2E524D46"),

    // MPEG (mpg).
    MPG("000001BA"),

    // Quicktime
    MOV("6D6F6F76"),

    // AVI
    AVI("41564920"),

    //  Windows Media.
    ASF("3026B2758E66CF11"),

    // ------------------------------------------- compressed data  ----------------------------------------------------

    // ZIP Archive.
    ZIP("504B0304"),

    // RAR Archive.
    RAR("52617221"),

    // gzip compressed data, from Unix
    GZIP("1F8B0800"),


    // -------------------------------------------    image file    ----------------------------------------------------

    // JEPG
    JPEG("FFD8FF"),

    // PNG
    PNG("89504E47"),

    // GIF
    GIF("47494638"),

    // TIFF
    TIFF("49492A00"),

    // Windows Bitmap.
    BMP("424D"),


    // -------------------------------------------    Audio file    ----------------------------------------------------

    // Real Audio.
    RAM("2E7261FD"),

    // Ogg data, Vorbis audio, stereo, 44100 Hz
    OGG("4F676753"),

    // RIFF (little-endian) data, WAVE audio, Microsoft PCM, 16 bit, mono 44100 Hz
    WAVE_AUDIO_MICROSOFT_PCM("52494646"),

    // MPEG ADTS, layer III, v1,  40 kbps, 32 kHz, JntStereo
    MPEG_ADTS_LAYER_III_V1_40("FFFB2"),

    // MPEG ADTS, layer III, v1,  48 kbps, 32 kHz, JntStereo
    MPEG_ADTS_LAYER_III_V1_48("FFFB3"),

    // MPEG ADTS, layer III, v1, 128 kbps, 44.1 kHz, JntStereo
    MPEG_ADTS_LAYER_III_V1_128_J1("FFFB90"),

    // MPEG ADTS, layer III, v1, 128 kbps, 44.1 kHz
    MPEG_ADTS_LAYER_III_V1_128_1("FFFA9"),

    // MPEG ADTS, layer III, v1, 128 kbps, 44.1 kHz
    MPEG_ADTS_LAYER_III_V1_128_2("FFFB9"),

    //

    // Audio file with ID3 version 2.3.0
    AUDIO_FILE_WITH_ID3_VERSION_23("4944330300000000");

    // Audio file with ID3 version 2.3.0, contains: MPEG ADTS, layer III, v1,  48 kbps, 44.1 kHz, Monaural
//    AUDIO_FILE_48("4944330300000000001054434F4E00000006000000"),
//
//    // Audio file with ID3 version 2.3.0, contains: MPEG ADTS, layer III, v1, 112 kbps, 44.1 kHz, JntStereo
//    AUDIO_FILE_112("4944330300000000010554434F4E00000006000000");


    // -------------------------------------------   END    ----------------------------------------------------
    //File Type value
    private String value = "";


    /**
     * Constructor.
     *
     * @param value type value
     */
    private FileType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}