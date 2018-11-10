package eu.chainfire.libcfsurface.gl;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class GLHelper {
    private final int mWidth;
    private final int mHeight;
    private final float[] mVMatrix = new float[16];
    
    public GLHelper(int width, int height, float[] vMatrix) {
        mWidth = width;
        mHeight = height;
        setVMatrix(vMatrix);
    }
    
    public void setVMatrix(float[] vMatrix) {
        if (vMatrix != null) System.arraycopy(vMatrix, 0, mVMatrix, 0, Math.min(vMatrix.length, mVMatrix.length));        
    }
    
    public void draw(GLPicture picture, int left, int top, int width, int height) {
        draw(picture, left, top, width, height, GLPicture.AlphaType.IMAGE, 1f);
    }

    public void draw(GLPicture picture, int left, int top, int width, int height, float alpha) {
        draw(picture, left, top, width, height, GLPicture.AlphaType.GLOBAL, alpha);
    }

    public void draw(GLPicture picture, int left, int top, int width, int height, GLPicture.AlphaType alphaType, float alpha) {
        float[] mMatrix = new float[16];
        float[] pMatrix = new float[16];
        float[] mvpMatrix = new float[16];
        
        Matrix.setIdentityM(mMatrix, 0);
        float w = (float)width / (float)mWidth;
        float h = (float)height / (float)mHeight;
        float l = -1.0f + ((2.0f * ((float)left / (float)mWidth)) + w);
        float t = 1.0f - ((2.0f * ((float)top / (float)mHeight)) + h);
        Matrix.translateM(mMatrix, 0, l, t, 0f);
        Matrix.scaleM(mMatrix, 0, w, h, 1.0f);

        Matrix.orthoM(pMatrix, 0, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 10f);
        Matrix.multiplyMM(mvpMatrix, 0, mVMatrix, 0, mMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, pMatrix, 0, mvpMatrix, 0);
        
        picture.draw(mvpMatrix, alphaType, alpha);
    }

    public boolean scissorOn(int left, int top, int width, int height, int maxHeight) {
        boolean scissor = true;
        if (maxHeight != 0) {
            scissor = GLES20.glIsEnabled(GLES20.GL_SCISSOR_TEST);
            if (!scissor) GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
            GLES20.glScissor(left, maxHeight - top - height, width, height);
        }
        return scissor;
    }

    public void scissorOff() {
        GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
    }

    public void scissorOff(boolean scissorOnResult) {
        if (!scissorOnResult) GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
    }
}
