package com.android.jackin.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
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
import android.view.ViewTreeObserver;
import android.widget.TextView;

/**
 * Created by LiuJQ on 2016/05/31
 */
public class ReferenceTextView extends TextView {

    private static final int TRIM_MODE_LINES = 0;
    private static final int TRIM_MODE_LENGTH = 1;
    private static final int DEFAULT_TRIM_LENGTH = 240;
    private static final int DEFAULT_TRIM_LINES = 2;
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
    private int trimMode;
    private int lineEndIndex;
    private int trimLines;

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
        this.trimLines = typedArray.getInt(R.styleable.ReferenceTextView_trimLines, DEFAULT_TRIM_LINES);
        this.colorClickableText = typedArray.getColor(R.styleable.ReferenceTextView_colorClickableText, ContextCompat.getColor(context, R.color.accent));
        this.showTrimExpandedText = typedArray.getBoolean(R.styleable.ReferenceTextView_showTrimExpandedText, DEFAULT_SHOW_TRIM_EXPANDED_TEXT);
        this.colorReference = typedArray.getColor(R.styleable.ReferenceTextView_referenceColor, ContextCompat.getColor(context, R.color.colorPrimary));
        int resourceIdReferenceText = typedArray.getResourceId(R.styleable.ReferenceTextView_referenceContent, R.string.reference);
        this.referenceContent = getResources().getString(resourceIdReferenceText);
        this.trimMode = typedArray.getInt(R.styleable.ReferenceTextView_trimMode, TRIM_MODE_LINES);
        typedArray.recycle();
        viewMoreSpan = new ReadMoreClickableSpan();
        onGlobalLayoutLineEndIndex();
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
        if (trimMode == TRIM_MODE_LENGTH) {
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
        }
        if (trimMode == TRIM_MODE_LINES) {
            if (text != null) {
                if (lineEndIndex > 0) {
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
        }
        return text;
    }

    private CharSequence updateCollapsedText() {
        int trimEndIndex = text.length();
        switch (trimMode) {
            case TRIM_MODE_LINES:
                trimEndIndex = lineEndIndex - (ELLIPSIZE.length() + trimCollapsedText.length() + 1);
                if (trimEndIndex < 0) {
                    trimEndIndex = trimLength + 1;
                }
                break;
            case TRIM_MODE_LENGTH:
                trimEndIndex = trimLength + 1;
                break;
        }
        SpannableString reference = getReferenceContent();
        SpannableStringBuilder s = new SpannableStringBuilder();
        if (reference != null) {
            s.append(reference).append(getResources().getString(R.string.blank_space));
            // refresh trimEndIndex
            // trimEndIndex minus referenceLength
            int referenceLength = reference.length();
            if (trimEndIndex > referenceLength) {
                trimEndIndex -= referenceLength;
            }
        }
        s.append(text.subSequence(0, trimEndIndex)).append(ELLIPSIZE).append(trimCollapsedText);
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

    public void setTrimMode(int trimMode) {
        this.trimMode = trimMode;
    }

    public void setTrimLines(int trimLines) {
        this.trimLines = trimLines;
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

    private void onGlobalLayoutLineEndIndex() {
        if (trimMode == TRIM_MODE_LINES) {
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ViewTreeObserver obs = getViewTreeObserver();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        obs.removeOnGlobalLayoutListener(this);
                    } else {
                        obs.removeGlobalOnLayoutListener(this);
                    }
                    refreshLineEndIndex();
                    setText();
                }
            });
        }
    }

    private void refreshLineEndIndex() {
        try {
            if (trimLines == 0) {
                lineEndIndex = getLayout().getLineEnd(0);
            } else if (trimLines > 0 && getLineCount() >= trimLines) {
                lineEndIndex = getLayout().getLineEnd(trimLines - 1);
            } else {
                lineEndIndex = getLayout().getLineEnd(getLayout().getLineCount() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}