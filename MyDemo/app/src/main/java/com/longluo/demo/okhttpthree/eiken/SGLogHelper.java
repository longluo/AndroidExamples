package com.longluo.demo.okhttpthree.eiken;

import android.util.Log;

import java.util.Arrays;


public class SGLogHelper {

    private static final String LOG_TAG_WEB_API = "STUDY_GEAR_WEBAPI";
    private static final String LOG_TAG_CHARGE = "STUDY_GEAR_CHARGE";
    private static final String LOG_TAG_APP = "STUDY_GEAR_APP";

    private static final int ENTRY_MAX_LEN = 4000;

    /**
     * コンストラクタをprivateに
     */
    private SGLogHelper() {
    }

    // **************************************************************
    // WebAPI出力
    // **************************************************************

    /**
     * APIからのログを出力する。API用。Infoのみ。
     *
     * @param message メッセージ
     */
    public static void api(String message) {
        output(Log.DEBUG, LOG_TAG_WEB_API, message, null);
    }

    /**
     * APIからのログを出力する。API用。Warning。
     *
     * @param message   メッセージ
     * @param throwable 例外
     */
    public static void apiWarn(String message, Throwable throwable) {
        output(Log.WARN, LOG_TAG_WEB_API, message, throwable);
    }

    /**
     * APIからのログを出力する。API用。Error。
     *
     * @param message
     */
    public static void apiError(String message) {
        output(Log.ERROR, LOG_TAG_WEB_API, message, null);
    }

    /**
     * APIからのログを出力する。API用。Error。
     *
     * @param message   メッセージ
     * @param throwable 例外
     */
    public static void apiError(String message, Throwable throwable) {
        output(Log.ERROR, LOG_TAG_WEB_API, message, throwable);
    }

    // **************************************************************
    // 課金出力
    // **************************************************************

    /**
     * 課金ログを出力する。Info。
     *
     * @param message メッセージ
     */
    public static void charge(String message) {
        output(Log.DEBUG, LOG_TAG_CHARGE, message, null);
    }

    /**
     * 課金ログを出力する。Error。
     *
     * @param message メッセージ
     */
    public static void chargeError(String message) {
        output(Log.DEBUG, LOG_TAG_CHARGE, message, null);
    }

    /**
     * 課金ログを出力する。Error。
     *
     * @param message   メッセージ
     * @param throwable 例外
     */
    public static void chargeError(String message, Throwable throwable) {
        output(Log.ERROR, LOG_TAG_CHARGE, message, throwable);
    }

    // **************************************************************
    // Verbose出力
    // **************************************************************

    /**
     * アプリからのログを出力する。Verbose。
     *
     * @param message メッセージ
     */
    public static void v(String message) {
        output(Log.VERBOSE, LOG_TAG_APP, message, null);
    }

    /**
     * アプリからのログを出力する。Verbose。
     *
     * @param message   メッセージ
     * @param throwable 例外
     */
    public static void v(String message, Throwable throwable) {
        output(Log.VERBOSE, LOG_TAG_APP, message, throwable);
    }

    // **************************************************************
    // デバッグ出力
    // **************************************************************

    /**
     * アプリからのログを出力する。Debug。
     *
     * @param message メッセージ
     */
    public static void d(String message) {
        output(Log.DEBUG, LOG_TAG_APP, message, null);
    }

    /**
     * アプリからのログを出力する。Debug。
     *
     * @param message   メッセージ
     * @param throwable 例外
     */
    public static void d(String message, Throwable throwable) {
        output(Log.DEBUG, LOG_TAG_APP, message, throwable);
    }

    // **************************************************************
    // Info出力
    // **************************************************************

    /**
     * アプリからのログを出力する。Info。
     *
     * @param message メッセージ
     */
    public static void i(String message) {
        output(Log.INFO, LOG_TAG_APP, message, null);
    }

    /**
     * アプリからのログを出力する。Info。
     *
     * @param message   メッセージ
     * @param throwable 例外
     */
    public static void i(String message, Throwable throwable) {
        output(Log.INFO, LOG_TAG_APP, message, throwable);
    }

    // **************************************************************
    // 警告出力
    // **************************************************************

    /**
     * アプリからのログを出力する。Warning。
     *
     * @param message メッセージ
     */
    public static void w(String message) {
        output(Log.WARN, LOG_TAG_APP, message, null);
    }

    /**
     * アプリからのログを出力する。Warning。
     *
     * @param message   メッセージ
     * @param throwable 例外
     */
    public static void w(String message, Throwable throwable) {
        output(Log.WARN, LOG_TAG_APP, message, throwable);
    }

    // **************************************************************
    // エラー出力
    // **************************************************************

    /**
     * アプリからのログを出力する。Error。
     *
     * @param message メッセージ
     */
    public static void e(String message) {
        output(Log.ERROR, LOG_TAG_APP, message, null);
    }

    /**
     * アプリからのログを出力する。Error。
     *
     * @param message   メッセージ
     * @param throwable 例外
     */
    public static void e(String message, Throwable throwable) {
        output(Log.ERROR, LOG_TAG_APP, message, throwable);
    }

    // **************************************************************
    // 共通
    // **************************************************************

    /**
     * ログを出力する。
     *
     * @param type      ログの種類
     * @param tag       タグ
     * @param message   メッセージ、null可
     * @param throwable 例外、null可
     */
    private static void output(int type, String tag, String message, Throwable throwable) {

        if (message == null) {
            message = getStackTraceInfo();
        } else {
            message = getStackTraceInfo() + message;
        }

        switch (type) {
            case Log.VERBOSE:
                if (throwable == null) {
                    log(Log.VERBOSE, true, message, tag);
                } else {
                    log(Log.VERBOSE, true, message, tag, throwable);
                }
                break;

            case Log.DEBUG:
                if (throwable == null) {
                    log(Log.DEBUG, true, message, tag);
                } else {
                    log(Log.DEBUG, true, message, tag, throwable);
                }
                break;

            case Log.INFO:
                if (throwable == null) {
                    log(Log.INFO, true, message, tag);
                } else {
                    log(Log.INFO, true, message, tag, throwable);
                }
                break;

            case Log.WARN:
                if (throwable == null) {
                    log(Log.WARN, true, message, tag);
                } else {
                    log(Log.WARN, true, message, tag, throwable);
                }
                break;

            case Log.ERROR:
                if (throwable == null) {
                    log(Log.ERROR, true, message, tag);
                } else {
                    log(Log.ERROR, true, message, tag, throwable);
                }
                break;
        }
    }

    /**
     * スタックトレースから呼び出し元の基本情報を取得。出力例、"<< className # methodName: lineNumber >>"
     *
     * @return クラス、メソッド、ライン数のString情報
     */
    private static String getStackTraceInfo() {
        // 現在のスタックトレースを取得。
        // 0:VM 1:スレッド 2:getStackTraceInfo() 3:output() 4:logDebug()等 5:呼び出し元
        StackTraceElement element = Thread.currentThread().getStackTrace()[5];

        String fullName = element.getClassName();
        String className = fullName.substring(fullName.lastIndexOf(".") + 1);
        String methodName = element.getMethodName();
        int lineNumber = element.getLineNumber();

        return "<< " + className + " # " + methodName + " : " + lineNumber + " >> ";
    }


    // **********************************************************************
    //
    // **********************************************************************

    private static void log(int priority, boolean ignoreLimit, String message, String tag, Object... args) {
        String print;
        if (args != null && args.length > 0 && args[args.length - 1] instanceof Throwable) {
            Object[] truncated = Arrays.copyOf(args, args.length - 1);
            Throwable ex = (Throwable) args[args.length - 1];
            print = formatMessage(message, truncated) + '\n' + Log.getStackTraceString(ex);
        } else {
            print = formatMessage(message, args);
        }
        if (ignoreLimit) {
            while (!print.isEmpty()) {
                int lastNewLine = print.lastIndexOf('\n', ENTRY_MAX_LEN);
                int nextEnd = lastNewLine != -1 ? lastNewLine : Math.min(ENTRY_MAX_LEN, print.length());
                String next = print.substring(0, nextEnd /*exclusive*/);
                Log.println(priority, tag, next);
                if (lastNewLine != -1) {
                    // Don't print out the \n twice.
                    print = print.substring(nextEnd + 1);
                } else {
                    print = print.substring(nextEnd);
                }
            }
        } else {
            Log.println(priority, tag, print);
        }
    }

    private static String formatMessage(String message, Object... args) {
        String formatted;
        try {
            /*
             * {} is used by SLF4J so keep it compatible with that as it's easy to forget to use %s when you are
             * switching back and forth between server and client code.
             */
            formatted = String.format(message.replaceAll("\\{\\}", "%s"), args);
        } catch (Exception ex) {
            formatted = message + Arrays.toString(args);
        }
        return formatted;
    }
}
