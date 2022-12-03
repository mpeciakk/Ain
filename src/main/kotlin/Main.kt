import org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray
import org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.system.MemoryUtil
import shader.Shader
import java.nio.FloatBuffer




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
        -0.5f,  0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f,
        0.5f, -0.5f, 0.0f,
        0.5f,  0.5f, 0.0f,
    )

    val indices = intArrayOf(
        0, 1, 3,
        3, 1, 2,
    )

    val shader = TestShader()

    val verticesBuffer = MemoryUtil.memAllocFloat(vertices.size)
    verticesBuffer.put(vertices).flip()

    val vao = glGenVertexArrays();
    glBindVertexArray(vao);

    val vbo = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vbo);
    glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

    val idxVbo = glGenBuffers();
    val indicesBuffer = MemoryUtil.memAllocInt(indices.size);
    indicesBuffer.put(indices).flip();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVbo);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

    glBindBuffer(GL_ARRAY_BUFFER, 0);

    glBindVertexArray(0);

    while (!window.shouldClose) {
        shader.start()

        glBindVertexArray(vao);
        glEnableVertexAttribArray(0);

        glDrawElements(GL_TRIANGLES, indices.size, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        shader.stop()

        window.update()
    }
}