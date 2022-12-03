import ain.render.Renderable
import mesh.IndicesVBO
import mesh.Mesh
import mesh.MeshBuilder
import mesh.MeshFactory
import org.joml.Matrix4f
import org.lwjgl.opengl.GL20.*
import rp.MeshRenderer
import rp.RenderPipeline
import shader.Shader
import kotlin.math.tan


class TestShader : Shader(
    """
        uniform mat4 projectionMatrix;
        uniform mat4 transformationMatrix;
        
#section VERTEX_SHADER

layout (location = 0) in vec3 position;

void main() {
gl_Position = projectionMatrix * transformationMatrix * vec4(position, 1.0);
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

const val FOV = 70f
const val NEAR_PLANE = 0.1f
const val FAR_PLANE = 1000f
private lateinit var projectionMatrix: Matrix4f

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

class Chunk : Renderable() {
    override fun rebuild() {
//        getBuilder("first").drawQuad(
//            Vertex(0f, 0f, -1f, 0f, 0f, null, 0f, 0f, 0f),
//            Vertex(1f, 0f, -1f, 0f, 0f, null, 0f, 0f, 0f),
//            Vertex(0f, 1f, -1f, 0f, 0f, null, 0f, 0f, 0f),
//            Vertex(1f, 1f, -1f, 0f, 0f, null, 0f, 0f, 0f)
//        )

        getBuilder().drawCube(0f, 0f, 0f, 1f, 1f, 1f, true, true, true, true, true, true)

//        getBuilder("second").drawQuad(
//            Vertex(0.2f, 0f, -1f, 0f, 0f, null, 0f, 0f, 0f),
//            Vertex(0.3f, 0f, -1f, 0f, 0f, null, 0f, 0f, 0f),
//            Vertex(0.2f, 1f, -1f, 0f, 0f, null, 0f, 0f, 0f),
//            Vertex(0.3f, 1f, -1f, 0f, 0f, null, 0f, 0f, 0f)
//        )
    }
}

class TestRenderPipeline : RenderPipeline(TestShader(), TestMeshFactory()) {

    var rotation = 0.0
        set(value) {
            field = if (field + value > 360) {
                0.0
            } else {
                value
            }
        }

    override fun render(mesh: Mesh) {
        shader.start()

        shader.loadProjectionMatrix(projectionMatrix)
        rotation += 1.5
        shader.loadTransformationMatrix(Matrix4f().identity().setTranslation(0f, 0f, -5f).rotateX(Math.toRadians(rotation).toFloat()).rotateY(Math.toRadians(rotation).toFloat()).rotateZ(Math.toRadians(rotation).toFloat()))

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

    glEnable(GL_DEPTH_TEST);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    val chunk = Chunk()
    val renderer = MeshRenderer<Chunk>(TestRenderPipeline())
    chunk.markDirty()

    val aspectRatio: Float = window.width.toFloat() / window.height.toFloat()
    val yScale = (1f / tan(Math.toRadians((FOV / 2f).toDouble())) * aspectRatio).toFloat()
    val xScale = yScale / aspectRatio
    val frustumLength: Float = FAR_PLANE - NEAR_PLANE

    projectionMatrix = Matrix4f()
    projectionMatrix.m00(xScale)
    projectionMatrix.m11(yScale)
    projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustumLength))
    projectionMatrix.m23(-1f)
    projectionMatrix.m32(-(2 * NEAR_PLANE * FAR_PLANE / frustumLength))
    projectionMatrix.m33(0f)

    while (!window.shouldClose) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        renderer.render(chunk)

        window.update()
    }
}