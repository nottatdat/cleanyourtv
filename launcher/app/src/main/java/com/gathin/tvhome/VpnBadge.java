package com.gathin.tvhome;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;

/** The VPN capsule after the network name: green with the VPN app's name while it is up, red while it is down. */
final class VpnBadge extends ReplacementSpan {

    private static final int GREEN = 0xFF30D158;
    private static final int INK = 0xFF06290F;
    private static final int RED = 0xFFFF453A;
    private static final int INK_OFF = 0xFFFFFFFF;

    private final float density;
    private final int fill, ink;
    private final RectF rect = new RectF();

    /** @param up true for the green "connected" badge, false for the red "disconnected" one */
    VpnBadge(boolean up, float density) {
        this.fill = up ? GREEN : RED;
        this.ink = up ? INK : INK_OFF;
        this.density = density;
    }

    private float textSize(Paint p) {
        return p.getTextSize() * 0.78f;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        float old = paint.getTextSize();
        paint.setTextSize(textSize(paint));
        float w = paint.measureText(text, start, end);
        paint.setTextSize(old);
        // margin + left pad + text + right pad
        return (int) (8 * density + 7 * density + w + 9 * density);
    }

    @Override
    public void draw(@NonNull Canvas c, CharSequence text, int start, int end, float x, int top, int y, int bottom,
                     @NonNull Paint paint) {
        float base = paint.getTextSize();
        float h = base * 1.3f;
        float cy = (top + bottom) / 2f;
        float left = x + 8 * density;
        float right = x + getSize(paint, text, start, end, null);
        rect.set(left, cy - h / 2f, right, cy + h / 2f);

        int oldColor = paint.getColor();
        paint.setColor(fill);
        c.drawRoundRect(rect, h / 2f, h / 2f, paint);

        paint.setColor(ink);
        paint.setTextSize(textSize(paint));
        Paint.FontMetrics fm = paint.getFontMetrics();
        float ty = cy - (fm.ascent + fm.descent) / 2f;
        c.drawText(text, start, end, left + 7 * density, ty, paint);
        paint.setTextSize(base);
        paint.setColor(oldColor);
    }
}
