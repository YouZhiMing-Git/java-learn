package netty.test.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.internal.ObjectUtil;

import java.util.List;

/**
 * 重写ByteToMessageDecoder继承类
 * 参考DelimiterBasedFrameDecoder
 *
 * @author Adam
 * @Date 2019/10/16
 */
public class MyFieldDecoder extends ByteToMessageDecoder {

    private ByteBuf[] delimiters;
    private int maxFrameLength;
    private boolean stripDelimiter;
    private boolean failFast;
    private boolean discardingTooLongFrame;
    private int tooLongFrameLength;
    private LineBasedFrameDecoder lineBasedDecoder;

    public MyFieldDecoder(int maxFrameLength, ByteBuf delimiter) {
        this(maxFrameLength, true, delimiter);
    }

    public MyFieldDecoder(int maxFrameLength, boolean stripDelimiter, ByteBuf delimiter) {
        this(maxFrameLength, stripDelimiter, true, delimiter);
    }


    public MyFieldDecoder(int maxFrameLength, ByteBuf... delimiters) {
        this(maxFrameLength, true, delimiters);
    }

    public MyFieldDecoder(int maxFrameLength, boolean stripDelimiter, ByteBuf... delimiters) {
        this(maxFrameLength, stripDelimiter, true, delimiters);
    }

    public MyFieldDecoder(int maxFrameLength, boolean stripDelimiter, boolean failFast, ByteBuf... delimiters) {
        validateMaxFrameLength(maxFrameLength);
        if (delimiters == null) {
            throw new NullPointerException("delimiters");
        } else if (delimiters.length == 0) {
            throw new IllegalArgumentException("empty delimiters");
        } else {
            if (isLineBased(delimiters) && !this.isSubclass()) {
                this.lineBasedDecoder = new LineBasedFrameDecoder(maxFrameLength, stripDelimiter, failFast);
                this.delimiters = null;
            } else {
                this.delimiters = new ByteBuf[delimiters.length];

                for (int i = 0; i < delimiters.length; ++i) {
                    ByteBuf d = delimiters[i];
                    validateDelimiter(d);
                    this.delimiters[i] = d.slice(d.readerIndex(), d.readableBytes());
                }

                this.lineBasedDecoder = null;
            }

            this.maxFrameLength = maxFrameLength;
            this.stripDelimiter = stripDelimiter;
            this.failFast = failFast;
        }
    }

    private static boolean isLineBased(ByteBuf[] delimiters) {
        if (delimiters.length != 2) {
            return false;
        } else {
            ByteBuf a = delimiters[0];
            ByteBuf b = delimiters[1];
            if (a.capacity() < b.capacity()) {
                a = delimiters[1];
                b = delimiters[0];
            }

            return a.capacity() == 2 && b.capacity() == 1 && a.getByte(0) == 13 && a.getByte(1) == 10 && b.getByte(0) == 10;
        }
    }

    private boolean isSubclass() {
        return this.getClass() != MyFieldDecoder.class;
    }

    @Override
    protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Object decoded = this.decode(ctx, in);
        if (decoded != null) {
            out.add(decoded);
        }

    }

    protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        int minFrameLength = 2147483647;
        ByteBuf minDelim = null;
        ByteBuf[] var5 = this.delimiters;
        int var6 = var5.length;

        int tooLongFrameLength;
        for (tooLongFrameLength = 0; tooLongFrameLength < var6; ++tooLongFrameLength) {
            ByteBuf delim = var5[tooLongFrameLength];
            int frameLength = indexOf(buffer, delim);
            if (frameLength >= 0 && frameLength < minFrameLength) {
                minFrameLength = frameLength;
                minDelim = delim;
            }
        }

        if (minDelim != null) {
            int minDelimLength = minDelim.capacity();
            if (this.discardingTooLongFrame) {
                this.discardingTooLongFrame = false;
                buffer.skipBytes(minFrameLength + minDelimLength);
                tooLongFrameLength = this.tooLongFrameLength;
                this.tooLongFrameLength = 0;
                if (!this.failFast) {
                    this.fail((long) tooLongFrameLength);
                }

                return null;
            } else if (minFrameLength > this.maxFrameLength) {
                buffer.skipBytes(minFrameLength + minDelimLength);
                this.fail((long) minFrameLength);
                return null;
            } else {
                ByteBuf frame;
                if (this.stripDelimiter) {
                    frame = buffer.readRetainedSlice(minFrameLength);
                    buffer.skipBytes(minDelimLength);
                } else {
                    frame = buffer.readRetainedSlice(minFrameLength + minDelimLength);
                }

                return frame;
            }
        } else {
            if (!this.discardingTooLongFrame) {
                if (buffer.readableBytes() > this.maxFrameLength) {
                    this.tooLongFrameLength = buffer.readableBytes();
                    buffer.skipBytes(buffer.readableBytes());
                    this.discardingTooLongFrame = true;
                    if (this.failFast) {
                        this.fail((long) this.tooLongFrameLength);
                    }
                }
            } else {
                this.tooLongFrameLength += buffer.readableBytes();
                buffer.skipBytes(buffer.readableBytes());
            }

            return null;
        }
    }

    private void fail(long frameLength) {
        if (frameLength > 0L) {
            throw new TooLongFrameException("frame length exceeds " + this.maxFrameLength + ": " + frameLength + " - discarded");
        } else {
            throw new TooLongFrameException("frame length exceeds " + this.maxFrameLength + " - discarding");
        }
    }

    private static int indexOf(ByteBuf haystack, ByteBuf needle) {
        for (int i = haystack.readerIndex(); i < haystack.writerIndex(); ++i) {
            int haystackIndex = i;

            int needleIndex;
            //判断0x7D前一位不是0x5C才按照一个整包截取
            for (needleIndex = 0; needleIndex < needle.capacity() && haystack.getByte(haystackIndex) == needle.getByte(needleIndex)
                    &&
                    (haystack.getByte(haystackIndex - 1) != (byte) 0x5C
                            || (haystackIndex > 2 && haystack.getByte(haystackIndex - 1) == (byte) 0x5C && haystack.getByte(haystackIndex - 2) == (byte) 0x5C) && haystack.getByte(haystackIndex - 3) != (byte) 0x5C)
                    ; ++needleIndex) {
                ++haystackIndex;
                if (haystackIndex == haystack.writerIndex() && needleIndex != needle.capacity() - 1) {
                    return -1;
                }
            }
            if (needleIndex == needle.capacity()) {
                return i - haystack.readerIndex();
            }
        }

        return -1;
    }

    private static void validateDelimiter(ByteBuf delimiter) {
        if (delimiter == null) {
            throw new NullPointerException("delimiter");
        } else if (!delimiter.isReadable()) {
            throw new IllegalArgumentException("empty delimiter");
        }
    }

    private static void validateMaxFrameLength(int maxFrameLength) {
        ObjectUtil.checkPositive(maxFrameLength, "maxFrameLength");
    }
}