/*
 * eagle_android_gles11_GL11Extention.c
 *
 *  Created on: 2010/07/10
 *      Author: eagle.sakura
 */
#include	<android/log.h>
#include	<math.h>
#include	<GLES/gl.h>
#include	<GLES/glext.h>
#include	"eagle_android_gles11_GL11Extension.h"

/*
 * Class:     eagle_android_gles11_GL11Extention
 * Method:    glMatrixMode
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_eagle_android_gles11_GL11Extension_glMatrixMode(JNIEnv *env, jobject obj, jint i )
{
	glMatrixMode( i );
}

/*
 * Class:     eagle_android_gles11_GL11Extention
 * Method:    glCurrentPaletteMatrixOES
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_eagle_android_gles11_GL11Extension_glCurrentPaletteMatrixOES(JNIEnv *env, jobject obj, jint i )
{
	glCurrentPaletteMatrixOES( i );
}

/*
 * Class:     eagle_android_gles11_GL11Extention
 * Method:    glLoadMatrixx
 * Signature: (Ljava/nio/Buffer;)V
 */
JNIEXPORT void JNICALL Java_eagle_android_gles11_GL11Extension_glLoadMatrixx(JNIEnv *env, jobject obj, jobject buffer )
{
	glLoadMatrixx( ( *env )->GetDirectBufferAddress( env, buffer ) );
}

/*
 * Class:     eagle_android_gles11_GL11Extention
 * Method:    glLoadMatrixf
 * Signature: (Ljava/nio/Buffer;)V
 */
JNIEXPORT void JNICALL Java_eagle_android_gles11_GL11Extension_glLoadMatrixf(JNIEnv *env, jobject obj, jobject buffer)
{
	glLoadMatrixf( ( *env )->GetDirectBufferAddress( env, buffer ) );
}

/*
 * Class:     eagle_android_gles11_GL11Extention
 * Method:    glEnable
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_eagle_android_gles11_GL11Extension_glEnable(JNIEnv *env, jobject obj, jint i)
{
	glEnable( i );
}

/*
 * Class:     eagle_android_gles11_GL11Extention
 * Method:    glEnableClientState
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_eagle_android_gles11_GL11Extension_glEnableClientState(JNIEnv *env, jobject obj, jint i)
{
	glEnableClientState( i );
}

/*
 * Class:     eagle_android_gles11_GL11Extention
 * Method:    glWeightPointerOES
 * Signature: (IIILjava/nio/Buffer;)V
 */
JNIEXPORT void JNICALL Java_eagle_android_gles11_GL11Extension_glWeightPointerOES(JNIEnv *env, jobject obj, jint num, jint type, jint straide, jobject buffer )
{
	glWeightPointerOES( num, type, straide, ( *env )->GetDirectBufferAddress( env, buffer ) );
}

/*
 * Class:     eagle_android_gles11_GL11Extention
 * Method:    glMatrixIndexPointerOES
 * Signature: (IIILjava/nio/Buffer;)V
 */
JNIEXPORT void JNICALL Java_eagle_android_gles11_GL11Extension_glMatrixIndexPointerOES(JNIEnv *env, jobject obj, jint num, jint type, jint straide, jobject buffer)
{
	glMatrixIndexPointerOES( num, type, straide, ( *env )->GetDirectBufferAddress( env, buffer ) );
}

/*
 * Class:     eagle_android_gles11_GL11Extension
 * Method:    glLoadPaletteFromModelViewMatrixOES
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_eagle_android_gles11_GL11Extension_glLoadPaletteFromModelViewMatrixOES(JNIEnv *env, jobject obj)
{
	glLoadPaletteFromModelViewMatrixOES( );
}
