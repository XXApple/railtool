package com.fengx.rtplayer.utils;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.google.android.exoplayer.util.Util;
import com.fengx.rtplayer.builder.ExtractorRendererBuilder;
import com.fengx.rtplayer.builder.HlsRendererBuilder;
import com.fengx.rtplayer.builder.RendererBuilder;

/**
 * 项目名称：OKPlayer
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：15/11/16 下午10:34
 * 修改人：wengyiming
 * 修改时间：15/11/16 下午10:34
 * 修改备注：
 */
public class RtPlayerUtils {
    public static final String CONTENT_TYPE = "content_type";
    public static final String TYPE = "type";

    public static final String CONTENT_EXT_EXTRA = "type";
    public static final String EXT_DASH = ".mpd";
    public static final String EXT_SS = ".ism";
    public static final String EXT_HLS = ".m3u8";

    public static final int TYPE_DASH = 0;
    public static final int TYPE_SS = 1;
    public static final int TYPE_HLS = 2;
    public static final int TYPE_OTHER = 3;

    /**
     * 如果指明了文件格式,根据格式返回需要解析type
     * 没有指明文件格式,根据uri来猜测是什么视频流
     */
    public static int inferContentType(Uri uri, String fileExtension) {
        String lastPathSegment = !TextUtils.isEmpty(fileExtension) ? "." + fileExtension : uri.getLastPathSegment();
        if (lastPathSegment == null) {
            return TYPE_OTHER;
        } else if (lastPathSegment.endsWith(EXT_DASH)) {
            return TYPE_DASH;
        } else if (lastPathSegment.endsWith(EXT_SS)) {
            return TYPE_SS;
        } else if (lastPathSegment.endsWith(EXT_HLS)) {
            return TYPE_HLS;
        } else {
            return TYPE_OTHER;
        }
    }

    /**
     * 微软 Smooth Streaming
     * MPEG组织 HTTP动态自适应流媒体
     * 苹果的HLS HTTP Live Streaming
     * Adobe的HDS HTTP Dynamic Streaming
     * <p/>
     * 根据contentType,返回具体的渲染器
     */
    public static RendererBuilder getRendererBuilder(Context context, Uri uri, int contentType) {
        String userAgent = Util.getUserAgent(context, "OkPlayer");

        switch (contentType) {
//            case TYPE_SS:
//                return new SmoothStreamingRendererBuilder(this, userAgent, uri.toString(),
//                        new SmoothStreamingTestMediaDrmCallback());
//            case TYPE_DASH:
//                return new DashRendererBuilder(this, userAgent, uri.toString(),
//                        new WidevineTestMediaDrmCallback(contentId));
            case TYPE_HLS:
                return new HlsRendererBuilder(context, userAgent, uri.toString());
            case TYPE_OTHER:
                return new ExtractorRendererBuilder(context, userAgent, uri);
            default:
                throw new IllegalStateException("Unsupported type: " + contentType);
        }
    }
}
