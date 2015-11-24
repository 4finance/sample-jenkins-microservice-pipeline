package io.fourfinanceit.pipeline.example.common

/**
 * @author Szymon Homa
 */
enum FastForwardMode {
    FF('FF'), FF_ONLY('FF-ONLY'), NO_FF('NO-FF')

    String mode

    FastForwardMode(String mode) {
        this.mode = mode
    }
}