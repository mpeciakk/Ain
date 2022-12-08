package ain.rp

import ain.mesh.Mesh
import ain.mesh.MeshBuilder
import org.joml.Matrix4f

abstract class Renderable : MeshHolder {
    val meshes = mutableMapOf<String, Mesh>()
    val builders = mutableMapOf<String, MeshBuilder>()

    var transformationMatrix: Matrix4f = Matrix4f().identity()

    var state = RenderableState.NONE

    abstract fun rebuild()

    fun markDirty() {
        state = RenderableState.REQUESTED
    }

    protected fun getMesh(): Mesh {
        return getMesh("default")
    }

    protected fun getBuilder(): MeshBuilder {
        return getBuilder("default")
    }

    protected fun getMesh(name: String): Mesh {
        return if (meshes.containsKey(name)) {
            meshes[name]!!
        } else {
            val mesh = Mesh()
            meshes[name] = mesh
            meshes[name]!!
        }
    }

    protected fun getBuilder(name: String): MeshBuilder {
        getMesh(name)

        return if (builders.containsKey(name)) {
            builders[name]!!
        } else {
            val builder = MeshBuilder()
            builders[name] = builder
            builders[name]!!
        }
    }
}