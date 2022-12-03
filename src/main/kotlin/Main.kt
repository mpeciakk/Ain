import ain.render.Renderable
import mesh.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.*
import rp.MeshRenderer
import rp.RenderPipeline
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

//class Chunk : SingleMeshHolder() {
//    override fun rebuild() {
//        builder.drawQuad(
//            Vertex(0f, 0f, 0f, 0f, 0f, null, 0f, 0f, 0f),
//            Vertex(1f, 0f, 0f, 0f, 0f, null, 0f, 0f, 0f),
//            Vertex(0f, 1f, 0f, 0f, 0f, null, 0f, 0f, 0f),
//            Vertex(1f, 1f, 0f, 0f, 0f, null, 0f, 0f, 0f)
//        )
//    }
//}

class Chunk : Renderable() {
    override fun rebuild() {
        getBuilder("first").drawQuad(
            Vertex(0f, 0f, 0f, 0f, 0f, null, 0f, 0f, 0f),
            Vertex(0.1f, 0f, 0f, 0f, 0f, null, 0f, 0f, 0f),
            Vertex(0f, 1f, 0f, 0f, 0f, null, 0f, 0f, 0f),
            Vertex(0.1f, 1f, 0f, 0f, 0f, null, 0f, 0f, 0f)
        )

        getBuilder("second").drawQuad(
            Vertex(0.2f, 0f, 0f, 0f, 0f, null, 0f, 0f, 0f),
            Vertex(0.3f, 0f, 0f, 0f, 0f, null, 0f, 0f, 0f),
            Vertex(0.2f, 1f, 0f, 0f, 0f, null, 0f, 0f, 0f),
            Vertex(0.3f, 1f, 0f, 0f, 0f, null, 0f, 0f, 0f)
        )
    }
}

class TestRenderPipeline : RenderPipeline(TestShader(), TestMeshFactory()) {
    override fun render(mesh: Mesh) {
        shader.start()

        mesh.bind()
        mesh.vbos.forEach {
            glEnableVertexAttribArray(it.attributeNumber)
        }

        glDrawElements(GL_TRIANGLES, mesh.elementsCount, GL_UNSIGNED_INT, 0);

        mesh.vbos.forEach {
            glDisableVertexAttribArray(it.attributeNumber)
        }
        mesh.unbind()

        glBindBuffer(GL_ARRAY_BUFFER, 0)

        shader.stop()
    }
}

fun main(args: Array<String>) {
    val window = Window(800, 600, "Ain engine")
    window.create()

    val shader = TestShader()
    val factory = TestMeshFactory()

    val chunk = Chunk()
    val renderer = MeshRenderer<Chunk>(TestRenderPipeline())
    chunk.markDirty()

//    val mesh = factory.processMesh(builder)

    while (!window.shouldClose) {
        renderer.render(chunk)

        window.update()
    }
}