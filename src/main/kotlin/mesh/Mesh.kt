package mesh

import Destroyable
import org.lwjgl.opengl.GL30.*

class Mesh : Destroyable {
    val vao = glGenVertexArrays()
    val vbos = mutableListOf<VBO>()

    var elementsCount = 0

    fun addVbo(vbo: VBO): VBO {
        vbos.add(vbo)
        return vbo
    }

    fun getVbo(attributeNumber: Int, size: Int): VBO {
        for (vbo in vbos) {
            if (vbo.attributeNumber == attributeNumber) {
                return vbo
            }
        }

        return addVbo(VBO(attributeNumber, size))
    }

    override fun destroy() {
        glDeleteVertexArrays(vao)

        for (vbo in vbos) {
            vbo.destroy()
        }
    }

    fun bind() {
        glBindVertexArray(vao)
    }

    fun unbind() {
        glBindVertexArray(0)
    }
}