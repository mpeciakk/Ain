import gln.*
import org.lwjgl.system.MemoryUtil
import shader.Shader

class TestShader : Shader(
    """
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;

#section VERTEX_SHADER

layout (location = 0) in vec3 position;

void main() {
gl_Position = vec4(position, 1.0);
}

#section FRAGMENT_SHADER

out vec4 out_Color;

void main() {
out_Color = vec4(1, 1, 1, 1);
}
""".trimIndent()
) {
    override fun bindAttributes() {
        bindAttribute(0, "position")
    }
}

fun main(args: Array<String>) {
    val window = Window(800, 600, "Ain engine")
    window.create()

    val vertices = floatArrayOf(
        0.0f, 0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f,
        0.5f, -0.5f, 0.0f
    )

    val vao = gl.genVertexArrays()
    vao.bound {
        val vbo = gl.genBuffers()

        vbo.bound(BufferTarget.ARRAY) {
            data(MemoryUtil.memAllocFloat(vertices.size).put(vertices).flip())
            gl.vertexAttribPointer(0, 3, VertexAttrType.FLOAT, false, 0, 0)
        }
    }

    val shader = TestShader()

    while (!window.shouldClose) {
        window.update()

        gl.clear(ClearBufferMask.COLOR_DEPTH_BUFFER_BIT)
        gl.clearColor(0.0f, 0.0f, 1.0f, 0.0f)

        shader.start()
        vao.bound {
            gl.enableVertexAttribArray(0)

            gl.drawArrays(DrawMode.TRIANGLES, 0, 3)

            gl.disableVertexAttribArray(0)
        }
        shader.stop()
    }
}