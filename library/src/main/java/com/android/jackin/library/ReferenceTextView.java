package com.android.jackin.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by LiuJQ on 2016/05/31
 */
public class ReferenceTextView extends TextView {

    private static final int DEFAULT_TRIM_LENGTH = 240;
    private static final boolean DEFAULT_SHOW_TRIM_EXPANDED_TEXT = true;
    private static final String ELLIPSIZE = "... ";

    private CharSequence text;
    private BufferType bufferType;
    private boolean readMore = true;
    private int trimLength;
    private CharSequence trimCollapsedText;
    private CharSequence trimExpandedText;
    private ReadMoreClickableSpan viewMoreSpan;
    private int colorClickableText;
    private boolean showTrimExpandedText;

    private int colorReference;
    private String referenceContent;

    public ReferenceTextView(Context context) {
        this(context, null);
    }

    public ReferenceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ReferenceTextView);
        this.trimLength = typedArray.getInt(R.styleable.ReferenceTextView_trimLength, DEFAULT_TRIM_LENGTH);
        int resourceIdTrimCollapsedText = typedArray.getResourceId(R.styleable.ReferenceTextView_trimCollapsedText, R.string.read_more);
        int resourceIdTrimExpandedText = typedArray.getResourceId(R.styleable.ReferenceTextView_trimExpandedText, R.string.read_less);
        this.trimCollapsedText = getResources().getString(resourceIdTrimCollapsedText);
        this.trimExpandedText = getResources().getString(resourceIdTrimExpandedText);
        this.colorClickableText = typedArray.getColor(R.styleable.ReferenceTextView_colorClickableText, ContextCompat.getColor(context, R.color.accent));
        this.showTrimExpandedText = typedArray.getBoolean(R.styleable.ReferenceTextView_showTrimExpandedText, DEFAULT_SHOW_TRIM_EXPANDED_TEXT);
        this.colorReference = typedArray.getColor(R.styleable.ReferenceTextView_referenceColor, ContextCompat.getColor(context, R.color.colorPrimary));
        int resourceIdReferenceText = typedArray.getResourceId(R.styleable.ReferenceTextView_referenceContent, R.string.reference);
        this.referenceContent = getResources().getString(resourceIdReferenceText);
        typedArray.recycle();
        viewMoreSpan = new ReadMoreClickableSpan();
        setText();
    }

    private void setText() {
        super.setText(getDisplayableText(), bufferType);
        setMovementMethod(LinkMovementMethod.getInstance());
        setHighlightColor(Color.TRANSPARENT);
    }

    private CharSequence getDisplayableText() {
        return getTrimmedText(text);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        this.text = text;
        bufferType = type;
        setText();
    }

    private CharSequence getTrimmedText(CharSequence text) {
        if (text != null) {
            if (text.length() > trimLength) {
                if (readMore) {
                    return updateCollapsedText();
                } else {
                    return updateExpandedText();
                }
            } else {
                SpannableString reference = getReferenceContent();
                if (reference != null) {
                    SpannableStringBuilder s = new SpannableStringBuilder();
                    s.append(reference).append(getResources().getString(R.string.blank_space)).append(text);
                    return s;
                }
            }
        }
        return text;
    }

    private CharSequence updateCollapsedText() {
        SpannableString reference = getReferenceContent();
        SpannableStringBuilder s = new SpannableStringBuilder();
        if (reference != null) {
            s.append(reference).append(getResources().getString(R.string.blank_space));
        }
        s.append(text.subSequence(0, trimLength + 1)).append(ELLIPSIZE).append(trimCollapsedText);
        return addClickableSpan(s, trimCollapsedText);
    }

    private CharSequence updateExpandedText() {
        SpannableString reference = getReferenceContent();
        if (showTrimExpandedText) {
            SpannableStringBuilder s = new SpannableStringBuilder();
            if (reference != null) {
                s.append(reference).append(getResources().getString(R.string.blank_space));
            }
            s.append(text.subSequence(0, text.length())).append(trimExpandedText);
            return addClickableSpan(s, trimExpandedText);
        }
        if (reference != null) {
            SpannableStringBuilder s = new SpannableStringBuilder();
            s.append(reference).append(getResources().getString(R.string.blank_space)).append(text);
            return s;
        }
        return text;
    }

    private SpannableString getReferenceContent() {
        if (TextUtils.isEmpty(referenceContent)) return null;
        SpannableString spannableString = new SpannableString(referenceContent);
        spannableString.setSpan(new ForegroundColorSpan(colorReference), 0, referenceContent.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private CharSequence addClickableSpan(SpannableStringBuilder s, CharSequence trimText) {
        s.setSpan(viewMoreSpan, s.length() - trimText.length(), s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }

    public void setTrimLength(int trimLength) {
        this.trimLength = trimLength;
        setText();
    }

    public void setReferenceContent(String referenceContent) {
        this.referenceContent = referenceContent;
        setText();
    }

    public void setColorClickableText(int colorClickableText) {
        this.colorClickableText = colorClickableText;
    }

    public void setTrimCollapsedText(CharSequence trimCollapsedText) {
        this.trimCollapsedText = trimCollapsedText;
    }

    public void setTrimExpandedText(CharSequence trimExpandedText) {
        this.trimExpandedText = trimExpandedText;
    }

    private class ReadMoreClickableSpan extends ClickableSpan {
        @Override
        public void onClick(View widget) {
            readMore = !readMore;
            setText();
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(colorClickableText);
        }
    }
}