package com.example;

import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.io.IOException;
import java.io.InputStream;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.*;

public class ChipRenderer {
	private int fontTexture;
	private STBTTFontinfo fontInfo;
	private ByteBuffer fontBuffer;
	private STBTTBakedChar.Buffer cdata;
	private float windowAspectRatio;

    public ChipRenderer(float windowAspectRatio) {
        this.windowAspectRatio = windowAspectRatio;
    }

 public void init() {
    try (InputStream is = getClass().getClassLoader().getResourceAsStream("AFSOlive04trial-B.ttf")) {
        if (is == null) {
            throw new IOException("Font file 'AFSOlive04trial-B.ttf' not found in resources");
        }
        byte[] fontData = is.readAllBytes();
        fontBuffer = BufferUtils.createByteBuffer(fontData.length);
        fontBuffer.put(fontData).flip();

        fontInfo = STBTTFontinfo.create();
        if (!STBTruetype.stbtt_InitFont(fontInfo, fontBuffer)) {
            throw new IOException("Failed to initialize font");
        }

        int bitmapWidth = 512;
        int bitmapHeight = 512;
        ByteBuffer bitmap = BufferUtils.createByteBuffer(bitmapWidth * bitmapHeight);
        cdata = STBTTBakedChar.malloc(96);
        STBTruetype.stbtt_BakeFontBitmap(fontBuffer, 32, bitmap, bitmapWidth, bitmapHeight, 32, cdata);

        fontTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, fontTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, bitmapWidth, bitmapHeight, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    } catch (IOException e) {
        throw new RuntimeException("Failed to load font: " + e.getMessage(), e);
    }
}

	public void render(int chips) {
		String chipText = "Chips: " + chips;
		renderText(chipText, -0.95f, -0.95f); // 画面の左下に表示
	}

private void renderText(String text, float x, float y) {
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glEnable(GL_TEXTURE_2D);
    glBindTexture(GL_TEXTURE_2D, fontTexture);

    glBegin(GL_QUADS);
    glColor3f(1.0f, 1.0f, 1.0f);

    float scale = 1.0f / windowAspectRatio;
    try (MemoryStack stack = MemoryStack.stackPush()) {
        FloatBuffer x0 = stack.floats(0);
        FloatBuffer y0 = stack.floats(0);
        STBTTAlignedQuad q = STBTTAlignedQuad.malloc(stack);

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            stbtt_GetBakedQuad(cdata, 512, 512, c - 32, x0, y0, q, true);

            float x1 = x + q.x0() * scale;
            float x2 = x + q.x1() * scale;
            float y1 = y - q.y0() * scale;
            float y2 = y - q.y1() * scale;

            glTexCoord2f(q.s0(), q.t0()); glVertex2f(x1, y1);
            glTexCoord2f(q.s1(), q.t0()); glVertex2f(x2, y1);
            glTexCoord2f(q.s1(), q.t1()); glVertex2f(x2, y2);
            glTexCoord2f(q.s0(), q.t1()); glVertex2f(x1, y2);

            x += (q.x1() - q.x0()) * scale;
        }
    }

    glEnd();
    glDisable(GL_TEXTURE_2D);
    glDisable(GL_BLEND);
}
	public void cleanup() {
		glDeleteTextures(fontTexture);
		cdata.free();
	}
}
