import mesh.*
import org.joml.Vector2f
import org.joml.Vector3f
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

class TestMeshFactory : MeshFactory() {
    override fun processMesh(meshBuilder: MeshBuilder, mesh: Mesh): Mesh {
        mesh.bind()

        val vertices = mesh.getVbo(0, 3)
        val indices = mesh.addVbo(IndicesVBO())

        val meshVertices = meshBuilder.vertices
        val meshIndices = meshBuilder.indices

        val verticesData = FloatArray(meshVertices.size * 3)
        val uvsData = FloatArray(meshVertices.size * 2)
        val normalsData = FloatArray(meshVertices.size * 3)
        val indicesData = meshIndices.toTypedArray()

        var verticesIndex = 0
        var uvsIndex = 0

        for (vertex in meshVertices) {
            verticesData[verticesIndex++] = vertex.position.x
            verticesData[verticesIndex++] = vertex.position.y
            verticesData[verticesIndex++] = vertex.position.z
            uvsData[uvsIndex++] = vertex.uvs.x
            uvsData[uvsIndex++] = vertex.uvs.y
        }

        vertices.flush(getFloatBuffer(verticesData))
        indices.flush(getIntBuffer(indicesData.toIntArray()))

        mesh.elementsCount = indicesData.size

        mesh.unbind()

        return mesh
    }
}

fun main(args: Array<String>) {
    val window = Window(800, 600, "Ain engine")
    window.create()

    val shader = TestShader()
    val factory = TestMeshFactory()

    val builder = MeshBuilder()

    builder.drawQuad(
        Vertex(Vector3f(-0.5f, 0.0f, 0.0f), Vector2f(0.0f, 0.0f), null, Vector3f(0.0f, 0.0f, 0.0f)),
        Vertex(Vector3f(0.5f, 0.0f, 0.0f), Vector2f(0.0f, 0.0f), null, Vector3f(0.0f, 0.0f, 0.0f)),
        Vertex(Vector3f(-0.5f, 0.5f, 0.0f), Vector2f(0.0f, 0.0f), null, Vector3f(0.0f, 0.0f, 0.0f)),
        Vertex(Vector3f(0.5f, 0.5f, 0.0f), Vector2f(0.0f, 0.0f), null, Vector3f(0.0f, 0.0f, 0.0f))
    )

    builder.drawQuad(
        Vertex(Vector3f(-0.5f, 0.5f, 0.0f), Vector2f(0.0f, 0.0f), null, Vector3f(0.0f, 0.0f, 0.0f)),
        Vertex(Vector3f(0.5f, 0.5f, 0.0f), Vector2f(0.0f, 0.0f), null, Vector3f(0.0f, 0.0f, 0.0f)),
        Vertex(Vector3f(-0.5f, 1f, 0.0f), Vector2f(0.0f, 0.0f), null, Vector3f(0.0f, 0.0f, 0.0f)),
        Vertex(Vector3f(0.5f, 1f, 0.0f), Vector2f(0.0f, 0.0f), null, Vector3f(0.0f, 0.0f, 0.0f))
    )

    val mesh = factory.processMesh(builder)

    while (!window.shouldClose) {
        shader.start()

        glBindVertexArray(mesh.vao);
        glEnableVertexAttribArray(0);

        glDrawElements(GL_TRIANGLES, mesh.elementsCount, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        shader.stop()

        window.update()
    }
}