package ain.render

import mesh.Mesh
import mesh.MeshBuilder
import rp.RenderableState

abstract class Renderable : MeshHolder {
    val meshes = mutableMapOf<String, Mesh>()
    val builders = mutableMapOf<String, MeshBuilder>()

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