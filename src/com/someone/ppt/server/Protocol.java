package com.someone.ppt.server;

interface Protocol {

    public static final String KILL = "[0 ";
    public static final String STATUS_REPORT = "[1 ";
    public static final String AVERAGE = "[2 ";
    public static final String STATISTICS = "[3 ";
    public static final String SEND_DATA = "[4 ";
    public static final String SEND_DATA_PREV = "[5 ";
    public static final String INCOMING_DATA = "[10 ";
    public static final String START_CMS_CFG = "[30 ";
    public static final String START_FTD_DAT = "[31 ";
    public static final String START_FVD_DAT = "[32 ";
    public static final String START_PCD_DAT = "[33 ";
    public static final String START_PPD_DAT = "[34 ";
    public static final String START_SID_DAT = "[35 ";
    public static final String START_CTD_DAT = "[36 ";
    public static final String START_PRT_DAT = "[37 ";
    public static final String START_SFT_DAT = "[38 ";
    public static final String END_CMS_CFG = "[70 ";
    public static final String END_FTD_DAT = "[71 ";
    public static final String END_FVD_DAT = "[72 ";
    public static final String END_PCD_DAT = "[73 ";
    public static final String END_PPD_DAT = "[74 ";
    public static final String END_SID_DAT = "[75 ";
    public static final String END_CTD_DAT = "[76 ";
    public static final String END_PRT_DAT = "[77 ";
    public static final String END_SFT_DAT = "[78 ";
    public static final String DELETE_STORED_DATA = "[81 ";
    public static final String DELETE_SHADOW_DATA = "[82 ";
    public static final String TIME_SYNC = "[92 ";
    public static final String RELOAD_TEST_DATA = "[93 ";
    public static final String ACK = "[95 ";
    public static final String NAK = "[96";
    public static final String LOGGED_OFF = "[98";
    public static final String EOF = "[99";

}
