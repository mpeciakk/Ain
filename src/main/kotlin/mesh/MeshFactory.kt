package mesh

import org.lwjgl.BufferUtils
import java.nio.FloatBuffer
import java.nio.IntBuffer

abstract class MeshFactory {
    abstract fun processMesh(meshBuilder: MeshBuilder, mesh: Mesh): Mesh

    fun processMesh(meshBuilder: MeshBuilder): Mesh {
        return processMesh(meshBuilder, Mesh())
    }

    companion object {
        fun getIntBuffer(data: IntArray): IntBuffer {
            return BufferUtils.createIntBuffer(data.size).put(data).flip()
        }

        fun getFloatBuffer(data: FloatArray): FloatBuffer {
            return BufferUtils.createFloatBuffer(data.size).put(data).flip()
        }
    }
}